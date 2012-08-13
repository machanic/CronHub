package org.cronhub.managesystem.modules.record.undo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.cronhub.managesystem.commons.dao.BaseRowMapper;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordUndo;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.commons.params.daemon.ParamCommons;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;
import org.cronhub.managesystem.modules.record.undo.dao.IUndoRecordDao;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mysql.jdbc.Statement;

public class UndoRecordDaoImpl implements IUndoRecordDao {
	private SimpleDateFormat sdf = new SimpleDateFormat(ParamCommons.DATE_FORMAT);
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	private ITaskDao taskDao;
	private IDaemonDao daemonDao;
	
	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public void setDaemonDao(IDaemonDao daemonDao) {
		this.daemonDao = daemonDao;
	}

	@Override
	public Long insert(final TaskRecordUndo record) {
		final String insertSql = "INSERT INTO task_record_undo(task_id,real_cmd,run_status,start_datetime,exec_type) VALUES(?,?,?,?,?)";
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(insertSql,Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, record.getTask_id());
				ps.setString(2, record.getReal_cmd());
				ps.setInt(3, record.getRun_status());
				ps.setString(4, sdf.format(record.getStart_datetime()));
				ps.setInt(5,record.getExec_type());
				return ps;
			}
		},keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public void deleteById(Long id) {
		final String deleteSql = "DELETE FROM task_record_undo WHERE id = ?";
		this.jdbcTemplate.update(deleteSql,new Object[]{id});
		
	}

	@Override
	public TaskRecordUndo findById(Long id) {
		String sql = String.format("SELECT task_record_undo.id AS id,task_id,real_cmd,run_status,start_datetime,exec_type,daemon.machine_ip AS machine_ip,task.cron_exp AS cron_exp,task.is_process_node AS is_process_node,task.end_redo_times AS end_redo_times FROM (task_record_undo INNER JOIN task ON task_record_undo.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id WHERE task_record_undo.id = %s",id);
		return (TaskRecordUndo)this.jdbcTemplate.queryForObject(sql,new BaseRowMapper(TaskRecordUndo.class));
	}

	@Override
	public List<TaskRecordUndo> findByPage(String tableName, String orderLimit,
			FillConfig fillConfig) {
		String sql = String.format("SELECT task_record_undo.id AS id,task_id,real_cmd,run_status,start_datetime,exec_type,daemon.machine_ip AS machine_ip,task.cron_exp AS cron_exp,task.is_process_node AS is_process_node,task.end_redo_times AS end_redo_times,task.run_mode AS run_mode FROM %s %s",tableName,orderLimit);
		List<TaskRecordUndo> records = this.jdbcTemplate.query(sql,new BaseRowMapper(TaskRecordUndo.class));
		if(fillConfig.getFillTask()){
			for(TaskRecordUndo record : records){
				FillConfig config = new FillConfig(false,false);
				Task task = taskDao.findById(record.getTask_id(),config);
				if(fillConfig.getFillDaemon()){
					task.setDaemon(this.daemonDao.findById(task.getDaemon_id()));
				}
				record.setTask(task);
			}
		}
		return records;
	}

	@Override
	public Map<String, Map<String, Set<JSONObject>>> getAllDateInfoFromTable() {
		String sql = "SELECT DATE_FORMAT(start_datetime,'%Y年') AS year,DATE_FORMAT(start_datetime,'%m月') AS month,DATE_FORMAT(start_datetime,'%m月%d日') AS day,DATE_FORMAT(start_datetime,'%Y%m%d') AS date FROM task_record_undo";
		List result = this.jdbcTemplate.queryForList(sql);
		Map<String,Map<String,Set<JSONObject>>> ret = new HashMap<String,Map<String,Set<JSONObject>>>();
		for(Object res : result){
			Map bean =(Map)res;
			if(!ret.containsKey(bean.get("year").toString())){
				ret.put(bean.get("year").toString(), new HashMap<String,Set<JSONObject>>());
			}
			if(!ret.get(bean.get("year").toString()).containsKey(bean.get("month").toString())){
				ret.get(bean.get("year").toString()).put(bean.get("month").toString(), new HashSet<JSONObject>());
			}
			JSONObject json = new JSONObject();
			json.put("day", bean.get("day").toString());
			json.put("date",bean.get("date").toString());
			ret.get(bean.get("year").toString()).get(bean.get("month").toString()).add(json);
		}
		return ret;
	}

	@Override
	public Integer findCountById(Long id) {
		String sql = "SELECT COUNT(*) FROM task_record_undo WHERE id="+id;
		Integer count = this.jdbcTemplate.queryForInt(sql);
		return count;
	}

}
