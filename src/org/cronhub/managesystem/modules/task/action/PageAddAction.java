package org.cronhub.managesystem.modules.task.action;

import java.util.Date;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;

import com.opensymphony.xwork2.ActionSupport;

public class PageAddAction extends ActionSupport {
	private ITaskDao dao;
	private Task task;
	public void setDao(ITaskDao dao) {
		this.dao = dao;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String submitAddForm(){
		task.setUpdate_time(new Date());
		this.dao.insert(task);
		return SUCCESS;
	}

}
