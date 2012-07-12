package org.cronhub.managesystem.commons.utils.container;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class WebContainer {
	public static Object getBean(String beanId){
		return WebApplicationContextUtils.getRequiredWebApplicationContext(ServletActionContext.getServletContext()).getBean(beanId);
	}
	public static Object getBean(String beanId,ServletContext context){
		return WebApplicationContextUtils.getRequiredWebApplicationContext(context).getBean(beanId);
	}
	public static String getWebAppPath(ServletContext context){
		return context.getRealPath("");
	}
}
