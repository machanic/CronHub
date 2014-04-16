package org.cronhub.managesystem.modules.record.done.action;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.thrift.process.RemoteExecutCmdProcessor;
import org.cronhub.managesystem.commons.utils.PageIOUtils;

import com.opensymphony.xwork2.ActionSupport;

public class RemoteExecuteCallerAction extends ActionSupport{
	private RemoteExecutCmdProcessor processor;
	
	public void setProcessor(RemoteExecutCmdProcessor processor) {
		this.processor = processor;
	}

	public String remoteExecute(){
		HttpServletRequest req = ServletActionContext.getRequest();
		final Long id = Long.valueOf(req.getParameter("id"));
		final String tableName = req.getParameter("tableName");
		TaskRecordDone record = null;
		try {
			record = this.processor.remoteExecute(id,tableName,Params.EXECTYPE_BTNREDO);
		} catch (Exception e) {
			PageIOUtils.printToPage(e.getMessage());
			e.printStackTrace();
		}
		if(record == null){
			return NONE;
		}
		JSONObject ajaxJson = new JSONObject(); 
		ajaxJson.put(Params.PAGE_RECORD_COMPLETE_STATS, record.getComplete_success_ISO());
		ajaxJson.put(Params.PAGE_RECORD_DURATION, record.getDuration_ISO());
		ajaxJson.put(Params.PAGE_RECORD_EXIT_CODE, record.getExit_code_ISO());
		ajaxJson.put(Params.PAGE_RECORD_END_DATETIME,record.getEnd_datetime_ISO());
		ajaxJson.put(Params.PAGE_RECORD_DATETIME_INTERVAL, record.getDatetime_interval_ISO());
		ajaxJson.put(Params.PAGE_RECORD_EXEC_TYPE, record.getExec_type_ISO());
		ajaxJson.put(Params.PAGE_RECORD_EXEC_RETURN_STR, record.getExec_return_str());
		ajaxJson.put(Params.PAGE_RECORD_START_DATETIME, record.getStart_datetime_ISO());
		ajaxJson.put("id",id);//这里把id放回去是为了前端ajax多线程并发的时候(ajax自身在并发的时候回调函数会混乱调错)，ajax回调函数不至于找错行。
		PageIOUtils.printToPage(ajaxJson.toString());
		return NONE;
	}
}
