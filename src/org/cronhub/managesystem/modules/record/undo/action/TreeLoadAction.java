package org.cronhub.managesystem.modules.record.undo.action;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.cronhub.managesystem.commons.utils.PageIOUtils;
import org.cronhub.managesystem.commons.utils.XmlUtils;
import org.cronhub.managesystem.modules.record.undo.dao.IUndoRecordDao;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.opensymphony.xwork2.ActionSupport;

public class TreeLoadAction extends ActionSupport {
	private static final String icon = "/res/icons/16x16/hourglass.png";
	
	private IUndoRecordDao recordDao;
	
	public void setRecordDao(IUndoRecordDao recordDao) {
		this.recordDao = recordDao;
	}
	private static Comparator<Element> dateDescComparator = new Comparator<Element>(){
		@Override
		public int compare(Element o1, Element o2) {
			if(StringUtils.isNotEmpty(o1.attributeValue("id")) && StringUtils.isNotEmpty(o2.attributeValue("id"))){
				return o2.attributeValue("id").compareTo(o1.attributeValue("id"));
			}
			return o2.attributeValue("name").compareTo(o1.attributeValue("name"));
		}
	};

	private Element chooseBiggestElement(Element parent){
		Element biggest = null;
		for(Object obj : parent.elements()){
			Element currentElement = (Element)obj;
			if(null==biggest){
				biggest = currentElement;
			}
			if(currentElement.attributeValue("name").compareTo(biggest.attributeValue("name")) > 0){
				biggest = currentElement;
			}
		}
		return biggest;
	}
	public String createInitTree(){
		HttpServletResponse response= ServletActionContext.getResponse();
		response.setHeader("pragma", "no-cache");
		response.setHeader("cache-control", "no-cache");
		response.setDateHeader("expires", 0);
		
		Map<String,Map<String,Set<JSONObject>>> dateInfos = recordDao.getAllDateInfoFromTable();
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");
		for(Map.Entry<String, Map<String,Set<JSONObject>>> entry:dateInfos.entrySet()){
			Element yearElement = root.addElement("node").addAttribute("type", "year").addAttribute("name",entry.getKey());
			for(Map.Entry<String,Set<JSONObject>> monthEntry : entry.getValue().entrySet()){
				Element monthElement = yearElement.addElement("node").addAttribute("type", "month").addAttribute("name",monthEntry.getKey());
				for(JSONObject json : monthEntry.getValue()){
					monthElement.addElement("node").addAttribute("type", "day").addAttribute("name",json.getString("day")).addAttribute("id", json.getString("date")).addAttribute("icon", icon);
				}
			}
		}
		if(root.elements().size()>0){
			Element bigYear = this.chooseBiggestElement(root);
			
			bigYear.addAttribute("open", "true");
			Element bigMonth = this.chooseBiggestElement(bigYear);
			for(Object yearObj : root.elements()){
				Element yearEl =(Element)yearObj;
				if(yearEl != bigYear){
					yearEl.addAttribute("open","false");
					for(Object monthObj :yearEl.elements()){
						Element monthEl = (Element)monthObj;
						monthEl.addAttribute("open","false");
					}
				}
			}
			bigMonth.addAttribute("open","true");
			XmlUtils.sort(root,dateDescComparator,true);
		}
		PageIOUtils.printToPage(doc);
		return NONE;
	}
}
