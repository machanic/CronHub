package org.cronhub.managesystem.commons.action;

import java.util.List;

import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;

/***
 * 泛型类，用于向分页的页面填充真正的数据<br />
 * 范例用法:<br />
 * IFindByPage<TaskRecordDone> ifinder = new IFindByPage<TaskRecordDone>(){
			@Override
			public List<TaskRecordDone> findByPage(int currentPage, int maxPerPage) {
				List<TaskRecordDone> findList =  doneRecordDao.findByPage(tableName,whereSql+orderSql.toString()+pageGen.generateFirst(maxPerPage),fillConfig);
				return findList;
			}
		};
	PageActionUtils.findFirstPage(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
 * @author mac
 *
 * @param <T>
 */
public interface IFindByPage<T> {
	public List<T> findByPage(int currentPage,int maxPerPage);
}
