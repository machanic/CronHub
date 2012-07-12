package org.cronhub.managesystem.commons.dao.bean;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Daemon {
	private Long id;
	private String machine_ip;
	private Integer machine_port;
	private String daemon_version_name;
	private Boolean must_lostconn_email;
	private String lostconn_emailaddress;
	private Boolean conn_status;
	private String comment;
	private Date update_time;
	
	/**
	 * 新增一个得到task任务个数的字段，供页面显示使用
	 */
	private Integer task_count;
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Boolean getMust_lostconn_email() {
		return must_lostconn_email;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMachine_ip() {
		return machine_ip;
	}
	public void setMachine_ip(String machine_ip) {
		this.machine_ip = machine_ip;
	}
	public Integer getMachine_port() {
		return machine_port;
	}
	public void setMachine_port(Integer machine_port) {
		this.machine_port = machine_port;
	}
	public String getDaemon_version_name() {
		return daemon_version_name;
	}
	public void setDaemon_version_name(String daemon_version_name) {
		this.daemon_version_name = daemon_version_name;
	}
	public String getLostconn_emailaddress() {
		return lostconn_emailaddress;
	}
	public void setLostconn_emailaddress(String lostconn_emailaddress) {
		this.lostconn_emailaddress = lostconn_emailaddress;
	}
	
	public Boolean getConn_status() {
		return conn_status;
	}
	public void setConn_status(Boolean conn_status) {
		this.conn_status = conn_status;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Boolean isMust_lostconn_email() {
		return must_lostconn_email;
	}
	public void setMust_lostconn_email(Boolean must_lostconn_email) {
		this.must_lostconn_email = must_lostconn_email;
	}
	
	public Integer getTask_count() {
		return task_count;
	}
	public void setTask_count(Integer task_count) {
		this.task_count = task_count;
	}
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
}
