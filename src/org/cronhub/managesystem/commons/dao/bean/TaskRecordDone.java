package org.cronhub.managesystem.commons.dao.bean;

import java.util.Date;

import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.utils.time.TimeUtils;


public class TaskRecordDone {
	private Long id;
	private Long task_id;
	private String real_cmd;
	private Integer exit_code;
	private Boolean complete_success;
	private Date start_datetime;
	private Date end_datetime;
	private Integer exec_type;
	private String exec_return_str;
	private Integer current_redo_times;
	private Task task;
	private TaskRecordUndo undoRecord;
	private Boolean on_processing;
	
	public Boolean getOn_processing() {
		return on_processing;
	}
	public void setOn_processing(Boolean on_processing) {
		this.on_processing = on_processing;
	}
	public TaskRecordUndo getUndoRecord() {
		return undoRecord;
	}
	public void setUndoRecord(TaskRecordUndo undoRecord) {
		this.undoRecord = undoRecord;
	}
	
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
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
	public Integer getExit_code() {
		return exit_code;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setExit_code(Integer exit_code) {
		this.exit_code = exit_code;
	}
	public Boolean getComplete_success() {
		return complete_success;
	}
	public void setComplete_success(Boolean complete_success) {
		this.complete_success = complete_success;
	}
	public Date getStart_datetime() {
		return start_datetime;
	}
	public void setStart_datetime(Date start_datetime) {
		this.start_datetime = start_datetime;
	}
	public Date getEnd_datetime() {
		return end_datetime;
	}
	public void setEnd_datetime(Date end_datetime) {
		this.end_datetime = end_datetime;
	}
	public Integer getExec_type() {
		return exec_type;
	}
	public void setExec_type(Integer exec_type) {
		this.exec_type = exec_type;
	}
	public String getExec_return_str() {
		return exec_return_str;
	}
	public void setExec_return_str(String exec_return_str) {
		this.exec_return_str = exec_return_str;
	}
	public Integer getCurrent_redo_times() {
		return current_redo_times;
	}
	public void setCurrent_redo_times(Integer current_redo_times) {
		this.current_redo_times = current_redo_times;
	}
	public String getDuration(){
		return TimeUtils.getDuration(this.end_datetime, this.start_datetime);
	}
	public String getComplete_success_ISO(){
		if(this.on_processing){
			return Params.PAGE_IMG_LOADING_TAG;
		}
		if(this.complete_success==true){
			return "<font style='color:green'>成功</font>";
		}else{
			return "<font style='color:red'>失败</font>";
		}
	}
	public String getExit_code_ISO(){
		if(this.on_processing){
			return Params.PAGE_IMG_LOADING_TAG;
		}
		return String.valueOf(this.exit_code);
	}
	public String getDuration_ISO(){
		if(this.on_processing){
			return Params.PAGE_IMG_LOADING_TAG;
		}
		return this.getDuration();
	}
	public String getStart_datetime_ISO(){
		if(this.on_processing){
			return Params.PAGE_IMG_LOADING_TAG;
		}
		return Params.date_format_page.format(this.start_datetime);
	}
	public String getEnd_datetime_ISO(){
		if(this.on_processing){
			return Params.PAGE_IMG_LOADING_TAG;
		}
		return Params.date_format_page.format(this.end_datetime);
	}
	public String getDatetime_interval_ISO(){
		if(this.on_processing){
			return Params.PAGE_IMG_LOADING_TAG;
		}
		return TimeUtils.getDateTimeInterval(this.start_datetime, this.end_datetime);
	}
	
	public String getExec_type_ISO(){
		if(this.on_processing){
			return Params.PAGE_IMG_LOADING_TAG;
		}
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
}
