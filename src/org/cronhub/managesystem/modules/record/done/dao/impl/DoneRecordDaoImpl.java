package org.cronhub.managesystem.modules.record.done.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.cronhub.managesystem.commons.dao.BaseRowMapper;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.springframework.jdbc.core.JdbcTemplate;


public class DoneRecordDaoImpl implements IDoneRecordDao {
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	private ITaskDao taskDao;
	private IDaemonDao daemonDao;
	
	private SimpleDateFormat mounthTableFormat = new SimpleDateFormat("yyyyMM");
	@Override
	public void insert(TaskRecordDone record) {
		String newTableName = String.format("task_record_done_%s", mounthTableFormat.format(record.getStart_datetime()));
		final String createTableSql = String.format("CREATE TABLE IF NOT EXISTS %s LIKE task_record_done;", newTableName);
		this.jdbcTemplate.update(createTableSql);
		final String insertSql = "INSERT INTO "+newTableName+"(task_id,real_cmd,exit_code,complete_success,start_datetime,end_datetime,exec_type,exec_return_str,current_redo_times,on_processing) VALUES(?,?,?,?,?,?,?,?,?,?)";
		this.jdbcTemplate.update(insertSql, new Object[]{record.getTask_id(),record.getReal_cmd(),record.getExit_code(),record.getComplete_success(),record.getStart_datetime(),record.getEnd_datetime(),record.getExec_type(),record.getExec_return_str(),record.getCurrent_redo_times(),record.getOn_processing()});
	}
	@Override
	public List<String> getAllDoneTableName() {
		String getTableDoneTableNameSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE `TABLE_SCHEMA`='cronhub_manage_system' AND `TABLE_NAME` LIKE 'task_record_done_%' AND TABLE_TYPE='BASE TABLE'";
		List<String> ret = new ArrayList<String>();
		List rows =  this.jdbcTemplate.queryForList(getTableDoneTableNameSql);
		for(Object entry : rows){
			ret.add(((Map)entry).get("TABLE_NAME").toString());
		}
		return ret;
	}
	@Override
	public List<Calendar> getAllDaysFromTable(String tableName) {
		String sql = "SELECT DISTINCT(DATE_FORMAT(start_datetime,'%Y-%m-%d')) AS day FROM "+tableName;
		//int year = Integer.parseInt(tableName.substring("task_record_done_".length()).substring(0, 4));
		List<Calendar> ret = new ArrayList<Calendar>();
		List<String> year_month_days = new ArrayList<String>();
		List rows =  this.jdbcTemplate.queryForList(sql);
		for(Object entry : rows){
			year_month_days.add(((Map)entry).get("day").toString());
		}
		for(String year_month_day : year_month_days){
			int firstIndex = year_month_day.indexOf("-");
			int lastIndex = year_month_day.lastIndexOf("-");
			int year = Integer.parseInt(year_month_day.substring(0, firstIndex));
			int month = Integer.parseInt(year_month_day.substring(firstIndex+1, lastIndex));
			int day = Integer.parseInt(year_month_day.substring(lastIndex+1));
			Calendar each = Calendar.getInstance();
			each.set(year,month-1,day);
			ret.add(each);
		}
		return ret;
	}
	@Override
	public List<TaskRecordDone> findByPage(String tableName,String orderLimit,
			FillConfig fillConfig) {
		//修正bug改为INNER JOIN,这一句如果用LEFT JOIN会发生如果以前有个任务执行了一半，但是将task或daemon删除了，这个时候删完后，执行完了报告回来的任务由于提取不到ip地址等信息，就会发生报错。
		String sql = String.format("SELECT %s.id AS id,task_id,complete_success,exit_code,daemon.machine_ip AS machine_ip,daemon.conn_status AS conn_status,task.cron_exp AS cron_exp,real_cmd,start_datetime,end_datetime,task.run_mode AS run_mode,exec_type,task.is_process_node AS is_process_node,task.is_redo AS is_redo,current_redo_times,task.end_redo_times AS end_redo_times,on_processing FROM (%s INNER JOIN task ON %s.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id %s",tableName,tableName,tableName,orderLimit);
		List<TaskRecordDone> taskRecordDones = this.jdbcTemplate.query(sql, new BaseRowMapper(TaskRecordDone.class));
		if(fillConfig.getFillTask()){
			for(TaskRecordDone done : taskRecordDones){
				FillConfig config = new FillConfig(false,false);
				Task task = this.taskDao.findById(done.getTask_id(),config);
				if(fillConfig.getFillDaemon()){
					task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
				}
				done.setTask(task);
			}
		}
		return taskRecordDones;
	}
	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}
	public void setDaemonDao(IDaemonDao daemonDao) {
		this.daemonDao = daemonDao;
	}
	@Override
	public void update(TaskRecordDone record) {
		String tableName = String.format("task_record_done_%s", mounthTableFormat.format(record.getStart_datetime()));
		String sql= String.format("UPDATE %s SET task_id=?,real_cmd=?,exit_code=?,complete_success=?,start_datetime=?,end_datetime=?,exec_type=?,exec_return_str=?,current_redo_times=?,on_processing=? WHERE id=?",tableName);
		this.jdbcTemplate.update(sql, new Object[]{record.getTask_id(),record.getReal_cmd(),record.getExit_code(),record.getComplete_success(),record.getStart_datetime(),record.getEnd_datetime(),record.getExec_type(),record.getExec_return_str(),record.getCurrent_redo_times(),record.getOn_processing(),record.getId()});
	}
	@Override
	public TaskRecordDone findById(Long id,String tableName,FillConfig fillConfig) {
		String sql = String.format("SELECT %s.id AS id,task_id,complete_success,exit_code,daemon.machine_ip AS machine_ip,task.cron_exp AS cron_exp,exec_return_str,real_cmd,start_datetime,end_datetime,task.run_mode AS run_mode,exec_type,task.is_process_node AS is_process_node,current_redo_times,task.end_redo_times AS end_redo_times,on_processing FROM (%s INNER JOIN task ON %s.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id WHERE %s.id = %s",tableName,tableName,tableName,tableName,id);
		TaskRecordDone record =  (TaskRecordDone)this.jdbcTemplate.queryForObject(sql,new BaseRowMapper(TaskRecordDone.class));
		if(fillConfig.getFillTask()){
			FillConfig config = new FillConfig(false,false);
			Task task = this.taskDao.findById(record.getTask_id(),config);
			if(fillConfig.getFillDaemon()){
				task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
			}
			record.setTask(task);
		}
		return record;
	}
	@Override
	public List<TaskRecordDone> findByTaskId(Long taskId, String tableName,
			FillConfig fillConfig) {
		String sql = String.format("SELECT %s.id AS id,task_id,complete_success,exit_code,daemon.machine_ip AS machine_ip,task.cron_exp AS cron_exp,exec_return_str,real_cmd,start_datetime,end_datetime,task.run_mode AS run_mode,exec_type,task.is_process_node AS is_process_node,current_redo_times,task.end_redo_times AS end_redo_times,on_processing FROM (%s INNER JOIN task ON %s.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id WHERE %s.task_id = %s",tableName,tableName,tableName,tableName,taskId);
		List<TaskRecordDone> taskRecordDones = this.jdbcTemplate.query(sql, new BaseRowMapper(TaskRecordDone.class));
		if(fillConfig.getFillTask()){
			for(TaskRecordDone done : taskRecordDones){
				FillConfig config = new FillConfig(false,false);
				Task task = this.taskDao.findById(done.getTask_id(),config);
				if(fillConfig.getFillDaemon()){
					task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
				}
				done.setTask(task);
			}
		}
		return taskRecordDones;
	}
	@Override
	public List<TaskRecordDone> findAll(String tableName, String whereSql,
			FillConfig fillConfig) {
		return findByPage(tableName,whereSql,fillConfig);
	}
	@Override
	public boolean hasTableByName(String tableName) {
		String sql = "SELECT COUNT(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES WHERE `TABLE_SCHEMA`='cronhub_manage_system' AND `TABLE_NAME` = '"+tableName+"' AND TABLE_TYPE='BASE TABLE'";
		return this.jdbcTemplate.queryForInt(sql) > 0;
	}
	
	
}
