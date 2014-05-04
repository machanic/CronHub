package org.cronhub.managesystem.commons.dao.bean;

public class User {
	private Long id;
	private String user_name;
	private String mail_name;
	
	public String getMail_name() {
		return mail_name;
	}
	public void setMail_name(String mail_name) {
		this.mail_name = mail_name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
}
