package org.cronhub.managesystem.modules.record.done.boot;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskCollector;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskTable;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.thrift.process.RemoteExecutCmdProcessor;
import org.cronhub.managesystem.commons.utils.container.WebContainer;
import org.cronhub.managesystem.commons.utils.database.RecordDoneUtils;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.springframework.web.context.ContextLoaderListener;

public class AutoRedoRemoteExec  extends ContextLoaderListener{
	private SchedulingPattern exec_cron_exp = new  SchedulingPattern("*/5 * * * *");
	//没有正在执行的、crontab执行类型的、通信正常的、返回码不是-99的(表示执行时ping不通)、失败的、允许可以自动重执行的、重执行次数小于截止次数的 任务才可自动重执行
	private String allRedoWhereSql = " WHERE on_processing = 0 AND (exec_type = "+Params.EXECTYPE_CRONTAB+" OR exec_type = "+Params.EXECTYPE_AUTOREDO+") AND exit_code!="+Params.DB_EXITCODE_ERROR_PING+" AND conn_status = 1 AND complete_success = 0 AND is_redo = 1 AND current_redo_times < end_redo_times ";
	private RemoteExecutCmdProcessor processor;
	private IDoneRecordDao doneRecordDao;
	
	public void setDoneRecordDao(IDoneRecordDao doneRecordDao) {
		this.doneRecordDao = doneRecordDao;
	}
	public void setProcessor(RemoteExecutCmdProcessor processor) {
		this.processor = processor;
	}
	public void start(){
		Thread autoThread = new Thread(execThread);
		autoThread.setDaemon(true);
		autoThread.start();
	}
	@Override
	public void contextInitialized(ServletContextEvent event) {
		AutoRedoRemoteExec exec = (AutoRedoRemoteExec)WebContainer.getBean("autoRedoRemoteExec",event.getServletContext());
		exec.start();
	}
	
	private Runnable execThread = new Runnable(){
		@Override
		public void run() {
			Scheduler sch = new Scheduler();
			sch.addTaskCollector(new TaskCollector(){
				@Override
				public TaskTable getTasks() {
					TaskTable table = new TaskTable();
					final String tableName = RecordDoneUtils.getTableName(new Date());
					if(!doneRecordDao.hasTableByName(tableName)){ //if not exists this table,return
						return table;
					}
					FillConfig fillConfig =new FillConfig(false,false);
					List<TaskRecordDone> records = doneRecordDao.findAll(tableName,allRedoWhereSql, fillConfig);
					for(final TaskRecordDone record : records){
						table.add(exec_cron_exp, new Task(){
							@Override
							public void execute(TaskExecutionContext context){
							try {
								processor.remoteExecute(record.getId(), tableName, Params.EXECTYPE_AUTOREDO);
							} catch (Exception e) {
								AppLogger.recordDoneErrorLogger.error("自动重执行task_record_done表的记录时失败[表名:"+tableName+",task_record_done的id:"+record.getId(),e);
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
}
