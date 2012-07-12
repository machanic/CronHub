package org.cronhub.managesystem.commons.action;

import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.params.Params;

/***
 * 这个类主要用于将实际数据List,总共多少页,翻页后应该到第几页了,总共多少条记录等,页面上要用的放入Request，传回给页面,供展示用的类,没有涉及分页算法的计算
 * @author dd
 *
 */
public class PageActionUtils{
	public static <T> void findNextPageNo(IFindByPage<T> ifind,int totalCount){
		int currentPage = Integer.parseInt(ServletActionContext.getRequest().getParameter(Params.PAGE_CURRENT_PAGENO));
		int maxPerPage = Integer.parseInt(ServletActionContext.getRequest().getParameter(Params.PAGE_MAX_PERPAGE));
		List<T> showlist = ifind.findByPage( currentPage, maxPerPage);
		
		ServletActionContext.getRequest().setAttribute(Params.PAGE_VIEW_LIST, showlist);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_PAGE_COUNT,(int)Math.ceil((float)totalCount/(float)maxPerPage));//Math的ceil函数返回的float的上界
		ServletActionContext.getRequest().setAttribute(Params.PAGE_CURRENT_PAGENO,currentPage+1);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_COUNT,totalCount);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_MAX_PERPAGE,maxPerPage);
		if(ServletActionContext.getRequest().getParameterMap().containsKey(Params.FILTER)){
			ServletActionContext.getRequest().setAttribute(Params.FILTER, ServletActionContext.getRequest().getParameter(Params.FILTER));
		}
		
	}
	public static <T> void findPrevPageNo(IFindByPage<T> ifind,int totalCount){
		int currentPage = Integer.parseInt(ServletActionContext.getRequest().getParameter(Params.PAGE_CURRENT_PAGENO));
		int maxPerPage = Integer.parseInt(ServletActionContext.getRequest().getParameter(Params.PAGE_MAX_PERPAGE));
		List<T> showlist = ifind.findByPage( currentPage, maxPerPage);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_VIEW_LIST, showlist);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_PAGE_COUNT,(int)Math.ceil((float)totalCount/(float)maxPerPage));
		ServletActionContext.getRequest().setAttribute(Params.PAGE_CURRENT_PAGENO,currentPage-1);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_COUNT,totalCount);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_MAX_PERPAGE,maxPerPage);
		if(ServletActionContext.getRequest().getParameterMap().containsKey(Params.FILTER)){
			ServletActionContext.getRequest().setAttribute(Params.FILTER, ServletActionContext.getRequest().getParameter(Params.FILTER));
		}
	}
	public static <T> void findFirstPage(IFindByPage<T> ifind,int totalCount){
		int maxPerPage = Integer.parseInt(ServletActionContext.getRequest().getParameter(Params.PAGE_MAX_PERPAGE));
		List<T> showlist = ifind.findByPage( 0, maxPerPage);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_VIEW_LIST, showlist);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_PAGE_COUNT,(int)Math.ceil((float)totalCount/(float)maxPerPage));
		ServletActionContext.getRequest().setAttribute(Params.PAGE_CURRENT_PAGENO,1);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_COUNT,totalCount);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_MAX_PERPAGE,maxPerPage);
		if(ServletActionContext.getRequest().getParameterMap().containsKey(Params.FILTER)){
			ServletActionContext.getRequest().setAttribute(Params.FILTER, ServletActionContext.getRequest().getParameter(Params.FILTER));
		}
	}
	public static <T> void findLastPage(IFindByPage<T> ifind,int totalCount){
		int maxPerPage = Integer.parseInt(ServletActionContext.getRequest().getParameter(Params.PAGE_MAX_PERPAGE));
		int totalPageCount = (int)Math.ceil((float)totalCount/(float)maxPerPage);
		List<T> showlist = ifind.findByPage(totalPageCount, maxPerPage);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_VIEW_LIST, showlist);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_PAGE_COUNT,totalPageCount);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_CURRENT_PAGENO,totalPageCount);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_TOTAL_COUNT,totalCount);
		ServletActionContext.getRequest().setAttribute(Params.PAGE_MAX_PERPAGE,maxPerPage);
		if(ServletActionContext.getRequest().getParameterMap().containsKey(Params.FILTER)){
			ServletActionContext.getRequest().setAttribute(Params.FILTER, ServletActionContext.getRequest().getParameter(Params.FILTER));
		}
	}
}
