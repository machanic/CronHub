package org.cronhub.managesystem.modules.user.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.dao.bean.User;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.commons.utils.SinaAlertUtils;
import org.cronhub.managesystem.modules.user.dao.IUserDao;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

public class PageViewAction extends ActionSupport {
	private IUserDao dao;
	private SinaAlertUtils sinaAlert;
	
	public void setSinaAlert(SinaAlertUtils sinaAlert) {
		this.sinaAlert = sinaAlert;
	}

	public void setDao(IUserDao dao) {
		this.dao = dao;
	}

	public String findPage() {
		List<User> users = dao.findAll();
		ServletActionContext.getRequest().setAttribute(Params.PAGE_VIEW_LIST, users);
		return SUCCESS;
	}
	
	public String deleteById(){
		Long id = Long.valueOf(ServletActionContext.getRequest().getParameter("id"));
		this.dao.deleteById(id);
		PageIOUtils.printToPage("success");
		return NONE;
	}
	public String notifyById(){
		Map m = ServletActionContext.getRequest().getParameterMap();
		Gson gson = new Gson();
		User user = gson.fromJson(gson.toJson(m), User.class);
		List<User> list = new ArrayList<User>();
		list.add(user);
		sinaAlert.alertMail(list, "测试报警", "这是您刚刚点击的测试报警");
		sinaAlert.alertSms(list,  "您刚刚点击的测试报警");
		PageIOUtils.printToPage("success");
		return NONE;
	}
	public String findUserJson(){
		Gson gson = new Gson();
		List<User> user_ls = dao.findAll();
		PageIOUtils.printToPage(gson.toJson(user_ls));
		return NONE;
	}
	
	public String insertNewUser(){
		String user_name = ServletActionContext.getRequest().getParameter("user_name");
		String mail_name=  ServletActionContext.getRequest().getParameter("mail_name");
		User u = new User();
		u.setUser_name(user_name);
		u.setMail_name(mail_name);
		this.dao.insert(u);
		return SUCCESS;
	}
}
