package org.cronhub.managesystem.modules.record.undo.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordUndo;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.params.daemon.ParamCommons;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.modules.record.undo.dao.IUndoRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;

import com.opensymphony.xwork2.ActionSupport;
public class ReportUndoAction extends ActionSupport {
	private SimpleDateFormat sdf = new SimpleDateFormat(ParamCommons.DATE_FORMAT);
	private IUndoRecordDao undoRecordDao;
	
	private ITaskDao taskDao;
	
	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}
	/**
	 * deamon奴隶端发来的"我开始执行了"的信息，这就是未完成列表数据插入
	 * @return
	 */
	public String reportUndoSave(){
		HttpServletRequest req = ServletActionContext.getRequest();
		Long task_id = Long.valueOf(req.getParameter(ParamCommons.REPORT_TASK_ID));
		FillConfig config = new FillConfig(false,false);
		Task daoTask = taskDao.findById(task_id,config);
		boolean isRealCmd = true;
		if(daoTask!=null){
			isRealCmd = daoTask.getMust_replace_cmd();
		}
		
		String shell_cmd = req.getParameter(ParamCommons.REPORT_SHELL_CMD);
		String real_cmd = req.getParameter(ParamCommons.REPORT_REAL_CMD);
		String report_undo_identifier = req.getParameter(ParamCommons.REPORT_UNDO_IDENTIFIER);
		Date start_date  = new Date();
//		try {
//			start_date  = sdf.parse(req.getParameter(ParamCommons.REPORT_START_TIME));
//			start_date = snew Date());
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Integer exec_type = Integer.parseInt(req.getParameter(ParamCommons.REPORT_EXEC_TYPE));
		Integer run_status = Integer.parseInt(req.getParameter(ParamCommons.REPORT_RUN_STATUS));
		TaskRecordUndo record = new TaskRecordUndo();
		record.setTask_id(task_id);
		if(isRealCmd){
			record.setReal_cmd(real_cmd);
		}else{
			record.setReal_cmd(shell_cmd);
		}
		record.setRun_status(run_status);
		record.setStart_datetime(start_date);
		record.setExec_type(exec_type);
		Long insertId = undoRecordDao.insert(record);
		Params.REPORT_UNDO_IDENTIFIER_ID.put(report_undo_identifier, insertId);
		AppLogger.recordUndoLogger.info(String.format("report undo save to db:[task_id:%s,real_cmd:%s,run_status:%s,start_date:%s,exec_type:%s]",task_id,real_cmd,run_status,sdf.format(start_date),exec_type));
		JSONObject jsonPrint = new JSONObject();
		jsonPrint.put("task_record_undo_id", insertId);
		PageIOUtils.printToPage(jsonPrint.toString());
		return NONE;
	}

	public void setUndoRecordDao(IUndoRecordDao undoRecordDao) {
		this.undoRecordDao = undoRecordDao;
	}
}
