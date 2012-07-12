package org.cronhub.managesystem.modules.daemon.boot;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskCollector;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskTable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContextEvent;

import org.cronhub.managesystem.commons.dao.bean.Daemon;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.utils.PingUtils;
import org.cronhub.managesystem.commons.utils.container.WebContainer;
import org.cronhub.managesystem.commons.utils.email.EmailUtils;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;
import org.springframework.web.context.ContextLoaderListener;


public class CommunicateDetect extends ContextLoaderListener {
	private IDaemonDao daemonDao;
	
	public void setDaemonDao(IDaemonDao daemonDao) {
		this.daemonDao = daemonDao;
	}
	private EmailUtils email;
	public void setEmail(EmailUtils email) {
		this.email = email;
	}
	
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		CommunicateDetect detecter = (CommunicateDetect)WebContainer.getBean("communicateDetect",event.getServletContext());
		detecter.start();
	}
	public void start(){
		Thread cThread =new Thread(communicateThread);
		cThread.setDaemon(true);
		cThread.start();
	}
	private final  ConcurrentHashMap<Daemon,Integer> alertMailCount = new ConcurrentHashMap<Daemon,Integer>();
	private SchedulingPattern communicate_cron_exp = new  SchedulingPattern("*/3 * * * *");
	private int noalertLimit = 3;
	private Runnable communicateThread = new Runnable(){
		@Override
		public void run() {
			Scheduler sch = new Scheduler();
			sch.addTaskCollector(new TaskCollector(){
				@Override
				public TaskTable getTasks() {
					TaskTable table = new TaskTable();
					List<Daemon> daemons = daemonDao.findAll("");
					for(final Daemon daemon : daemons){
						table.add(communicate_cron_exp, new Task(){
							@Override
							public void execute(TaskExecutionContext context)
									throws RuntimeException {
								//每三分钟检测一次,多线程同时检测,如果连续3次不能通信才发送报警
								final String dateStr = Params.date_format_page.format(new Date());
								final String subject = "["+dateStr+"] 调度系统\"通信报警\"通知 [ip:"+daemon.getMachine_ip()+"]";
								boolean ping = PingUtils.ping(daemon.getMachine_ip(), daemon.getMachine_port());
								daemon.setConn_status(ping);
								daemonDao.update(daemon);
								if(!ping){
									AppLogger.daemonErrorLogger.error("daemon id:"+daemon.getId()+",ip:"+daemon.getMachine_ip()+",port:"+daemon.getMachine_port()+" can not communicate ping! UPDATE TO table daemon in DB");
									if(daemon.getMust_lostconn_email()){
										String current_content = "["+dateStr+"] 错误报警:调度系统中央服务器不能与ip地址:"+daemon.getMachine_ip()+",端口号port:"+daemon.getMachine_port()+",daemon_id:"+daemon.getId()+",取得通信,请检查!";
										List<String> mailDest = Arrays.asList(daemon.getLostconn_emailaddress().split("#"));
										if(mailDest.size()>0){
											if(!alertMailCount.containsKey(daemon)){
												alertMailCount.put(daemon, 0);
											}
											//alertMailCount.put(daemon, alertMailCount.get(daemon)+1);
											int currentCount = alertMailCount.get(daemon)%noalertLimit;
											if(alertMailCount.get(daemon) == noalertLimit){
												email.sendMail(subject, current_content, mailDest);
												alertMailCount.put(daemon,currentCount);
											}else{
												alertMailCount.put(daemon,alertMailCount.get(daemon)+1);
											}
										}
											
										}
									}
								}
							}
							);
					}
					return table;
				}
			});
			sch.start();
		}
	};

	
}
