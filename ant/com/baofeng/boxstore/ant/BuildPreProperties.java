package com.baofeng.boxstore.ant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class BuildPreProperties extends Task {
	private String modRefFilePath;
	private String changeDestXmlFilePath;
	public void setModRefFilePath(String modRefFilePath) {
		this.modRefFilePath = modRefFilePath;
	}

	public void setChangeDestXmlFilePath(String changeDestXmlFilePath) {
		this.changeDestXmlFilePath = changeDestXmlFilePath;
	}
	private Document readDocumentFile(String filePath) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new File(filePath));
		return doc;
	}
	private void saveDocument(Document root, String filename) throws IOException{
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"), OutputFormat.createPrettyPrint());
		writer.write(root); 
		writer.close();
	}
	private String findModDestPath(String classPath) throws UnsupportedEncodingException{
		String path =  classPath.substring(0,classPath.indexOf("WebRoot"))+"/ant/";
		return URLDecoder.decode(path, "UTF-8");
	}
	public void execute() throws BuildException{
		try {
			String realModDestPath = findModDestPath(this.getClass().getClassLoader().getResource("").getPath())+this.changeDestXmlFilePath;
			String realModRef = findModDestPath(this.getClass().getClassLoader().getResource("").getPath())+modRefFilePath;
			System.out.println("execute mod:"+realModDestPath+" from:"+realModRef);
			Properties properties = new Properties();
			properties.load(new FileInputStream(realModRef));
			Document doc = readDocumentFile(realModDestPath);
			Element target =(Element) doc.selectNodes("//target").get(0);
			for(Object old : target.elements("replaceregexp")){
				Element rep = (Element)old;
				target.remove(rep);
			}
			for( Enumeration e=properties.propertyNames();e.hasMoreElements();){
				String key = (String)e.nextElement();
//				String value = properties.getProperty(key);
				
				Element replaceregexp = target.addElement("replaceregexp");
				replaceregexp.addAttribute("byline","true");
				replaceregexp.addElement("regexp").addAttribute("pattern","^"+key+"=.*$");
				replaceregexp.addElement("substitution").addAttribute("expression", key+"=${"+key+"}");
				Element fileset = replaceregexp.addElement("fileset").addAttribute("dir", "${classesDir}");
				fileset.addElement("include").addAttribute("name", "application.properties");
				fileset.addElement("include").addAttribute("name", "log4j.properties");
			}
			this.saveDocument(doc, realModDestPath);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/***
	public static void main(String[] args) {
		BuildPreProperties task = new BuildPreProperties();
		task.setChangeDestXmlFilePath("modProperties.xml");
		task.setModRefFilePath("185ant.properties");
		task.execute();
	}
	***/
}
