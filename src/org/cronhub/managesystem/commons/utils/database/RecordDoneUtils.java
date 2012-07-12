package org.cronhub.managesystem.commons.utils.database;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordDoneUtils {
	private static SimpleDateFormat mounthTableFormat = new SimpleDateFormat("yyyyMM");
	public static String getTableName(Date date){
		return String.format("task_record_done_%s", mounthTableFormat.format(date));
	}
}
