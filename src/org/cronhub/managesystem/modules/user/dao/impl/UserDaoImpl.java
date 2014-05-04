package org.cronhub.managesystem.modules.user.dao.impl;

import java.util.List;

import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.bean.User;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.cronhub.managesystem.modules.user.dao.IUserDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDaoImpl implements IUserDao {
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private ITaskDao taskDao;
	
	
	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}

	@Override
	public void insert(User user) {
			this.jdbcTemplate.update("INSERT INTO user(user_name, mail_name) VALUES(?,?)",
					new Object[]{user.getUser_name(), user.getMail_name()});
	}

	@Override
	public List<User> findAll() {
		return this.jdbcTemplate.queryForList("SELECT id,user_name,mail_name FROM user;");
	}

	@Override
	public void deleteById(Long user_id) {
		this.jdbcTemplate.update("DELETE FROM user WHERE id=?", new Object[]{user_id});
		this.jdbcTemplate.update("DELETE FROM task_user WHERE user_id=?", new Object[]{user_id});
	}


}
