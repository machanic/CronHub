package org.cronhub.managesystem.commons.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.params.Params;

/***
 * 利用{"equal":{"state":"success","mode":"true"},"like":{"ip":"230.91"}}生成形如WHERE state = 'success' AND mode = 'true' AND ip LIKE '%230.91%'这样的SQL
 * @author mac
 *
 */
public class FilterSqlGenerater {
	public static final String EQUAL = "equal";
	public static final String LIKE = "like";
	public static final String CUSTOM = "custom";
	public static final String genWhereSql(){
		if(ServletActionContext.getRequest().getParameterMap().containsKey(Params.FILTER)){
		String filterJson = ServletActionContext.getRequest().getParameter(Params.FILTER);
		return genWhereSql(filterJson);
		}
		return "";
	}
	public static final String genWhereSql(String filter_json_string){
		JSONObject filter_jsons = JSONObject.fromObject(filter_json_string);
		return genWhereSql(filter_jsons);
	}
	
//	public static final String genWhereSql(JSONArray filter_jsons){
//		StringBuilder whereSql = new StringBuilder(" WHERE ");
//		List<String> eachStmt = new ArrayList<String>();
//		for(Iterator iter= filter_jsons.iterator();iter.hasNext();){
//			JSONArray perList = (JSONArray)iter.next();
//			Object[] perArray = perList.toArray();
//			if(perArray[0].toString().equals(FilterSqlGenerater.EQUAL)){
//				JSONObject equalJson = perList.getJSONObject(1);
//				for(Object key : equalJson.keySet()){
//					eachStmt.add(String.format("%s = '%s'", key.toString(),equalJson.get(key)));
//				}
//			}else if(perArray[0].toString().equals(FilterSqlGenerater.LIKE)){
//				JSONObject likeJson = perList.getJSONObject(1);
//				for(Object key : likeJson.keySet()){
//					eachStmt.add(key.toString()+" LIKE '%"+likeJson.get(key).toString()+"%'");
//				}
//			}
//		}
//		whereSql.append(StringUtils.join(eachStmt," AND ")).append(" ");
//		return whereSql.toString();
//	}
	
	public static final String genWhereSql(JSONObject filter_json){
		StringBuilder whereSql = new StringBuilder(" WHERE ");
		List<String> eachStmt = new ArrayList<String>();
		if(filter_json.containsKey(FilterSqlGenerater.CUSTOM)){
			JSONObject customJson = JSONObject.fromObject(filter_json.get(FilterSqlGenerater.CUSTOM));
			for (Object customKey : customJson.keySet()) {
				String key = customKey.toString();
				eachStmt.add(String.format("%s %s",key.toString(),customJson.get(customKey)));
			}
		}
		if (filter_json.containsKey(FilterSqlGenerater.EQUAL)) {
			JSONObject equalJson = JSONObject.fromObject(filter_json.get(FilterSqlGenerater.EQUAL));
			for (Object equalKey : equalJson.keySet()) {
				String key = equalKey.toString();
				eachStmt.add(String.format("%s = '%s'", key.toString(),
						equalJson.get(equalKey)));
			}
		}
		if(filter_json.containsKey(FilterSqlGenerater.LIKE)){
			JSONObject likeJson = JSONObject.fromObject(filter_json.get(FilterSqlGenerater.LIKE));
			for (Object likeKey : likeJson.keySet()) {
				String key = likeKey.toString();
				eachStmt.add(key.toString()+" LIKE '%"+likeJson.get(likeKey).toString()+"%'");
			}
		}
		whereSql.append(StringUtils.join(eachStmt," AND ")).append(" ");
		return whereSql.toString();
	}
	public static void main(String[] args) {
		System.out.println(FilterSqlGenerater.genWhereSql("{\"equal\":{\"type\":1,\"live\":false},\"like\":{\"ip\":\"192\"},\"custom\":{\"end_times\":\">'3'\"}}"));
	}
}
