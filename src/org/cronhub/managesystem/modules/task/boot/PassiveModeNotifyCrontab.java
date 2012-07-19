package org.cronhub.managesystem.modules.task.boot;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.TaskCollector;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContextEvent;

import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.params.daemon.ParamCommons;
import org.cronhub.managesystem.commons.thrift.call.IExecuter;
import org.cronhub.managesystem.commons.thrift.call.RemoteCaller;
import org.cronhub.managesystem.commons.thrift.gen.ExecuteDoneReportResult;
import org.cronhub.managesystem.commons.thrift.gen.Extra;
import org.cronhub.managesystem.commons.thrift.gen.ExecutorService.Client;
import org.cronhub.managesystem.commons.thrift.process.RemoteExecutCmdProcessor;
import org.cronhub.managesystem.commons.utils.container.WebContainer;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.cronhub.managesystem.modules.record.undo.dao.IUndoRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.springframework.web.context.ContextLoaderListener;

import com.baofeng.dispatchexecutor.utils.ReplaceRealCmdUtils;
/**
 * 被动模式下由task表的记录执行crontab，由crontab远程执行，web容器一启动就会启动这个类里面的contextInitialized方法，里面启动一个新的线程
 * @author dd
 *
 */
public class PassiveModeNotifyCrontab extends ContextLoaderListener{
	private static final String wherePassiveRunMode = " WHERE run_mode = 0 AND is_process_node =0";
	private ITaskDao taskDao;
	private IDoneRecordDao doneRecordDao;
	private IUndoRecordDao undoRecordDao;
	private SimpleDateFormat sdf = new SimpleDateFormat(ParamCommons.DATE_FORMAT);
	private static final FillConfig fillConfig = FillConfig.getFillDaemonInstance();
	private String undoReportHttpUrl;
	
	private RemoteExecutCmdProcessor processor;
	
	private Runnable crontabExecThread = new Runnable(){
		@Override
		public void run() {
			Scheduler sch = new Scheduler();
			sch.addTaskCollector(new TaskCollector(){
				@Override
				public TaskTable getTasks() {
					TaskTable table = new TaskTable();
					List<Task> tasks = taskDao.findAll(wherePassiveRunMode, fillConfig);
					for(final Task task : tasks){
						table.add(new SchedulingPattern(task.getCron_exp()),new it.sauronsoftware.cron4j.Task(){
							@Override
							public void execute(TaskExecutionContext context)
									throws RuntimeException {
								try {
									processor.remoteExecute(task, Params.EXECTYPE_CRONTAB);
								} catch (Exception e) {
									throw new RuntimeException("task is failed while executing task:"+task.getId());
								}
							}
						});
					}
					return table;
				}
			});
			sch.start();
		}
	};
	
