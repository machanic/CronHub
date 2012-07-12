package org.cronhub.managesystem.modules.daemon.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.action.IFindByPage;
import org.cronhub.managesystem.commons.action.PageActionUtils;
import org.cronhub.managesystem.commons.dao.bean.Daemon;
import org.cronhub.managesystem.commons.dao.config.AssociateDeleteConfig;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.thrift.call.IExecuter;
import org.cronhub.managesystem.commons.thrift.call.RemoteCaller;
import org.cronhub.managesystem.commons.thrift.gen.ExecutorService.Client;
import org.cronhub.managesystem.commons.utils.FilterSqlGenerater;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.commons.utils.PageSqlGenerater;
import org.cronhub.managesystem.modules.daemon.dao.IDaemonDao;

import com.opensymphony.xwork2.ActionSupport;

public class PageViewAction extends ActionSupport {
	private PageSqlGenerater pageGen;
	private IDaemonDao dao;
	private static final String order_update_time_sql= " ORDER BY update_time DESC";
	private String updateShellName;
	private String updateShellDownloadUrl;
	
	public void setUpdateShellDownloadUrl(String updateShellDownloadUrl) {
		this.updateShellDownloadUrl = updateShellDownloadUrl;
	}
	public void setUpdateShellName(String updateShellName) {
		this.updateShellName = updateShellName;
	}
	public void setDao(IDaemonDao dao) {
		this.dao = dao;
	}
	public void setPageGen(PageSqlGenerater pageGen) {
		this.pageGen = pageGen;
	}
	
	public String findNextPageNo(){
		//增加分页过滤条件
		final String whereSql = FilterSqlGenerater.genWhereSql();
		IFindByPage<Daemon> ifinder = new IFindByPage<Daemon>(){
			@Override
			public List<Daemon> findByPage(int currentPage, int maxPerPage) {
				return dao.findByPage(whereSql+order_update_time_sql+pageGen.generateNext(Params.TABLE_DAEMON, currentPage, maxPerPage,whereSql));//这里的pageGen.generatePrev才是真正的生成mysql的limit造成实质分页的算法。
				//其实调用了dao.findByPage这个东西，dao.findByPage从形参这拿到了where和limit，与他内部的select一拼接，就完成了sql的拼接，就拿到了数据。
			}
		};//ifinder这个接口是核心思想,这个ifinder提供具体的数据List,由于是为了应对不同的类型比如Daemon和Task以及Record类都支持，而通用的因此这个IFindByPage定义为了泛型接口,然后传入下面的PageActionUtils中。这里用了"匿名内部类"。
        //PageActionUtils提供四个静态函数,1.获取第一页，并利用Request发送到页面上的,2.获取下一页，3.获取前一页,4.获取最后一页
		PageActionUtils.findNextPageNo(ifinder, this.pageGen.getTotalCountFromTable(Params.TABLE_DAEMON,whereSql));//这个里面需要传入三样参数,1:ifinder提供具体数据,2:根据where
		return SUCCESS;
	}
	public String findPrevPageNo(){
		//增加分页过滤条件
		final String whereSql = FilterSqlGenerater.genWhereSql();
		IFindByPage<Daemon> ifinder = new IFindByPage<Daemon>(){
			@Override
			public List<Daemon> findByPage(int currentPage, int maxPerPage) {
				return dao.findByPage(whereSql+order_update_time_sql+pageGen.generatePrev(currentPage, maxPerPage));
			}
		};
		PageActionUtils.findPrevPageNo(ifinder, this.pageGen.getTotalCountFromTable(Params.TABLE_DAEMON,whereSql));
		return SUCCESS;
	}
	public String findFirstPage(){
		//增加分页过滤条件
		final String whereSql = FilterSqlGenerater.genWhereSql();
		IFindByPage<Daemon> ifinder = new IFindByPage<Daemon>(){
			@Override
			public List<Daemon> findByPage(int currentPage, int maxPerPage) {
				return dao.findByPage(whereSql+order_update_time_sql+pageGen.generateFirst(maxPerPage));
			}
		};
		PageActionUtils.findFirstPage(ifinder, this.pageGen.getTotalCountFromTable(Params.TABLE_DAEMON,whereSql));
		return SUCCESS;
		
	}
	public String findLastPage(){
		//增加分页过滤条件
		final String whereSql = FilterSqlGenerater.genWhereSql();
		IFindByPage<Daemon> ifinder = new IFindByPage<Daemon>(){
			@Override
			public List<Daemon> findByPage(int currentPage, int maxPerPage) {
				return dao.findByPage(whereSql+order_update_time_sql+pageGen.generateLast(Params.TABLE_DAEMON, maxPerPage,whereSql));
			}
		};
		PageActionUtils.findLastPage(ifinder, this.pageGen.getTotalCountFromTable(Params.TABLE_DAEMON,whereSql));
		return SUCCESS;
	}
	public String deleteOneById(){
		try{
			Long id = Long.valueOf(ServletActionContext.getRequest().getParameter("id"));
			AssociateDeleteConfig config = new AssociateDeleteConfig(true,true,true);
			this.dao.deleteById(id, config);
			PageIOUtils.printToPage("success");
		}catch(Exception e){
			PageIOUtils.printToPage("fail");
		}
		return NONE;
	}
	
	
	
	/***
	 * 升级daemon包,由于update时包被kill掉后，会中断通信。所以这个功能暂未完成
	 * @return
	 */
	public String updateDaemonPackage(){
		HttpServletRequest req = ServletActionContext.getRequest();
		Long id = Long.valueOf(req.getParameter("id"));
		Daemon daemon = this.dao.findById(id);
		final String ip = daemon.getMachine_ip();
		final Integer port = daemon.getMachine_port();
		JSONObject ajaxJson = new JSONObject();
		String result = "fail";
		try {
		IExecuter executer_project_path = new IExecuter(){
			@Override
			public Object execute(Client client) throws Exception {
				return client.getProjectFolderPath();
			}
		};
		String folder_path = RemoteCaller.call(ip, port,executer_project_path, null).toString();
		//final String updateRemoteExecShell = String.format("wget %s -O %s/%s;if [ $? -eq 0 ];then sh %s/%s %s && echo update_success || echo update_fail;else echo 'cannot download from %s';fi",updateShellDownloadUrl, folder_path, updateShellName,folder_path, updateShellName,port,updateShellDownloadUrl);
		final String updateRemoteExecShell = String.format("wget %s -O %s/%s;if [ $? -eq 0 ];then cd %s && nohup sh %s %s &  else echo 'cannot download from %s';fi",updateShellDownloadUrl, folder_path, updateShellName,folder_path, updateShellName,port,updateShellDownloadUrl);
		System.out.println(updateRemoteExecShell);
		IExecuter executer = new IExecuter() {
				@Override
				public Object execute(Client client) throws Exception {
					return client.update(updateRemoteExecShell);
				}
			};
		String remote_result = RemoteCaller.call(ip, port, executer, null).toString();
		result = remote_result.contains("update_success") ? "success": "fail";
		if(remote_result.contains("download") || result.equals("fail")){
			ajaxJson.put("error", remote_result.toString());
		}
		} catch (Exception e) {
			ajaxJson.put("error", "服务器[ip:" + ip + ",端口port:" + port
					+ "]不能通信,不能升级daemon包,请检查");
			ajaxJson.put("id", id);
			ajaxJson.put("status", "fail");
			PageIOUtils.printToPage(ajaxJson.toString());
			return NONE;
		}
		ajaxJson.put("id", id);
		ajaxJson.put("status",result);
		PageIOUtils.printToPage(ajaxJson.toString());
		return NONE;
	}
}
