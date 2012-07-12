package org.cronhub.managesystem.commons.utils.time;

import java.util.Date;

public class TimeUtils {
	public static String getDuration(Date endDate,Date beginDate){
		long durationMillisecond= endDate.getTime() - beginDate.getTime();
		long day=durationMillisecond/(24*60*60*1000);
		long hour=(durationMillisecond/(60*60*1000)-day*24);
		long min=((durationMillisecond/(60*1000))-day*24*60-hour*60);
		long second=(durationMillisecond/1000-day*24*60*60-hour*60*60-min*60);
		StringBuilder ret = new StringBuilder();
		if(day > 0L){
			ret.append(day).append("天");
		}
		if(hour>0L){
			ret.append(hour).append("时");
		}
		if(min>0L){
			ret.append(min).append("分");
		}
		if(second>0L){
			ret.append(second).append("秒");
		}
		if(ret.toString().equals("")){
			ret.append("<1秒");
		}
		return ret.toString();
	}
}
