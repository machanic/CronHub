package org.cronhub.managesystem.commons.logger;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Appender;
import org.apache.log4j.WriterAppender;


public class LoggerToReader {
	public static PrintWriter getFromAppender(Appender appender){
		PrintWriter writer= null;
		try{
			writer = new PrintWriter(System.out); 
		((WriterAppender)appender).setWriter(writer);
		}catch (Exception e) {
		}
		return writer;
	}
	public static List<String> getStringFromReader(Reader reader){
		Scanner scanner = new Scanner(reader);
		List<String> strings = new ArrayList<String>();
		String line = "";
		while(scanner.hasNext()){
			strings.add(scanner.nextLine());
			System.out.println("!!!read"+strings.get(strings.size()-1));
		}
		return strings;
	}
	
}
