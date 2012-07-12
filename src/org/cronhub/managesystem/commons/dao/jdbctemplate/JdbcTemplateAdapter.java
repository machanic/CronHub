package org.cronhub.managesystem.commons.dao.jdbctemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * 数据存取适配器
 * @author David Day
 */
public class JdbcTemplateAdapter extends JdbcTemplate {
	
	public JdbcTemplateAdapter() {
		super();
	}
	
	public JdbcTemplateAdapter(DataSource ds) {
		super(ds);
	}
	
	/**
	 * 增加并且获取主键
	 * @param sql sql语句
	 * @param params 参数列表
	 * @return 主键
	 */
	public Object insertAndGetKey(final String sql) {
		logger.debug("Executing SQL update and returning generated keys");
		
		final KeyHolder key = new GeneratedKeyHolder();

		update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, 
						PreparedStatement.RETURN_GENERATED_KEYS);
				return ps;
			}
			
		}, key);
		
		return key.getKey();
	}

}
