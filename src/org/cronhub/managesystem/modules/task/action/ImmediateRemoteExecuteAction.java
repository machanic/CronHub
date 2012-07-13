package org.cronhub.managesystem.modules.task.action;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.thrift.process.RemoteExecutCmdProcessor;
import org.cronhub.managesystem.commons.utils.PageIOUtils;

import com.opensymphony.xwork2.ActionSupport;

public class ImmediateRemoteExecuteAction extends ActionSupport {
	RemoteExecutCmdProcessor processor;
	
	public void setProcessor(RemoteExecutCmdProcessor processor) {
		this.processor = processor;
	}

	public String remoteExecute(){
		HttpServletRequest req = ServletActionContext.getRequest();
		final Long id = Long.valueOf(req.getParameter("id"));
		boolean success = false;
		JSONObject ajaxJson = new JSONObject(); 
		try {
			success = this.processor.remoteExecuteOnSpot(id);
		} catch (Exception e) {
			ajaxJson.put("error",e.getMessage());
		}
		ajaxJson.put("status", success==true?"success":"fail");
		ajaxJson.put("id",id);
		PageIOUtils.printToPage(ajaxJson.toString());
		return NONE;
	}
}
