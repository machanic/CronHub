package org.cronhub.managesystem.commons.dao.bean;

import java.util.Date;

public class Task {
	private Long id;
	private Long daemon_id;
	private String cron_exp;
	private String shell_cmd;
	private Boolean must_replace_cmd;
	private Boolean run_mode;
	private String run_start_reportaddress;
	private String run_end_reportaddress;
	private Boolean is_process_node;
	private Boolean is_process_chain;
	private String process_tasks;
	private String comment;
	private Long operate_uid;
	private Date update_time;
	private Boolean is_redo;
	private Integer end_redo_times;
	private Daemon daemon;
	public Daemon getDaemon() {
		return daemon;
	}
	public void setDaemon(Daemon daemon) {
		this.daemon = daemon;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getDaemon_id() {
		return daemon_id;
	}
	public void setDaemon_id(Long daemon_id) {
		this.daemon_id = daemon_id;
	}
	public String getCron_exp() {
		return cron_exp;
	}
	public void setCron_exp(String cron_exp) {
		this.cron_exp = cron_exp;
	}
	public String getShell_cmd() {
		return shell_cmd;
	}
	public void setShell_cmd(String shell_cmd) {
		this.shell_cmd = shell_cmd;
	}
	public Boolean getMust_replace_cmd() {
		return must_replace_cmd;
	}
	public void setMust_replace_cmd(Boolean must_replace_cmd) {
		this.must_replace_cmd = must_replace_cmd;
	}
	public Boolean getRun_mode() {
		return run_mode;
	}
	public void setRun_mode(Boolean run_mode) {
		this.run_mode = run_mode;
	}
	public String getRun_start_reportaddress() {
		return run_start_reportaddress;
	}
	public void setRun_start_reportaddress(String run_start_reportaddress) {
		this.run_start_reportaddress = run_start_reportaddress;
	}
	public String getRun_end_reportaddress() {
		return run_end_reportaddress;
	}
	public void setRun_end_reportaddress(String run_end_reportaddress) {
		this.run_end_reportaddress = run_end_reportaddress;
	}
	public Boolean getIs_process_node() {
		return is_process_node;
	}
	public void setIs_process_node(Boolean is_process_node) {
		this.is_process_node = is_process_node;
	}
	public Boolean getIs_process_chain() {
		return is_process_chain;
	}
	public void setIs_process_chain(Boolean is_process_chain) {
		this.is_process_chain = is_process_chain;
	}
	public String getProcess_tasks() {
		return process_tasks;
	}
	public void setProcess_tasks(String process_tasks) {
		this.process_tasks = process_tasks;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Long getOperate_uid() {
		return operate_uid;
	}
	public void setOperate_uid(Long operate_uid) {
		this.operate_uid = operate_uid;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Boolean getIs_redo() {
		return is_redo;
	}
	public void setIs_redo(Boolean is_redo) {
		this.is_redo = is_redo;
	}
	public Integer getEnd_redo_times() {
		return end_redo_times;
	}
	public void setEnd_redo_times(Integer end_redo_times) {
		this.end_redo_times = end_redo_times;
	}
	
}
