package org.cronhub.managesystem.modules.record.done.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.params.Params;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.commons.utils.XmlUtils;
import org.cronhub.managesystem.modules.record.done.dao.IDoneRecordDao;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.opensymphony.xwork2.ActionSupport;

public class TreeLoadAction extends ActionSupport {
	private static final String prefixTableName = Params.TABLE_TASK_RECORD_DONE;
	private static final String icon_history = "/res/icons/16x16/clock.png";
	private static final String icon_today = "/res/icons/16x16/hourglass_add.png";
	private static final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
	private static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
	private  IDoneRecordDao doneRecordDone;
	private static Comparator<Element> dateDescComparator = new Comparator<Element>(){//最后递归排序,这样就可以页面上显示为最近时间再上的效果的"排序器"
		@Override
		public int compare(Element o1, Element o2) {
			return o2.attributeValue("id").compareTo(o1.attributeValue("id"));
		}
	};
	public void setDoneRecordDone(IDoneRecordDao doneRecordDone) {
		this.doneRecordDone = doneRecordDone;
	}
	public List<Element> loadDayDataToMonth(Element monthElement,Calendar todayCalendar){
		int id = Integer.parseInt(monthElement.attributeValue("id"));
		List<Element> days = loadDayDataToMonth(id,todayCalendar);
		for(Element day : days){
			monthElement.add(day);
		}
		return days;
		
	}
	public List<Element> loadDayDataToMonth(Integer yearMonth,Calendar todayCalendar){
		String tableName = prefixTableName+"_"+yearMonth;
		return loadDayDataToMonth(tableName,todayCalendar);
	}
	public List<Element> loadDayDataToMonth(String tableName,Calendar todayCalendar){
		List<Calendar> calendars = doneRecordDone.getAllDaysFromTable(tableName);
		List<Element> days = new ArrayList<Element>();
		for(Calendar cal : calendars){
			Element dayElement = DocumentHelper.createElement("node");
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH)+1; //Calendar的Month居然是从0开始的，详见API：一年中的第一个月是 JANUARY，它为 0；最后一个月取决于一年中的月份数。
			int day = cal.get(Calendar.DAY_OF_MONTH);//而Calendar的DAY_OF_MONTH是从1开始的,详见API:get 和 set 的字段数字，指示一个月中的某天。它与 DATE 是同义词。一个月中第一天的值为 1。 
			if((todayCalendar.get(Calendar.MONTH)+1) == month && todayCalendar.get(Calendar.YEAR) == year && todayCalendar.get(Calendar.DAY_OF_MONTH) == day){
				dayElement.addAttribute("icon", icon_today).addAttribute("type", "day");
			}else{
				dayElement.addAttribute("icon", icon_history).addAttribute("type", "day");
			}
			dayElement.addAttribute("id",dayFormat.format(cal.getTime())).addAttribute("name", String.format("%s月%s日", month,day)).addAttribute("isParent", "false").addAttribute("tableName", tableName);
			days.add(dayElement);
		}
		return days;
	}
	public String createInitTree(){
		HttpServletResponse response= ServletActionContext.getResponse();
		response.setHeader("pragma", "no-cache");
		response.setHeader("cache-control", "no-cache");
		response.setDateHeader("expires", 0);
		boolean unfoldCurrentMonth = true;
		HttpServletRequest req = ServletActionContext.getRequest();
		if(req.getParameterMap().containsKey(Params.PAGE_UNFOLD_CURRENT_MONTH)){
			unfoldCurrentMonth = Boolean.valueOf(req.getParameter(Params.PAGE_UNFOLD_CURRENT_MONTH));
		}
		if(req.getParameterMap().containsKey("id")){
			System.out.println("loading tree");
			loadDayFromMonthNode();
			return NONE;
		}
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");
		List<String> tableNames = doneRecordDone.getAllDoneTableName();
		Map<Integer,List<Integer>> year_months = new HashMap<Integer,List<Integer>>();
		for(String table : tableNames){
			String monthStr = table.substring(prefixTableName.length()+1);
			Date monthDate = null;
			Calendar monthCalendar = Calendar.getInstance();//先调用getInstance得到一个Calendar的对象
			try {
				monthDate = monthFormat.parse(monthStr);//将月份的String转换成Date类型,调用SimpleDateFormat的parse方法,而format方法则相反：将Date转成String
				monthCalendar.setTime(monthDate);//然后将Date的对象设置入Calendar调用setTime即可,随后Calendar就可以取出月份,年份,每月几号等信息了
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				AppLogger.errorLogger.error("init record done tree error!can not init month_format:"+monthStr,e);
			}
			int year = monthCalendar.get(Calendar.YEAR);
			int month = monthCalendar.get(Calendar.MONTH)+1;//Calendar的Month居然是从0开始的，详见API：一年中的第一个月是 JANUARY，它为 0；最后一个月取决于一年中的月份数。
			if(!year_months.containsKey(year)){
				year_months.put(year,new ArrayList<Integer>());
			}
			year_months.get(year).add(month);
		}
		Calendar todayCalendar = Calendar.getInstance();//先调用getInstance得到一个Calendar的对象
		todayCalendar.setTime(new Date());//然后将Date（这里为现场时间new Date）的对象设置入Calendar调用setTime即可,随后Calendar就可以取出月份,年份,每月几号等信息了
//		int currentYear = todayCalendar.get(Calendar.YEAR);
//		int currentMonth = todayCalendar.get(Calendar.MONTH)+1;//Calendar的Month居然是从0开始的，详见API：一年中的第一个月是 JANUARY，它为 0；最后一个月取决于一年中的月份数。
//		int currentDay = todayCalendar.get(Calendar.DAY_OF_MONTH);//而Calendar的DAY_OF_MONTH是从1开始的,详见API:get 和 set 的字段数字，指示一个月中的某天。它与 DATE 是同义词。一个月中第一天的值为 1。
		for(Map.Entry<Integer, List<Integer>> year_month : year_months.entrySet()){
			int year = year_month.getKey();
			Element yearElement = root.addElement("node");
			if(unfoldCurrentMonth && Collections.max(year_months.keySet()).equals(year)){//修改为最大年份的就展开
				yearElement.addAttribute("open", "true");
			}else{
				yearElement.addAttribute("open", "false");
			}
			yearElement.addAttribute("id",String.valueOf(year)).addAttribute("name", String.format("%s年", year)).addAttribute("type", "year").addAttribute("isParent", "false");
			for(int month : year_month.getValue()){
				Element monthElement = yearElement.addElement("node");
				if(unfoldCurrentMonth && Collections.max(year_month.getValue()).equals(month)){//修改此处bug,发现如果几天没有数据，但新来的一天是新的月的第一天，这样就不能展开文件夹了,修改为最大的月份就会展开
					monthElement.addAttribute("open", "true");//王波的ajax树的属性展开，则该文件夹展开了
				}else{
					monthElement.addAttribute("open", "false");//王波的ajax树的属性关闭，则该文件夹关闭了
				}
				Calendar ym = Calendar.getInstance();
				/**
				 * //Calendar的set有多种重载函数:1.前面代码中的set(int field, int value) 将给定的日历字段设置为给定值(field为Calendar的静态域)。
				 * 2.set(int year, int month, int date) 设置日历字段 YEAR、MONTH 和 DAY_OF_MONTH 的值。保留其他日历字段以前的值。如果不需要这样做，则先调用 clear()。注意： month - 用来设置 MONTH 日历字段的值。Month 值是基于 0 的。例如，0 表示 January。
				 */
				ym.set(year, month-1, 01);
				
				monthElement.addAttribute("id",monthFormat.format(ym.getTime())).addAttribute("name",String.format("%s月",month)).addAttribute("type", "month").addAttribute("isParent", "true");
				if(unfoldCurrentMonth && Collections.max(year_month.getValue()).equals(month)){ //修改为最大的月份会自动加载天的数据
					loadDayDataToMonth(monthElement,todayCalendar);
				}
			}
		}
		XmlUtils.sort(doc.getRootElement(),TreeLoadAction.dateDescComparator, true);//最后递归排序,这样就可以页面上显示为最近时间再上的效果了
		PageIOUtils.printToPage(doc);
		return NONE;
	}
	
	public void loadDayFromMonthNode(){
		HttpServletRequest req= ServletActionContext.getRequest();
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");
		int year_month = Integer.parseInt(req.getParameter("id"));
		Date today= new Date();
		Calendar todayCal = Calendar.getInstance();
		todayCal.setTime(today);
		List<Element> loadElement = loadDayDataToMonth(year_month,todayCal);
		for(Element each : loadElement){
			root.add(each);
		}
		XmlUtils.sort(doc.getRootElement(),TreeLoadAction.dateDescComparator, true);
		PageIOUtils.printToPage(doc);
	}
	
}
