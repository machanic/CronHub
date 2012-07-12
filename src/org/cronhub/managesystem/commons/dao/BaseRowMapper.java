package org.cronhub.managesystem.commons.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.cronhub.managesystem.commons.dao.bean.Daemon;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordUndo;
import org.cronhub.managesystem.commons.utils.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

/***
 * 利用反射去根据jdbc的ResultSet构造出bean类的对象
 * @author dd
 *
 */
public class BaseRowMapper implements RowMapper {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Class _class = null;
	/***
	 * 构造方法，传入class类型信息,比如Student.class,调用范例<br />
	 * List<TaskRecordUndo> records = this.jdbcTemplate.query(sql,new BaseRowMapper(TaskRecordUndo.class));
	 * 或
	 * final String findByIdSql = "SELECT id,machine_ip,machine_port,daemon_version_name,must_lostconn_email,lostconn_emailaddress,conn_status,comment,update_time FROM daemon WHERE id = ?";
	 * Daemon d = (Daemon)this.jdbcTemplate.queryForObject(findByIdSql, new Object[]{id},new BaseRowMapper(Daemon.class));
	 * 或
	 * TaskRecordUndo t=(TaskRecordUndo)this.jdbcTemplate.queryForObject(sql,new BaseRowMapper(TaskRecordUndo.class));
	 * @param _class
	 */
	public BaseRowMapper(Class _class){
		this._class = _class;
	}

	public Object mapRow(ResultSet rs, int index) throws SQLException {
		// TODO Auto-generated method stub
		
		List fieldList = BeanUtils.getFieldList(_class);
		Map setterMap = BeanUtils.getSetMethods(_class);
		Object obj = null;
		try {
			obj = BeanUtils.newInsatnce(_class.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block

			throw new SQLException("实例化类：" + _class.getName() + ",出错");
		}
		
		ResultSetMetaData rsm = rs.getMetaData();
		List columnList = new ArrayList();

		//从1开始
		for(int i = 1; i <= rsm.getColumnCount(); i++){
			columnList.add(rsm.getColumnName(i));
		}
		
		for(int i = 0; i < fieldList.size(); i++){
			Field field = (Field)fieldList.get(i);
			String fieldName = field.getName();
			
			//ResultSet 不存在该字段
			if(!columnList.contains(fieldName)){
				continue;
			}
			
			Method method = (Method)setterMap.get(fieldName);
			//该字段没有setter方法
			if(method == null){
				continue;
			}
			
			//调用方法,只是String类型
			//String args = rs.getString(fieldName);
			try {
				Class[] param_types = method.getParameterTypes();
				//method.invoke(obj, param_types[0].cast(args));
				String param_class = param_types[0].getCanonicalName();
				if(param_class.equals(String.class.getCanonicalName())){
					method.invoke(obj, rs.getString(fieldName));
				}else if(param_class.equals(Boolean.class.getCanonicalName())){
					method.invoke(obj, rs.getBoolean(fieldName));
				}else if(param_class.equals(Integer.class.getCanonicalName())){
					method.invoke(obj, rs.getInt(fieldName));
				}else if(param_class.equals(Double.class.getCanonicalName())){
					method.invoke(obj, rs.getDouble(fieldName));
				}else if(param_class.equals(Float.class.getCanonicalName())){
					method.invoke(obj, rs.getFloat(fieldName));
				}else if(param_class.equals(Date.class.getCanonicalName())){
					//mysql的datetime类型与java.utils.Date类型转换,再set进java bean
					Timestamp ts  = rs.getTimestamp(fieldName);
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(ts.getTime());
					method.invoke(obj,calendar.getTime());
				}else if(param_class.equals(Long.class.getCanonicalName())){
					method.invoke(obj, rs.getLong(fieldName));
				}
				
			} catch (Exception e) {
				throw new SQLException("调用类：" + _class.getName() + 
						"的方法:" + method.getName() + ",出错");
			}
		
		}
		
		return obj;
	}
		
	
}
