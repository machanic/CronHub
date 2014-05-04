package org.cronhub.managesystem.modules.user.dao;

import java.util.List;

import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.bean.User;

public interface IUserDao {
	public void insert(User user);
	public List<User> findAll();
	public void deleteById(Long user_id);
}
