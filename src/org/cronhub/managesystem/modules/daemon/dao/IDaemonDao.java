package org.cronhub.managesystem.modules.daemon.dao;

import java.util.List;

import org.cronhub.managesystem.commons.dao.bean.Daemon;
import org.cronhub.managesystem.commons.dao.config.AssociateDeleteConfig;
import org.cronhub.managesystem.commons.dao.config.FillConfig;


public interface IDaemonDao {
	public void update(Daemon daemon);
	public void insert(Daemon daemon);
	public Daemon findById(Long id);
	public List<Daemon> findByPage(String orderLimit);
	public List<Daemon> findByPage(String orderLimit,FillConfig fillConfig);
	public List<Daemon> findAll(String whereSql);
	public void deleteById(Long id,AssociateDeleteConfig config);
}