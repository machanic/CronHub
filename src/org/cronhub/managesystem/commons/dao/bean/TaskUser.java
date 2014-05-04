package org.cronhub.managesystem.commons.dao.bean;

public class TaskUser {
	private Long id;
	private Long task_id;
	private Long user_id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTask_id() {
		return task_id;
	}
	public void setTask_id(Long task_id) {
		this.task_id = task_id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	
}
