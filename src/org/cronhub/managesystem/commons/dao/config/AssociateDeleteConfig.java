package org.cronhub.managesystem.commons.dao.config;

public class AssociateDeleteConfig {
	private Boolean deleteTask;
	private Boolean deleteTask_record_done;
	private Boolean deleteTask_record_undo;
	public AssociateDeleteConfig() {
	}

	public AssociateDeleteConfig(Boolean deleteTask,Boolean deleteTask_record_done,Boolean deleteTask_record_undo) {
		this.deleteTask = deleteTask;
		this.deleteTask_record_done = deleteTask_record_done;
		this.deleteTask_record_undo = deleteTask_record_undo;
	}

	public Boolean getDeleteTask() {
		return deleteTask;
	}

	public void setDeleteTask(Boolean deleteTask) {
		this.deleteTask = deleteTask;
	}

	public Boolean getDeleteTask_record_done() {
		return deleteTask_record_done;
	}

	public void setDeleteTask_record_done(Boolean deleteTask_record_done) {
		this.deleteTask_record_done = deleteTask_record_done;
	}

	public Boolean getDeleteTask_record_undo() {
		return deleteTask_record_undo;
	}

	public void setDeleteTask_record_undo(Boolean deleteTask_record_undo) {
		this.deleteTask_record_undo = deleteTask_record_undo;
	}
	
	
}
