package org.cronhub.managesystem.modules.task.action;

import java.util.Date;
import java.util.List;

import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.cronhub.managesystem.modules.taskuser.dao.ITaskUserDao;

import com.opensymphony.xwork2.ActionSupport;

public class PageAddAction extends ActionSupport {
	private ITaskDao dao;
	private ITaskUserDao taskUserDao;
	private Task task;
	private List<Long> alert_users;
	
	

	public void setTaskUserDao(ITaskUserDao taskUserDao) {
		this.taskUserDao = taskUserDao;
	}

	public void setAlert_users(List<Long> alert_users) {
		this.alert_users = alert_users;
	}
	
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
		
		for(Long userId : alert_users){
			taskUserDao.insert(task.getId(),userId);
		}
		
		
		return SUCCESS;
	}

}
