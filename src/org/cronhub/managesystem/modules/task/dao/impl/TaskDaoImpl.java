package org.cronhub.managesystem.modules.task.dao.impl;

import java.util.List;

import org.cronhub.managesystem.commons.dao.BaseRowMapper;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.config.AssociateDeleteConfig;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.springframework.jdbc.core.JdbcTemplate;


public class TaskDaoImpl implements ITaskDao {
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	private IDaemonDao daemonDao;
	
	public void setDaemonDao(IDaemonDao daemonDao) {
		this.daemonDao = daemonDao;
	}
	private IDoneRecordDao doneRecordDao;
	
	public void setDoneRecordDao(IDoneRecordDao doneRecordDao) {
		this.doneRecordDao = doneRecordDao;
	}

	@Override
	public void insert(Task task) {
		final String sql = "INSERT INTO task(daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,comment,operate_uid,update_time,is_redo,end_redo_times) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		this.jdbcTemplate.update(sql, new Object[] { task.getDaemon_id(),
				task.getCron_exp(), task.getShell_cmd(),
				task.getMust_replace_cmd(), task.getRun_mode(),
				task.getRun_start_reportaddress(),
				task.getRun_end_reportaddress(), task.getIs_process_node(),
				task.getIs_process_chain(),
				task.getProcess_tasks(), task.getComment(),
				task.getOperate_uid(), task.getUpdate_time(),
				task.getIs_redo(), task.getEnd_redo_times() });
	}

	@Override
	public List<Task> findByPage(String orderLimit, FillConfig fillConfig) {
		String sql = "SELECT task.id as id,daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,task.comment as comment,operate_uid,task.update_time,is_redo,end_redo_times,daemon.machine_ip as machine_ip FROM task  left join daemon on task.daemon_id = daemon.id "+orderLimit;
		List<Task> tasks = this.jdbcTemplate.query(sql, new BaseRowMapper(Task.class));
		if(fillConfig.getFillDaemon()){
			for(Task task : tasks){
				task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
			}
		}
		return tasks;
	}

	@Override
	public List<Task> findAll(String whereSql, FillConfig fillConfig) {
		String sql="SELECT task.id as id,daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,task.comment as comment,operate_uid,task.update_time,is_redo,end_redo_times,daemon.machine_ip as machine_ip,daemon.conn_status as conn_status FROM task  LEFT JOIN daemon on task.daemon_id = daemon.id " + whereSql;
		List<Task> tasks = this.jdbcTemplate.query(sql, new BaseRowMapper(Task.class));
		if(fillConfig.getFillDaemon()){
			for(Task task : tasks){
				task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
			}
		}
		return tasks;
	}

	@Override
	public Task findById(Long id,FillConfig fillConfig) {
		String sql="SELECT id,daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,comment,operate_uid,task.update_time,is_redo,end_redo_times FROM task WHERE id = "+id;
		List<Task> tasks = this.jdbcTemplate.query(sql, new BaseRowMapper(Task.class));
		Task t = null;
		if(tasks != null && tasks.size()>0){
			t= tasks.get(0);
		}else{
			return null;
		}
		if(fillConfig.getFillDaemon()){
			t.setDaemon(this.daemonDao.findById(t.getDaemon_id()));
		}
		return t;
	}

	@Override
	public void deleteById(Long id,AssociateDeleteConfig config) {
		if(config.getDeleteTask_record_done()){
			for(String tableName:this.doneRecordDao.getAllDoneTableName()){
				String deleteDoneRecordSql = "DELETE FROM "+tableName+" WHERE task_id=?";
				this.jdbcTemplate.update(deleteDoneRecordSql,new Object[]{id});
			}
		}
		if(config.getDeleteTask_record_undo()){
			String deleteUndoSql = "DELETE FROM task_record_undo WHERE task_id = ?";
			this.jdbcTemplate.update(deleteUndoSql,new Object[]{id});
		}
		String sql = "DELETE FROM task WHERE id = ?";
		this.jdbcTemplate.update(sql,new Object[]{id});
	}

	@Override
	public List<Task> findByDaemonId(Long id) {
		String sql="SELECT id,daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,comment,operate_uid,task.update_time,is_redo,end_redo_times FROM task WHERE daemon_id = "+id;
		List<Task> tasks = this.jdbcTemplate.query(sql, new BaseRowMapper(Task.class));
		return tasks;
	}

	@Override
	public Integer findCount(String whereSql) {
		String sql = "SELECT COUNT(*) FROM task "+whereSql;
		return this.jdbcTemplate.queryForInt(sql);
	}


}
