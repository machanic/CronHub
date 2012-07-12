package org.cronhub.managesystem.commons.dao.config;

public class FillConfig {
	private Boolean fillDaemon;
	private Boolean fillTask;
	public Boolean getFillDaemon() {
		return fillDaemon;
	}
	public Boolean getFillTask() {
		return fillTask;
	}
	public FillConfig(Boolean fillDaemon, Boolean fillTask) {
		this.fillDaemon = fillDaemon;
		this.fillTask = fillTask;
	}
	private FillConfig() {
	}
	private static final FillConfig fillTaskInstance =new  FillConfig(false,true);
	private static final FillConfig fillDaemonInstance =new  FillConfig(true,false);
	private static final FillConfig fillAllInstance =new  FillConfig(true,true);
	private static final FillConfig fillNoneInstance =new  FillConfig();
	public static FillConfig getFillTaskInstance(){
		return FillConfig.fillTaskInstance;
	}
	public static FillConfig getFillDaemonInstance(){
		return FillConfig.fillDaemonInstance;
	}
	public static FillConfig getFillAllInstance(){
		return FillConfig.fillAllInstance;
	}
	public static FillConfig getFillNoneInstance(){
		return FillConfig.fillNoneInstance;
	}
	
}
