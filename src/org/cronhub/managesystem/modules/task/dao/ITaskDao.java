package org.cronhub.managesystem.modules.task.dao;

import java.util.List;

import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.config.AssociateDeleteConfig;
import org.cronhub.managesystem.commons.dao.config.FillConfig;


public interface ITaskDao {
	public void insert(Task task);
	public List<Task> findByPage(String orderLimit,FillConfig fillConfig);
	public List<Task> findAll(String whereSql,FillConfig fillConfig);
	public Task findById(Long id,FillConfig fillConfig);
	public void deleteById(Long id,AssociateDeleteConfig config);
	public List<Task> findByDaemonId(Long id);
	public Integer findCount(String whereSql);
}
