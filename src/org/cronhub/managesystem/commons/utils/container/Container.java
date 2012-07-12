package org.cronhub.managesystem.commons.utils.container;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Container {
	private static ApplicationContext context = new FileSystemXmlApplicationContext("classpath:spring/*.xml");
	public static Object getBean(String beanId){
		return context.getBean(beanId);
	}
	
}