	/***
	 * 通知进程，已经作废,被crontabExecThread取代
	 */
	@Deprecated
	private Runnable notifyExecThread = new Runnable(){
		@Override
		public void run() {
			Scheduler sch = new Scheduler();
			sch.addTaskCollector(new TaskCollector(){
				@Override
				public TaskTable getTasks() {
					TaskTable table = new TaskTable();
					List<Task> tasks = taskDao.findAll(wherePassiveRunMode, fillConfig);
					for(final Task task : tasks){
						table.add(new SchedulingPattern(task.getCron_exp()),new it.sauronsoftware.cron4j.Task(){
							@Override
							public void execute(TaskExecutionContext context)
									throws RuntimeException {
								IExecuter executer = new IExecuter(){
									@Override
									public Object execute(Client client)
											throws Exception {
										Extra extra = new Extra();
										String report_undo_identifier = UUID.randomUUID().toString();
										extra.setReport_undo_identifier(report_undo_identifier);
										ExecuteDoneReportResult result = client.executeCmd(task.getShell_cmd(), task.getId(), true, undoReportHttpUrl, Params.EXECTYPE_CRONTAB,false,extra);
										System.out.println("execute cmd:"+task.getShell_cmd()+".result:"+result.getExec_return_str());
										TaskRecordDone record = new TaskRecordDone();
										record.setTask_id(result.getTask_id());
										if(task.getMust_replace_cmd()){
											record.setReal_cmd(result.getReal_cmd());
										}else{
											record.setReal_cmd(task.getShell_cmd());
										}
										//执行完毕，存入数据库执行结果，并且从未完成列表中删除，未完成列表的插入数据的过程发生在com.baofeng.dispatchsystem.modules.record.undo.action.ReportUndoAction类中的reportUndoSave方法
										record.setExit_code(result.getComplete_status());
										record.setComplete_success(result.isSuccess());
										record.setStart_datetime(new Date(result.getStart_datetime()));
										record.setEnd_datetime(new Date(result.getEnd_datetime()));
										record.setExec_type(result.getExec_type());
										record.setExec_return_str(result.getExec_return_str());
										record.setCurrent_redo_times(0);
										record.setOn_processing(false);
										doneRecordDao.insert(record);
										undoRecordDao.deleteById(result.getTask_record_undo_id());
										AppLogger.recordDoneLogger.info(String.format("crontab by active_mode done!ip:%s,port:%s.insert to record_done table.[start_datetime:%s,end_datetime:%s].Delete from record_undo table.record_undo_id=%s",task.getDaemon().getMachine_ip(),task.getDaemon().getMachine_port(),sdf.format(record.getStart_datetime()),sdf.format(record.getEnd_datetime()),result.getTask_record_undo_id()));
										return null;
									}
//									@Override
//									public String getName() {
//										return String.format("Passive run mode crontab executer.task_id:%s",task.getId());
//									}
								};
								Date startDate = new Date();
								try {
									RemoteCaller.call(task.getDaemon().getMachine_ip(),task.getDaemon().getMachine_port(),executer,null);
								} catch (Exception e) {
									//如果通信失败，则在本机器上使用shell现场时间替换，并且在本地机器上执行该命令，并记入-99的结果码
									TaskRecordDone record = new TaskRecordDone();
									record.setTask_id(task.getId());
									record.setReal_cmd(ReplaceRealCmdUtils.replaceCmdFromOriginalToReal(task.getShell_cmd()));
									record.setExit_code(Params.DB_EXITCODE_ERROR_PING);
									record.setComplete_success(false);
									record.setStart_datetime(startDate);//不能这么写，因为有可能干活5小时的中间第3小时执行失败，（网络异常）而发生问题。
									record.setEnd_datetime(new Date());
									record.setExec_type(Params.EXECTYPE_CRONTAB);
									record.setExec_return_str("无法ping通daemon端[ip:"+task.getDaemon().getMachine_ip()+",port:"+task.getDaemon().getMachine_port()+"],执行命令:"+task.getShell_cmd()+"的时候无法通信");
									record.setCurrent_redo_times(0);
									record.setOn_processing(false);
									doneRecordDao.insert(record);
									AppLogger.errorLogger.error("error in execute passtive run mode crontab.ip:"+task.getDaemon().getMachine_ip()+",cmd:"+task.getShell_cmd()+".",e);
								}
							}
						});
					}
					return table;
				}
			});
			sch.start();
		}
	};
	private void startCrontabThread(){
		Thread cronThread = new Thread(this.crontabExecThread);
		cronThread.setDaemon(true);
		cronThread.start();
	}
	@Override
	public void contextInitialized(ServletContextEvent event) {
		PassiveModeNotifyCrontab cron = (PassiveModeNotifyCrontab)WebContainer.getBean("passiveModeNotifyCrontab",event.getServletContext());
		cron.startCrontabThread();
	}


	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}



	public void setDoneRecordDao(IDoneRecordDao doneRecordDao) {
		this.doneRecordDao = doneRecordDao;
	}


	public void setUndoRecordDao(IUndoRecordDao undoRecordDao) {
		this.undoRecordDao = undoRecordDao;
	}
	public void setUndoReportHttpUrl(String undoReportHttpUrl) {
		this.undoReportHttpUrl = undoReportHttpUrl;
	}
	public void setProcessor(RemoteExecutCmdProcessor processor) {
		this.processor = processor;
	}
	}
