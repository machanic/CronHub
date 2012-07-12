package org.cronhub.managesystem.commons.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class PageIOUtils {
	public static void printToPage(String message){
		 PrintWriter out = null;
	       try {
				out = ServletActionContext.getResponse().getWriter();
				out.print(message);
				out.flush();
			}catch(IOException e){
				AppLogger.errorLogger.error("error in get PrintWriter to page",e);
			}finally{
				if(null!=out){
				out.close();
				}
			}
	}
	public static void printToPage(Document doc){
		//输出良好排版的xml
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter out = null;
		XMLWriter outWriter = null;
		try{
		OutputFormat format = OutputFormat.createPrettyPrint(); 
		format.setEncoding("UTF-8");//设置编码为UTF-8
		out = response.getWriter();
		outWriter = new XMLWriter(out,format);//将response.writer传入构造方法，包装他,java的流支持"装饰器模式"
		outWriter.write(doc);//现在用XMLWriter类输出了，因为包装了response.writer，所以不用response.writer也具有了他的"输出到页面"了
		outWriter.flush();//将剩余信息输出，这句话必要
		}catch(IOException e){
			AppLogger.errorLogger.error("error in write xml to page",e);
		}finally{
			if(null!=out){
				out.close();
			}
			if(null!=outWriter){
				try {
					outWriter.close();
				} catch (IOException e) {
					AppLogger.errorLogger.error("error in write xml to page",e);
				}
			}
		}
	}
}
