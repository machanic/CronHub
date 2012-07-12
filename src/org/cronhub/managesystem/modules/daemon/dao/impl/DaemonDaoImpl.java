package org.cronhub.managesystem.modules.daemon.dao.impl;

import java.util.List;

import org.cronhub.managesystem.commons.dao.BaseRowMapper;
import org.cronhub.managesystem.commons.dao.bean.Daemon;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.config.AssociateDeleteConfig;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.springframework.jdbc.core.JdbcTemplate;


public class DaemonDaoImpl implements IDaemonDao {
	private JdbcTemplate jdbcTemplate;
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	private IDoneRecordDao doneRecordDao;
	private ITaskDao taskDao;

	public void setDoneRecordDao(IDoneRecordDao doneRecordDao) {
		this.doneRecordDao = doneRecordDao;
	}
	
	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}

	@Override
	public void deleteById(Long id,AssociateDeleteConfig config) {
		if(config.getDeleteTask() || config.getDeleteTask_record_done() || config.getDeleteTask_record_undo()){
			List<Task> tasks  = this.taskDao.findByDaemonId(id);
			if(config.getDeleteTask_record_done()){
				for(String tableName:this.doneRecordDao.getAllDoneTableName()){
					for(Task task : tasks){
						String deleteDoneRecordSql = "DELETE FROM "+tableName+" WHERE task_id=?";
						this.jdbcTemplate.update(deleteDoneRecordSql,new Object[]{task.getId()});
					}
				}
			}
			if(config.getDeleteTask_record_undo()){
				String deleteUndoSql = "DELETE FROM task_record_undo WHERE task_id = ?";
				for(Task task : tasks){
					this.jdbcTemplate.update(deleteUndoSql,new Object[]{task.getId()});
				}
			}
			if(config.getDeleteTask()){
				String deleteTaskSql = "DELETE FROM task WHERE daemon_id = ?";
				this.jdbcTemplate.update(deleteTaskSql,new Object[]{id});
			}
		}
		final String deleteSql = "DELETE FROM daemon WHERE id = ?";
		this.jdbcTemplate.update(deleteSql, new Object[]{id});
	}

	@Override
	public Daemon findById(Long id) {
		final String findByIdSql = "SELECT id,machine_ip,machine_port,daemon_version_name,must_lostconn_email,lostconn_emailaddress,conn_status,comment,update_time FROM daemon WHERE id = ?";
		return (Daemon)this.jdbcTemplate.queryForObject(findByIdSql, new Object[]{id},new BaseRowMapper(Daemon.class));
	}

	@Override
	public void insert(Daemon daemon) {
		//final String insertSql = "INSERT INTO daemon(machine_ip,machine_port,daemon_version_name,must_lostconn_email,lostconn_emailaddress,conn_status,comment) VALUES('?',?,'?',?,'?',?,'?')";
		final String insertSql = "INSERT INTO daemon(machine_ip,machine_port,daemon_version_name,must_lostconn_email,lostconn_emailaddress,conn_status,comment,update_time) VALUES(?,?,?,?,?,?,?,?)";
		this.jdbcTemplate.update(insertSql, new Object[]{daemon.getMachine_ip(),daemon.getMachine_port(),daemon.getDaemon_version_name(),daemon.isMust_lostconn_email(),daemon.getLostconn_emailaddress(),daemon.getConn_status(),daemon.getComment(),daemon.getUpdate_time()});
	}

	@Override
	public void update(Daemon daemon) {
		final String updateSql = "UPDATE daemon SET machine_ip=?,machine_port=?,daemon_version_name=?,must_lostconn_email=?,lostconn_emailaddress=?,conn_status=?,comment=?,update_time=? WHERE id=?";
		this.jdbcTemplate.update(updateSql,new Object[]{daemon.getMachine_ip(),daemon.getMachine_port(),daemon.getDaemon_version_name(),daemon.isMust_lostconn_email(),daemon.getLostconn_emailaddress(),daemon.getConn_status(),daemon.getComment(),daemon.getUpdate_time(),daemon.getId()});
		
	}

	@Override
	public List<Daemon> findByPage(String orderLimit) {
		final String findSql = "SELECT id,machine_ip,machine_port,daemon_version_name,must_lostconn_email,lostconn_emailaddress,conn_status,comment,update_time FROM daemon "+orderLimit;
		List<Daemon> daemons = (List<Daemon>)this.jdbcTemplate.query(findSql, new BaseRowMapper(Daemon.class));
		return daemons;
	}

	@Override
	public List<Daemon> findAll(String whereSql) {
		return findByPage(whereSql);
	}

	@Override
	public List<Daemon> findByPage(String orderLimit, FillConfig fillConfig) {
		final String findSql = "SELECT id,machine_ip,machine_port,daemon_version_name,must_lostconn_email,lostconn_emailaddress,conn_status,comment,update_time FROM daemon "+orderLimit;
		List<Daemon> daemons = (List<Daemon>)this.jdbcTemplate.query(findSql, new BaseRowMapper(Daemon.class));
		if(fillConfig.getFillTask()){
			for(Daemon daemon: daemons){
				daemon.setTask_count(taskDao.findCount(" WHERE daemon_id="+daemon.getId()));
			}
		}
		return daemons;
	}



}
