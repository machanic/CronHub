package org.cronhub.managesystem.commons.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlUtils {
	/***
	 * java的xml的按照某一个"属性"递归sort
	 * @param element 传入的Element元素
	 * @param comparator 比较器，要对哪一个属性怎么比较?
	 * @param recursive 是否递归
	 */
	public static void sort(Element element,Comparator<Element> comparator,boolean recursive){
		if(element!=null && element.nodeCount()>0){
			List<Element> subs = (List<Element>)element.elements();//传入的这个Element下的sub子节点
			//先将本级的节点clone下来
			List<Element> cloneSubs = new ArrayList<Element>();
			for(Element s : subs){
				cloneSubs.add((Element)s.clone());
			}
			//对clone的节点进行sort
			Collections.sort(cloneSubs, comparator);
			//然后清除本级节点下的所有内容，将排好序的clone节点里的内容加入到这个element下
			element.clearContent();
			for(Element ss:cloneSubs){
				element.add(ss);
			}
			if(!recursive){return;}
			 //对他的孩子节点递归sort
			for(Element sub : (List<Element>)element.elements()){
				XmlUtils.sort(sub, comparator, recursive);//递归
			}
		}
	}
	public static void main(String[] args) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");
		root.addElement("node").addAttribute("date", "9").addElement("node").addAttribute("date","99").addElement("node").addAttribute("date","1");
		root.addElement("node").addAttribute("date", "8").addElement("node").addAttribute("date","88");
		root.addElement("node").addAttribute("date", "7").addElement("node").addAttribute("date","77");
		root.addElement("node").addAttribute("date", "6").addElement("node").addAttribute("date","66");
		Comparator<Element> com = new Comparator<Element>(){
			@Override
			public int compare(Element o1, Element o2) {
				return o1.attributeValue("date").compareTo(o2.attributeValue("date"));
			}
		};
		XmlUtils.sort(root, com, true);
		System.out.println(doc.asXML());
	}
}
