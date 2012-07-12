package org.cronhub.managesystem.commons.validation;

import it.sauronsoftware.cron4j.SchedulingPattern;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.logger.AppLogger;

import com.opensymphony.xwork2.ActionSupport;
public class ValidationAction extends ActionSupport {
	public String validateCronExp(){
		String value = ServletActionContext.getRequest().getParameter("cron_exp");
		boolean pass = SchedulingPattern.validate(value);
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			if(pass){
				out.print("success");
			}else{
				out.print("fail");
			}
			out.flush();
		} catch (IOException e) {
			AppLogger.errorLogger.error("error in get PrintWriter to page",e);
		}finally{
			if(null!=out){
			out.close();
			}
		}
		return NONE;
	}
}
