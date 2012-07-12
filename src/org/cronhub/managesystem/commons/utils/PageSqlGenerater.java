package org.cronhub.managesystem.commons.utils;

import org.springframework.jdbc.core.JdbcTemplate;
/***
 * 这个类就是根据"下一页","上一页","首页","末页",生成mysql的LIMIT的算法工具类
 * @author dd
 *
 */
public class PageSqlGenerater {
	private JdbcTemplate template;
	
	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}
	public int getTotalCountFromTable(String tableName){
		final String sql = "SELECT COUNT(*) c FROM "+tableName;
		return this.template.queryForInt(sql);
	}
	public int getTotalCountFromTable(String tableName,String whereSql){
		final String sql = "SELECT COUNT(*) c FROM "+tableName+" "+whereSql;
		return this.template.queryForInt(sql);
	}
	public int getTotalPageCountFromTable(String tableName,int maxPerPage){
		int allCount = this.getTotalCountFromTable(tableName);
		return  (int)Math.ceil((float)allCount/(float)maxPerPage);
	}
	public int getTotalPageCountFromTable(String tableName,int maxPerPage,String whereSql){
		int allCount = this.getTotalCountFromTable(tableName,whereSql);
		return  (int)Math.ceil((float)allCount/(float)maxPerPage);
	}
	public String generateNext(String tableName,int currentPageNo,int maxPerPage,String whereSql){
		int allPageNum = getTotalPageCountFromTable(tableName,maxPerPage,whereSql);
		int nextPageNo = currentPageNo+1;
		int startIndex =0;
		if(nextPageNo <= allPageNum){
			startIndex = currentPageNo*maxPerPage;
		}else{
			startIndex = (currentPageNo-1)*maxPerPage;
		}
		return String.format(" LIMIT %s,%s",startIndex,maxPerPage);
		
		
	}
	public String generatePrev(int currentPageNo,int maxPerPage){
		int prevPageNo = currentPageNo - 1;
		int startIndex =0;
		if(prevPageNo>0){
			startIndex = (prevPageNo-1)*maxPerPage;
		}else{
			startIndex = 0;
		}
		return String.format(" LIMIT %s,%s",startIndex,maxPerPage);
	}
	public String generateFirst(int maxPerPage){
		return String.format(" LIMIT 0,%s",maxPerPage);
	}
	public String generateLast(String tableName,int maxPerPage,String whereSql){
		int allPageNum = getTotalPageCountFromTable(tableName,maxPerPage,whereSql);
		int startIndex= maxPerPage*(allPageNum-1);
		return String.format(" LIMIT %s,%s",startIndex,maxPerPage);
	}
}
