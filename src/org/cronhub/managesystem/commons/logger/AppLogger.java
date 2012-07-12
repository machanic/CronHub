package org.cronhub.managesystem.commons.logger;


import org.apache.log4j.Logger;

public class AppLogger {
	// context log
	public static Logger validateLogger = Logger.getLogger("Validate");
	public static Logger errorLogger = Logger.getLogger("Error");
	public static Logger daemonErrorLogger = Logger.getLogger("DaemonError");
	public static Logger recordDoneErrorLogger = Logger.getLogger("RecordDoneError");
	public static Logger recordUndoLogger = Logger.getLogger("RecordUndoLogger");
	public static Logger recordDoneLogger = Logger.getLogger("RecordDoneLogger");
}
