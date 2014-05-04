package org.cronhub.managesystem.modules.task.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.cronhub.managesystem.commons.dao.BaseRowMapper;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.bean.TaskUser;
import org.cronhub.managesystem.commons.dao.bean.User;
import org.cronhub.managesystem.commons.dao.config.AssociateDeleteConfig;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.mysql.jdbc.Statement;


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

	
	/***
	 * 这个函数如果jdbctemplace使用连接池，因此会造成bug, SELECT LAST_INSERT_ID()会得出0的返回值
	 */
	@Override
	public void insert(final Task task) {
		final String sql = "INSERT INTO task(daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,comment,operate_uid,update_time,is_redo,end_redo_times) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection)
					throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, task.getDaemon_id());
				ps.setString(2, task.getCron_exp());
				ps.setString(3, task.getShell_cmd());
				ps.setBoolean(4, task.getMust_replace_cmd());
				ps.setBoolean(5, task.getRun_mode());
				ps.setString(6, task.getRun_start_reportaddress());
				ps.setString(7, task.getRun_end_reportaddress());
				ps.setBoolean(8, task.getIs_process_node());
				
				ps.setNull(9, Types.BOOLEAN);
	//			ps.setBoolean(9,task.getIs_process_chain());
				
				ps.setString(10,task.getProcess_tasks());
				ps.setString(11, task.getComment());
				
				ps.setNull(12, Types.BIGINT);
//				ps.setLong(12, task.getOperate_uid());
				
				ps.setDate(13, new java.sql.Date(task.getUpdate_time().getTime()));
				ps.setBoolean(14,task.getIs_redo());
				ps.setInt(15, task.getEnd_redo_times());
				return ps;
			}
		}, keyHolder);
		
		Long lastId = keyHolder.getKey().longValue();   
		task.setId(lastId);
	}
	
	private void fillUserToTask(final Task task){
		String taskUserSql = "SELECT id,task_id,user_id FROM task_user WHERE task_id = ?";
		List<TaskUser> task_users = this.jdbcTemplate.query(taskUserSql, new Object[]{task.getId()}, new BaseRowMapper(TaskUser.class));
		if(task_users.size() == 0){
			return;
		}
		StringBuilder user_ids = new StringBuilder();
		for(TaskUser taskUser : task_users){
			user_ids.append(taskUser.getUser_id()).append(",");
		}
		user_ids.deleteCharAt(user_ids.length()-1);
		String userSql = String.format("SELECT id,user_name,mail_name FROM user WHERE id IN (%s)", user_ids);
		List<User> users = this.jdbcTemplate.query(userSql, new BaseRowMapper(User.class));
		task.setUsers(users);
	}
	
	private void fillUserToTask(final List<Task> tasks){
		for(final Task task : tasks){
			fillUserToTask(task);
		}
	}
	
	@Override
	public List<Task> findByPage(String orderLimit, FillConfig fillConfig) {
		String sql = "SELECT task.id as id,daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,task.comment as comment,operate_uid,task.update_time,is_redo,end_redo_times,daemon.machine_ip as machine_ip FROM task  INNER JOIN daemon on task.daemon_id = daemon.id "+orderLimit;
		List<Task> tasks = this.jdbcTemplate.query(sql, new BaseRowMapper(Task.class));
		if(fillConfig.getFillDaemon()){
			for(Task task : tasks){
				task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
			}
		}
		
		fillUserToTask(tasks);
		return tasks;
	}

	@Override
	public List<Task> findAll(String whereSql, FillConfig fillConfig) {
		String sql="SELECT task.id as id,daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,task.comment as comment,operate_uid,task.update_time,is_redo,end_redo_times,daemon.machine_ip as machine_ip,daemon.conn_status as conn_status FROM task INNER JOIN daemon on task.daemon_id = daemon.id" + whereSql;
		List<Task> tasks = this.jdbcTemplate.query(sql, new BaseRowMapper(Task.class));
		if(fillConfig.getFillDaemon()){
			for(Task task : tasks){
				task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
			}
		}
		
		fillUserToTask(tasks);
		
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
		
		fillUserToTask(t);
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
		
		this.jdbcTemplate.update("DELETE FROM task_user WHERE task_id = ?", new Object[]{id});
		
		String sql = "DELETE FROM task WHERE id = ?";
		this.jdbcTemplate.update(sql,new Object[]{id});
	}

	@Override
	public List<Task> findByDaemonId(Long id) {
		String sql="SELECT id,daemon_id,cron_exp,shell_cmd,must_replace_cmd,run_mode,run_start_reportaddress,run_end_reportaddress,is_process_node,is_process_chain,process_tasks,comment,operate_uid,task.update_time,is_redo,end_redo_times FROM task WHERE daemon_id = "+id;
		List<Task> tasks = this.jdbcTemplate.query(sql, new BaseRowMapper(Task.class));
		fillUserToTask(tasks);
		return tasks;
	}
	
	@Override
	public Integer findCount(String whereSql) {
		String sql = "SELECT COUNT(*) FROM task "+whereSql;
		return this.jdbcTemplate.queryForInt(sql);
	}


}
