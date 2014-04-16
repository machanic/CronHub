package org.cronhub.managesystem.commons.thrift.process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import net.sf.json.JSONObject;

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
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.cronhub.managesystem.modules.record.undo.dao.IUndoRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;

import com.baofeng.dispatchexecutor.utils.ReplaceRealCmdUtils;


public class RemoteExecutCmdProcessor {
	private SimpleDateFormat returnStrFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
	private String undoReportHttpUrl;
	private IDoneRecordDao doneRecordDao;
	private IUndoRecordDao undoRecordDao;
	private ITaskDao taskDao;
	private SimpleDateFormat sdf = new SimpleDateFormat(ParamCommons.DATE_FORMAT);
	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}
	public void setUndoReportHttpUrl(String undoReportHttpUrl) {
		this.undoReportHttpUrl = undoReportHttpUrl;
	}
	public void setDoneRecordDao(IDoneRecordDao doneRecordDao) {
		this.doneRecordDao = doneRecordDao;
	}
	
	public void setUndoRecordDao(IUndoRecordDao undoRecordDao) {
		this.undoRecordDao = undoRecordDao;
	}
	/***
	 * 此方法表示远程执行的整体流程,比如点击重新执行时,让loading旋转,包括未完成时发送至未完成列表,已完成时存储至已完成列表，并且删除未完成列表
	 * 注意:execType=2"自动重执行"时不会将start_datetime更改，并且自动重执行时会将current_redo_times+1
	 * @param doneRecordId task_record_done的表里的id
	 * @param doneTableName task_record_done的tableName,因为分成每月一张表的结构
	 * @param execType 执行类型:0--crontab执行，1--手动重执行,2--自动重执行,3--当场执行等
	 * @return 被修改过的该task_record_done表的记录的bean
	 * @throws Exception 远程执行因为通信等原因执行失败
	 */
	public TaskRecordDone remoteExecute(Long doneRecordId,String doneTableName,final Integer execType) throws Exception{
		FillConfig fillConfig = new FillConfig(true,true);
		TaskRecordDone record = this.doneRecordDao.findById(doneRecordId, doneTableName, fillConfig);
		final String real_cmd = record.getReal_cmd();
		final Long task_id = record.getTask_id();
		final String ip = record.getTask().getDaemon().getMachine_ip();
		final Integer port = record.getTask().getDaemon().getMachine_port();
		final Boolean delTempFile = record.getTask().getShell_cmd().equals(real_cmd) == true?false:true;
		//设置on_processing属性
		record.setOn_processing(true);
		this.doneRecordDao.update(record);
//		final Long task_id = Long.valueOf(req.getParameter("task_id"));
//		final String real_cmd = req.getParameter("real_cmd");
//		final String ip = req.getParameter("machine_ip");
//		final Integer port = Integer.parseInt(req.getParameter("machine_port"));
		
		//下面三句话是防止远程执行了一半的时候,将唯一标识码放入远端，再通过http post汇报给这里的Params.REPORT_UNDO_IDENTIFIER_ID的Map，最后出异常时从这个Map取出undo_report_id，删去
		final Extra extra = new Extra();
		String report_undo_identifier = UUID.randomUUID().toString();
		extra.setReport_undo_identifier(report_undo_identifier);
		IExecuter executer = new IExecuter(){
			@Override
			public Object execute(Client client) throws Exception {
				return client.executeCmd(real_cmd, task_id, true, undoReportHttpUrl, execType, delTempFile,extra);
			}
//			@Override
//			public String getName() {
//				return String.format("[ip:%s,real_cmd:%s,task_id:%s]:execute by hand",ip,real_cmd,task_id);
//			}
		};
		ExecuteDoneReportResult result = null;
		try {
			result = (ExecuteDoneReportResult) RemoteCaller.call(ip, port, executer, null);
		} catch (Exception e) {
			record.setOn_processing(false);
			record.setExec_type(execType);
			if(!execType.equals(Params.EXECTYPE_AUTOREDO)){
				record.setStart_datetime(new Date());//不等于"自动重执行"类型时,重新设置初始时间
			}else{//如果是"自动重执行"类型时,设置current_redo_times+1
				record.setCurrent_redo_times(record.getCurrent_redo_times()+1);
			}
			record.setEnd_datetime(new Date());
			record.setExit_code(Params.DB_EXITCODE_ERROR_PING); //此行从-1修改为-99,我认为上面的Exception抛出是网络原因造成的
			record.setComplete_success(false);
			this.doneRecordDao.update(record);
			JSONObject errorJson = new JSONObject(); //errorJson供前端ajax显示使用
			errorJson.put("error", Params.PAGE_CONN_LOST_MSG+",服务器ip:"+ip+",端口号:"+port);
			errorJson.put(Params.PAGE_RECORD_COMPLETE_STATS, record.getComplete_success_ISO());
			errorJson.put(Params.PAGE_RECORD_DURATION, record.getDuration_ISO());
			//errorJson.put(Params.PAGE_RECORD_EXIT_CODE, record.getExit_code_ISO());
			errorJson.put(Params.PAGE_RECORD_END_DATETIME,record.getEnd_datetime_ISO());
			errorJson.put(Params.PAGE_RECORD_DATETIME_INTERVAL,record.getDatetime_interval_ISO());
			errorJson.put(Params.PAGE_RECORD_EXEC_TYPE, record.getExec_type_ISO());
			errorJson.put(Params.PAGE_RECORD_EXEC_RETURN_STR, record.getExec_return_str());
			errorJson.put(Params.PAGE_RECORD_START_DATETIME, record.getStart_datetime_ISO());
			errorJson.put("id",doneRecordId);
			
			throw new Exception(errorJson.toString());
		}finally{
			//如果已经汇报到未完成表了,那么Map中必然有,删除之
			if(Params.REPORT_UNDO_IDENTIFIER_ID.containsKey(report_undo_identifier)){
				undoRecordDao.deleteById(Params.REPORT_UNDO_IDENTIFIER_ID.get(report_undo_identifier));
				Params.REPORT_UNDO_IDENTIFIER_ID.remove(report_undo_identifier);
			}
		}
		record.setComplete_success(result.isSuccess());
		record.setExit_code(result.getComplete_status());
		record.setExec_type(execType);
		StringBuilder newReturnStr = new StringBuilder(record.getExec_return_str());
		newReturnStr.append("\n"+returnStrFormat.format(new Date(result.getEnd_datetime()))).append(result.getExec_return_str());
		record.setExec_return_str(newReturnStr.toString());
		record.setEnd_datetime(new Date(result.getEnd_datetime()));
		if(execType != Params.EXECTYPE_AUTOREDO){
			record.setStart_datetime(new Date(result.getStart_datetime()));//不等于自动重执行类型时,设置初始时间
		}else{
			record.setCurrent_redo_times(record.getCurrent_redo_times()+1);//如果是自动重新时,设置current_redo_times+1
		}
		//重置on_processing使其不执行状态,页面上的loading圆圈不再转
		record.setOn_processing(false);
		undoRecordDao.deleteById(result.getTask_record_undo_id());
		this.doneRecordDao.update(record);
		return record;
	}
	
	
	
	/***
	 * 这个方法表示在点击"当场执行按钮"时以及"crontab时"的执行流程,从任务列表中拉出一个taskId去执行,但没有点击"重新执行"时让loading开始旋转的步骤
	 * @param task 任务
	 * @param exec_type 执行类型:0--crontab执行，1--手动重执行,2--自动重执行,3--当场执行等
	 * @return
	 * @throws Exception
	 */
	public boolean remoteExecute(final Task task,final Integer exec_type) throws Exception{
		String machine_ip = task.getDaemon().getMachine_ip();
		int machine_port =task.getDaemon().getMachine_port();
		
		//下面三句话是防止远程执行了一半的时候,将唯一标识码放入远端，再通过http post汇报给这里的Params.REPORT_UNDO_IDENTIFIER_ID的Map，最后出异常时从这个Map取出undo_report_id，删去
		final Extra extra = new Extra();
		String report_undo_identifier = UUID.randomUUID().toString();
		extra.setReport_undo_identifier(report_undo_identifier);
		IExecuter executer = new IExecuter(){
			@Override
			public Object execute(Client client) throws Exception {
				
				return client.executeCmd(task.getShell_cmd(), task.getId(), true, undoReportHttpUrl, exec_type, false,extra);
			}
		};
		ExecuteDoneReportResult result;
		Date startDate = new Date();
		try {
			result = (ExecuteDoneReportResult)RemoteCaller.call(machine_ip, machine_port, executer, null);
		} catch (Exception e) { //修复一个严重的bug，当远程执行的时候，一开始的undoReport表是从远端的daemon程序以http post的方式存入undo表,然后再以方法调用的结果作为返回，如果执行一半网络断掉，这样就无法获得远端的执行结果了
			//修复办法如下:将远端的执行结果在post到undo的action的时候，就存入一个Map中,key为我执行这个方法时产生的UUID随机码
			//如果通信失败，则在本机器上使用shell现场时间替换，并且在本地机器上执行该命令，并记入-99的结果码
			TaskRecordDone record = new TaskRecordDone();
			record.setTask_id(task.getId());
			if(task.getMust_replace_cmd()){//如果通信失败而且task属性标识为必须替换为现场真实的时间,就要在本机使用shell现场时间替换
				record.setReal_cmd(ReplaceRealCmdUtils.replaceCmdFromOriginalToReal(task.getShell_cmd()));
			}else{
				record.setReal_cmd(task.getShell_cmd());
			}
			record.setExit_code(Params.DB_EXITCODE_ERROR_PING);
			record.setComplete_success(false);
			record.setStart_datetime(startDate);//不能这么写，因为有可能干活5小时的中间第3小时执行失败，（网络异常）而发生问题。
			record.setEnd_datetime(new Date());
			record.setExec_type(exec_type);
			record.setExec_return_str("无法ping通daemon端[ip:"+task.getDaemon().getMachine_ip()+",port:"+task.getDaemon().getMachine_port()+"],执行命令:"+task.getShell_cmd()+"的时候无法通信");
			record.setCurrent_redo_times(0);
			record.setOn_processing(false);
			doneRecordDao.insert(record);
			AppLogger.errorLogger.error("error in execute exec_type:"+Params.EXECTYPE_REPRESENT.get(exec_type)+" . ip:"+task.getDaemon().getMachine_ip()+",cmd:"+task.getShell_cmd()+".",e);
			throw new Exception(Params.PAGE_CONN_LOST_MSG+",task_id:"+task.getId()+",服务器ip:"+machine_ip+",端口号:"+machine_port);
		}finally{
			//如果已经汇报到未完成表了,那么Map中必然有,删除之
			if(Params.REPORT_UNDO_IDENTIFIER_ID.containsKey(report_undo_identifier)){
				undoRecordDao.deleteById(Params.REPORT_UNDO_IDENTIFIER_ID.get(report_undo_identifier));
				Params.REPORT_UNDO_IDENTIFIER_ID.remove(report_undo_identifier);
			}
		}
		TaskRecordDone record = new TaskRecordDone();
		record.setTask_id(result.getTask_id());
		if(task.getMust_replace_cmd()){
			record.setReal_cmd(result.getReal_cmd());
		}else{
			record.setReal_cmd(task.getShell_cmd());
		}
		record.setExit_code(result.getComplete_status());
		record.setComplete_success(result.isSuccess());
		record.setStart_datetime(new Date(result.getStart_datetime()));
		record.setEnd_datetime(new Date(result.getEnd_datetime()));
		record.setExec_type(result.getExec_type());
		record.setExec_return_str(result.getExec_return_str());
		record.setCurrent_redo_times(0);
		//重置on_processing使其不执行状态,页面上的loading圆圈不转
		record.setOn_processing(false);
		doneRecordDao.insert(record);
		undoRecordDao.deleteById(result.getTask_record_undo_id());
		AppLogger.recordDoneLogger.info(String.format("execute type:%s done!ip:%s,port:%s.insert to record_done table.[start_datetime:%s,end_datetime:%s].Delete from record_undo table.record_undo_id=%s",Params.EXECTYPE_REPRESENT.get(exec_type),task.getDaemon().getMachine_ip(),task.getDaemon().getMachine_port(),sdf.format(record.getStart_datetime()),sdf.format(record.getEnd_datetime()),result.getTask_record_undo_id()));
		return result.isSuccess();
	}
	/***
	 * 这个方法表示在点击"当场执行按钮"时的执行流程,从任务列表中拉出一个taskId去执行,但没有点击"重新执行"时让loading开始旋转的步骤
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public boolean remoteExecuteOnSpot(final Long taskId) throws Exception{
		FillConfig config = new FillConfig(true,true);
		Task task = this.taskDao.findById(taskId,config);
		return remoteExecute(task,Params.EXECTYPE_SPOT);
	}
}
