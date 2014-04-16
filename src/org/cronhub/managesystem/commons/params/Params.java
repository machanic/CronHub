package org.cronhub.managesystem.commons.params;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Params {
	public static final String TABLE_DAEMON = "daemon";
	public static final String TABLE_TASK = "task";
	public static final String TABLE_TASK_RECORD_DONE ="task_record_done";
	public static final String TABLE_TASK_RECORD_UNDO = "task_record_undo";
	public static final String PAGE_VIEW_LIST = "beanlist";
	public static final String PAGE_TOTAL_PAGE_COUNT = "total_page_count";
	public static final String PAGE_TOTAL_COUNT = "total_count";
	public static final String PAGE_CURRENT_PAGENO = "current_page_no";
	public static final String PAGE_MAX_PERPAGE = "max_per_page";
	public static final Integer DB_EXITCODE_ERROR_PING = -99;
	public static final String MACHINE_IP = "machine_ip";
	public static final String FILTER = "filter";
	public static final Integer EXECTYPE_CRONTAB = 0;
	public static final Integer EXECTYPE_BTNREDO = 1;
	public static final Integer EXECTYPE_AUTOREDO= 2;
	public static final Integer EXECTYPE_SPOT = 3;
	public static final String PAGE_UNFOLD_CURRENT_MONTH="unfold_current_month";
	public static final Integer TYPE_START_DATETIME = 0;
	public static final Integer TYPE_END_DATETIME = 1;
	public static final SimpleDateFormat date_format_page = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat time_format_page = new SimpleDateFormat("MM.dd(HH:mm:ss)");
	public static final String PAGE_RECORD_COMPLETE_STATS = "complete_stats";
	public static final String PAGE_RECORD_EXIT_CODE = "exit_code";
	public static final String PAGE_RECORD_END_DATETIME = "end_datetime";
	public static final String PAGE_RECORD_START_DATETIME = "start_datetime";
	public static final String PAGE_RECORD_DATETIME_INTERVAL = "datetime_interval";
	public static final String PAGE_RECORD_EXEC_TYPE = "exec_type";
	public static final String PAGE_RECORD_DURATION = "duration";
	public static final String PAGE_RECORD_EXEC_RETURN_STR = "exec_return_str";
	public static final String PAGE_CONN_LOST_MSG = "不能与服务器上的daemon通信";
	public static final String PAGE_IMG_LOADING_TAG ="<img src='/res/images/gif/loading.gif'/>";
	public static final Map<Integer,String> TYPE_DATETIME_MAP= new HashMap<Integer, String>();
	static{
		TYPE_DATETIME_MAP.put(TYPE_START_DATETIME, "start_datetime");
		TYPE_DATETIME_MAP.put(TYPE_END_DATETIME, "end_datetime");
	}
	public static final Map<Integer,String> EXECTYPE_REPRESENT = new HashMap<Integer,String>();
	static{
		EXECTYPE_REPRESENT.put(EXECTYPE_CRONTAB, "crontab");
		EXECTYPE_REPRESENT.put(EXECTYPE_BTNREDO, "button_redo");
		EXECTYPE_REPRESENT.put(EXECTYPE_AUTOREDO, "auto_redo");
		EXECTYPE_REPRESENT.put(EXECTYPE_SPOT, "on_spot_execute");
	}
	public static final ConcurrentHashMap<String, Long> REPORT_UNDO_IDENTIFIER_ID = new ConcurrentHashMap<String, Long>();
}
