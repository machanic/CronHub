package org.cronhub.managesystem.commons.dao.bean;

import java.util.Date;

import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.utils.time.TimeUtils;


public class TaskRecordUndo {
	private Long id;
	private Long task_id;
	private String real_cmd;
	private Integer run_status;
	private Date start_datetime;
	private Integer exec_type;
	private Task task;
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
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
	public String getReal_cmd() {
		return real_cmd;
	}
	public void setReal_cmd(String real_cmd) {
		this.real_cmd = real_cmd;
	}
	public Integer getRun_status() {
		return run_status;
	}
	public void setRun_status(Integer run_status) {
		this.run_status = run_status;
	}
	public Integer getExec_type() {
		return exec_type;
	}
	public void setExec_type(Integer exec_type) {
		this.exec_type = exec_type;
	}
	public String getExec_type_ISO(){
		if(this.exec_type == 0){
			return "<font style='color:green'>crontab执行</font>";
		}else if(this.exec_type == 1){
			return "<font style='color:red'>手动重执行</font>";
		}else if(this.exec_type== 2){
			return "<font style='color:#8B2323'>自动重执行</font>";
		}else if(this.exec_type == 3){
			return "<font style='color:#8E388E'>当场执行</font>";
		}
		return "<font style='color:red'>未知类型</font>";
	}
	public Date getStart_datetime() {
		return start_datetime;
	}
	public void setStart_datetime(Date start_datetime) {
		this.start_datetime = start_datetime;
	}
	public String getDuration() {
		return TimeUtils.getDuration(new Date(),this.start_datetime);
	}
	
}
