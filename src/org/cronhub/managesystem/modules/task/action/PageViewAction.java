package org.cronhub.managesystem.modules.task.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.action.IFindByPage;
import org.cronhub.managesystem.commons.action.PageActionUtils;
import org.cronhub.managesystem.commons.dao.bean.Task;
import org.cronhub.managesystem.commons.dao.config.AssociateDeleteConfig;
import org.cronhub.managesystem.commons.dao.config.FillConfig;
import org.cronhub.managesystem.commons.utils.FilterSqlGenerater;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.commons.utils.PageSqlGenerater;
import org.cronhub.managesystem.modules.task.dao.ITaskDao;

import com.opensymphony.xwork2.ActionSupport;

public class PageViewAction extends ActionSupport {
	private PageSqlGenerater pageGen;
	private ITaskDao dao;
	private static final String join_table = " task LEFT JOIN daemon on task.daemon_id = daemon.id ";
	public void setPageGen(PageSqlGenerater pageGen) {
		this.pageGen = pageGen;
	}
	public void setDao(ITaskDao dao) {
		this.dao = dao;
	}
	private static final String order_update_time_sql= " ORDER BY update_time DESC";
	/***
	private static Comparator<Task> machineIpSort = new Comparator<Task>(){
		@Override
		public int compare(Task o1, Task o2) {
			int result=  o1.getDaemon().getMachine_ip().compareTo(o2.getDaemon().getMachine_ip());
			if (0==result){
				result = o1.getUpdate_time().compareTo(o2.getUpdate_time());
			}
			return result;
		}
	};
	**/
	public String findFirstPage(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillDaemonInstance();
		IFindByPage<Task> ifinder = new IFindByPage<Task>(){
			@Override
			public List<Task> findByPage(int currentPage, int maxPerPage) {
				List<Task> findList =  dao.findByPage(whereSql+order_update_time_sql+pageGen.generateFirst(maxPerPage),fillConfig);
				//Collections.sort(findList, machineIpSort);
				return findList;
			}
		};
		PageActionUtils.findFirstPage(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		return SUCCESS;
	}
	public String findNextPageNo(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillDaemonInstance();
		IFindByPage<Task> ifinder = new IFindByPage<Task>(){
			@Override
			public List<Task> findByPage(int currentPage, int maxPerPage) {
				List<Task> findList =  dao.findByPage(whereSql+order_update_time_sql+pageGen.generateNext(join_table, currentPage, maxPerPage,whereSql),fillConfig);
				//Collections.sort(findList, machineIpSort);
				return findList;
			}
		};
		PageActionUtils.findNextPageNo(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		return SUCCESS;
	}
	public String findPrevPageNo(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillDaemonInstance();
		IFindByPage<Task> ifinder = new IFindByPage<Task>(){
			@Override
			public List<Task> findByPage(int currentPage, int maxPerPage) {
				List<Task> findList =  dao.findByPage(whereSql+order_update_time_sql+pageGen.generatePrev(currentPage, maxPerPage),fillConfig);
				//Collections.sort(findList, machineIpSort);
				return findList;
			}
		};
		PageActionUtils.findPrevPageNo(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		return SUCCESS;
	}
	public String findLastPage(){
		final String whereSql = FilterSqlGenerater.genWhereSql();
		final FillConfig fillConfig = FillConfig.getFillDaemonInstance();
		IFindByPage<Task> ifinder = new IFindByPage<Task>(){
			@Override
			public List<Task> findByPage(int currentPage, int maxPerPage) {
				List<Task> findList =  dao.findByPage(whereSql+order_update_time_sql+pageGen.generateLast(join_table, maxPerPage,whereSql),fillConfig);
				//Collections.sort(findList, machineIpSort);
				return findList;
			}
		};
		PageActionUtils.findLastPage(ifinder, this.pageGen.getTotalCountFromTable(join_table,whereSql));
		return SUCCESS;
	}
	public String deleteOneById(){
		Long id = Long.valueOf(ServletActionContext.getRequest().getParameter("id"));
		AssociateDeleteConfig config = new AssociateDeleteConfig(true,true,true);
		this.dao.deleteById(id, config);
		PageIOUtils.printToPage("success");
		return NONE;
	}
}
