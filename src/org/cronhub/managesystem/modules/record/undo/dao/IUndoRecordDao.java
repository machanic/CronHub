package org.cronhub.managesystem.modules.record.undo.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cronhub.managesystem.commons.dao.bean.TaskRecordUndo;
import org.cronhub.managesystem.commons.dao.config.FillConfig;

import net.sf.json.JSONObject;


public interface IUndoRecordDao {
	public Long insert(TaskRecordUndo record);
	public void deleteById(Long id);
	public TaskRecordUndo findById(Long id);
	public List<TaskRecordUndo> findByPage(String tableName,String orderLimit,FillConfig fillConfig);
	public Map<String,Map<String,Set<JSONObject>>> getAllDateInfoFromTable();
	public Integer findCountById(Long id);
}
