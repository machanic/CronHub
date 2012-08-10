package org.cronhub.managesystem.modules.record.done.dao;

import java.util.Calendar;
import java.util.List;

import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;
import org.cronhub.managesystem.commons.dao.config.FillConfig;


public interface IDoneRecordDao {
	public void insert(TaskRecordDone record);
	public List<String> getAllDoneTableName();
	public List<Calendar> getAllDaysFromTable(String tableName);
	public List<TaskRecordDone> findByPage(String tableName,String orderLimit,FillConfig fillConfig);
	public List<TaskRecordDone> findAll(String tableName,String whereSql,FillConfig fillConfig);
	public void update(TaskRecordDone record);
	public TaskRecordDone findById(Long id,String tableName,FillConfig config);
	public List<TaskRecordDone> findByTaskId(Long taskId,String tableName,FillConfig config);
	public boolean hasTableByName(String tableName);
}
