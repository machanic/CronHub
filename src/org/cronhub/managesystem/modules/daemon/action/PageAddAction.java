package org.cronhub.managesystem.modules.daemon.action;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.dao.bean.Daemon;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.commons.utils.PingUtils;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;

import com.opensymphony.xwork2.ActionSupport;

public class PageAddAction extends ActionSupport {
	private Daemon daemon;
	private IDaemonDao dao;
	
	private String alertMailJson;
	
	
	
	public void setAlertMailJson(String alertMailJson) {
		this.alertMailJson = alertMailJson;
	}

	public void setDao(IDaemonDao dao) {
		this.dao = dao;
	}

	public void setDaemon(Daemon daemon) {
		this.daemon = daemon;
	}

	public Daemon getDaemon() {
		return daemon;
	}

	public String validatePing(){
		String ip = ServletActionContext.getRequest().getParameter("ip");
		int port = Integer.parseInt(ServletActionContext.getRequest().getParameter("port"));
		String pageOut="fail";
       boolean ping = PingUtils.ping(ip, port);
       if(!ping){AppLogger.daemonErrorLogger.error(String.format("validate ip:%s,port:%s cannot ping!",ip,port));}
       else{pageOut="success";}
       PageIOUtils.printToPage(pageOut);
       return NONE;
	}
	public String submitAddForm(){
		daemon.setUpdate_time(new Date());
		this.dao.insert(this.daemon);
		return SUCCESS;
	}
	public String getAlertMail(){
		PageIOUtils.printToPage(this.alertMailJson);
		return NONE;
	}
}
