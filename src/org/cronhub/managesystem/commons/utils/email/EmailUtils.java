package org.cronhub.managesystem.commons.utils.email;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.mail.SimpleEmail;

public class EmailUtils {
	private String fromMailUser;
	private String fromMailPass;
	private String hostMailName;
	private String destinations;
	public EmailUtils(String fromMailUser, String fromMailPass,
			String hostMailName,String dest) {
		this.fromMailUser = fromMailUser;
		this.fromMailPass = fromMailPass;
		this.hostMailName = hostMailName;
		this.destinations = dest;
	}
	public EmailUtils(String fromMailUser, String fromMailPass,
			String hostMailName){
		this(fromMailUser,fromMailPass,hostMailName,"");
	}
	public  void sendMail(String subject,String content){
		this.sendMail(subject, content,this.destinations);
	}
	/***
	 * 可供发送按照井号隔开的 收件人 字符串
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @param destinations 收件人地址字符串，按照井号隔开
	 */
	public void sendMail(String subject,String content,String destinations){
		this.sendMail(subject, content,Arrays.asList(destinations.split("#")));
	}
	public void sendMail(String subject,String content,List<String> destinations){
		SimpleEmail email = new SimpleEmail();
		//email.setTLS(true);
		email.setCharset("UTF-8");
		email.setHostName(this.hostMailName);
		email.setAuthentication(this.fromMailUser, this.fromMailPass);
		try{
			for(String dest : destinations){
			email.addTo(dest);
			}
			email.setFrom(this.fromMailUser);
			email.setSubject(subject);
			email.setMsg(content);
			email.send();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
