package org.cronhub.managesystem.modules.taskuser.dao.impl;

import org.cronhub.managesystem.modules.taskuser.dao.ITaskUserDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class TaskUserDaoImpl implements ITaskUserDao {
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Override
	public void insert(Long taskId, Long userId) {
		final String sql = "INSERT INTO task_user(task_id,user_id) VALUES(?,?)";
		this.jdbcTemplate.update(sql, new Object[] {taskId, userId });
	}

}
