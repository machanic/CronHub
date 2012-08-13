package org.cronhub.managesystem.modules.record.done.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.action.IFindByPage;
import org.cronhub.managesystem.commons.action.PageActionUtils;
import org.cronhub.managesystem.commons.dao.bean.TaskRecordDone;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.commons.utils.FilterSqlGenerater;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.commons.utils.PageSqlGenerater;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;

import com.opensymphony.xwork2.ActionSupport;

public class PageViewAction extends ActionSupport {
	private PageSqlGenerater pageGen;

	public void setPageGen(PageSqlGenerater pageGen) {
		this.pageGen = pageGen;
	}

	private IDoneRecordDao doneRecordDao;

	public void setDoneRecordDao(IDoneRecordDao doneRecordDao) {
		this.doneRecordDao = doneRecordDao;
	}

	private static final String defaultOrderBy = "ORDER BY end_datetime DESC";

	public String findFirstPage() {
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if (ServletActionContext.getRequest().getParameterMap().containsKey("sort_column") && ServletActionContext.getRequest().getParameterMap().containsKey("sort_order")) {
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY " + ServletActionContext.getRequest().getParameter("sort_column")+ " " + ServletActionContext.getRequest().getParameter("sort_order"));
		}
		final String tableName = ServletActionContext.getRequest()
				.getParameter("tableName");
		final String join_table = "(" + tableName + " INNER JOIN task ON " + tableName + ".task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordDone> ifinder = new IFindByPage<TaskRecordDone>() {
			@Override
			public List<TaskRecordDone> findByPage(int currentPage,
					int maxPerPage) {
				List<TaskRecordDone> findList = doneRecordDao
						.findByPage(tableName, whereSql + orderSql.toString()
								+ pageGen.generateFirst(maxPerPage), fillConfig);
				return findList;
			}
		};
		PageActionUtils.findFirstPage(ifinder, this.pageGen
				.getTotalCountFromTable(join_table, whereSql));
		ServletActionContext.getRequest().setAttribute("tableName", tableName);
		return SUCCESS;
	}

	public String findNextPageNo() {
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if (ServletActionContext.getRequest().getParameterMap().containsKey(
				"sort_column")
				&& ServletActionContext.getRequest().getParameterMap()
						.containsKey("sort_order")) {
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY "
					+ ServletActionContext.getRequest().getParameter(
							"sort_column")
					+ " "
					+ ServletActionContext.getRequest().getParameter(
							"sort_order"));
		}
		final String tableName = ServletActionContext.getRequest()
				.getParameter("tableName");
		final String join_table = "("
				+ tableName
				+ " INNER JOIN task ON "
				+ tableName
				+ ".task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordDone> ifinder = new IFindByPage<TaskRecordDone>() {
			@Override
			public List<TaskRecordDone> findByPage(int currentPage,
					int maxPerPage) {
				List<TaskRecordDone> findList = doneRecordDao.findByPage(
						tableName, whereSql
								+ orderSql.toString()
								+ pageGen.generateNext(join_table, currentPage,
										maxPerPage, whereSql), fillConfig);
				return findList;
			}
		};
		PageActionUtils.findNextPageNo(ifinder, this.pageGen
				.getTotalCountFromTable(join_table, whereSql));
		ServletActionContext.getRequest().setAttribute("tableName", tableName);
		return SUCCESS;
	}

	public String findPrevPageNo() {
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if (ServletActionContext.getRequest().getParameterMap().containsKey("sort_column")
				&& ServletActionContext.getRequest().getParameterMap()
						.containsKey("sort_order")) {
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY "
					+ ServletActionContext.getRequest().getParameter(
							"sort_column")
					+ " "
					+ ServletActionContext.getRequest().getParameter(
							"sort_order"));
		}
		final String tableName = ServletActionContext.getRequest()
				.getParameter("tableName");
		final String join_table = "("
				+ tableName
				+ " INNER JOIN task ON "
				+ tableName
				+ ".task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordDone> ifinder = new IFindByPage<TaskRecordDone>() {
			@Override
			public List<TaskRecordDone> findByPage(int currentPage,
					int maxPerPage) {
				List<TaskRecordDone> findList = doneRecordDao
						.findByPage(tableName,
								whereSql
										+ orderSql.toString()
										+ pageGen.generatePrev(currentPage,
												maxPerPage), fillConfig);
				return findList;
			}
		};
		PageActionUtils.findPrevPageNo(ifinder, this.pageGen
				.getTotalCountFromTable(join_table, whereSql));
		ServletActionContext.getRequest().setAttribute("tableName", tableName);
		return SUCCESS;
	}

	public String findLastPage() {
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillAllInstance();
		final StringBuilder orderSql = new StringBuilder(defaultOrderBy);
		if (ServletActionContext.getRequest().getParameterMap().containsKey(
				"sort_column")
				&& ServletActionContext.getRequest().getParameterMap()
						.containsKey("sort_order")) {
			orderSql.delete(0, orderSql.length());
			orderSql.append(" ORDER BY "
					+ ServletActionContext.getRequest().getParameter(
							"sort_column")
					+ " "
					+ ServletActionContext.getRequest().getParameter(
							"sort_order"));
		}
		final String tableName = ServletActionContext.getRequest()
				.getParameter("tableName");
		final String join_table = "("
				+ tableName
				+ " INNER JOIN task ON "
				+ tableName
				+ ".task_id = task.id) INNER JOIN daemon ON task.daemon_id = daemon.id";
		IFindByPage<TaskRecordDone> ifinder = new IFindByPage<TaskRecordDone>() {
			@Override
			public List<TaskRecordDone> findByPage(int currentPage,
					int maxPerPage) {
				List<TaskRecordDone> findList = doneRecordDao.findByPage(
						tableName, whereSql
								+ orderSql.toString()
								+ pageGen.generateLast(join_table, maxPerPage,
										whereSql), fillConfig);
				return findList;
			}
		};
		PageActionUtils.findLastPage(ifinder, this.pageGen
				.getTotalCountFromTable(join_table, whereSql));
		ServletActionContext.getRequest().setAttribute("tableName", tableName);
		return SUCCESS;
	}

	public String getExecReturnString() {
		HttpServletRequest req = ServletActionContext.getRequest();
		Long id = Long.valueOf(req.getParameter("id"));
		String tableName = req.getParameter("tableName");
		FillConfig fillConfig = new FillConfig(false, false);
		TaskRecordDone record = this.doneRecordDao.findById(id, tableName,
				fillConfig);
		JSONObject ajaxJson = new JSONObject();
		ajaxJson.put("id", id);
		ajaxJson.put("return_str", record.getExec_return_str());
		PageIOUtils.printToPage(ajaxJson.toString());
		return NONE;
	}
}
