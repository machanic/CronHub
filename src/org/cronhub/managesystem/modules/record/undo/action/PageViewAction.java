package org.cronhub.managesystem.modules.record.undo.action;

import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.action.IFindByPage;
import org.cronhub.managesystem.commons.action.PageActionUtils;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordUndo;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.commons.utils.FilterSqlGenerater;
import org.cronhub.managesystem.commons.utils.PageSqlGenerater;
import org.cronhub.managesystem.modules.record.undo.dao.IUndoRecordDao;

import com.opensymphony.xwork2.ActionSupport;

public class PageViewAction extends ActionSupport {
	private PageSqlGenerater pageGen;
	public void setPageGen(PageSqlGenerater pageGen) {
		this.pageGen = pageGen;
	}
	private IUndoRecordDao undoRecordDao;
	public void setUndoRecordDao(IUndoRecordDao undoRecordDao) {
		this.undoRecordDao = undoRecordDao;
	}

	private static final String defaultOrderBy = " ORDER BY start_datetime DESC";
	public String findFirstPage(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if(ServletActionContext.getRequest().getParameterMap().containsKey("sort_column") && ServletActionContext.getRequest().getParameterMap().containsKey("sort_order")){
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY "+ServletActionContext.getRequest().getParameter("sort_column")+" "+ServletActionContext.getRequest().getParameter("sort_order"));
		}
		final String join_table = "(task_record_undo INNER JOIN task ON task_record_undo.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordUndo> ifinder = new IFindByPage<TaskRecordUndo>(){
			@Override
			public List<TaskRecordUndo> findByPage(int currentPage, int maxPerPage) {
				List<TaskRecordUndo> findList =  undoRecordDao.findByPage(join_table,whereSql+orderSql.toString()+pageGen.generateFirst(maxPerPage),fillConfig);
				return findList;
			}
		};
		PageActionUtils.findFirstPage(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		ServletActionContext.getRequest().setAttribute("tableName",join_table);
		return SUCCESS;
	}
	
	public String findNextPageNo(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if(ServletActionContext.getRequest().getParameterMap().containsKey("sort_column") && ServletActionContext.getRequest().getParameterMap().containsKey("sort_order")){
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY "+ServletActionContext.getRequest().getParameter("sort_column")+" "+ServletActionContext.getRequest().getParameter("sort_order"));
		}
		final String join_table = "(task_record_undo INNER JOIN task ON task_record_undo.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordUndo> ifinder = new IFindByPage<TaskRecordUndo>(){
			@Override
			public List<TaskRecordUndo> findByPage(int currentPage, int maxPerPage) {
				List<TaskRecordUndo> findList =  undoRecordDao.findByPage(join_table,whereSql+orderSql.toString()+pageGen.generateNext(join_table, currentPage, maxPerPage, whereSql),fillConfig);
				return findList;
			}
		};
		PageActionUtils.findNextPageNo(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		ServletActionContext.getRequest().setAttribute("tableName",join_table);
		return SUCCESS;
	}
	
	public String findPrevPageNo(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if(ServletActionContext.getRequest().getParameterMap().containsKey("sort_column") && ServletActionContext.getRequest().getParameterMap().containsKey("sort_order")){
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY "+ServletActionContext.getRequest().getParameter("sort_column")+" "+ServletActionContext.getRequest().getParameter("sort_order"));
		}
		final String join_table = "(task_record_undo INNER JOIN task ON task_record_undo.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordUndo> ifinder = new IFindByPage<TaskRecordUndo>(){
			@Override
			public List<TaskRecordUndo> findByPage(int currentPage, int maxPerPage) {
				List<TaskRecordUndo> findList =  undoRecordDao.findByPage(join_table,whereSql+orderSql.toString()+pageGen.generatePrev(currentPage, maxPerPage),fillConfig);
				return findList;
			}
		};
		PageActionUtils.findPrevPageNo(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		ServletActionContext.getRequest().setAttribute("tableName",join_table);
		return SUCCESS;
	}
	
	public String findLastPage(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if(ServletActionContext.getRequest().getParameterMap().containsKey("sort_column") && ServletActionContext.getRequest().getParameterMap().containsKey("sort_order")){
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY "+ServletActionContext.getRequest().getParameter("sort_column")+" "+ServletActionContext.getRequest().getParameter("sort_order"));
		}
		final String join_table = "(task_record_undo INNER JOIN task ON task_record_undo.task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordUndo> ifinder = new IFindByPage<TaskRecordUndo>(){
			@Override
			public List<TaskRecordUndo> findByPage(int currentPage, int maxPerPage) {
				List<TaskRecordUndo> findList =  undoRecordDao.findByPage(join_table,whereSql+orderSql.toString()+pageGen.generateLast(join_table, maxPerPage, whereSql),fillConfig);
				return findList;
			}
		};
		PageActionUtils.findLastPage(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		ServletActionContext.getRequest().setAttribute("tableName",join_table);
		return SUCCESS;
	}
	
}
