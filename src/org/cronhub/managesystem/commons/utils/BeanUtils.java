package org.cronhub.managesystem.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeanUtils {

	
	/**
	 * 根据类名实例化一个对象
	 * @param className
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Object newInsatnce(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//		return Class.forName(className).newInstance();
		
		// copy form RequestUtils
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null){
			classLoader = BeanUtils.class.getClassLoader();
		}
		
		return classLoader.loadClass(className).newInstance();
	}
	
	
	/**
	 * 返回一个类的所有字段
	 * @param _class
	 * @return Field[]
	 */
	public static Field[] getFields(Class _class){
		return _class.getDeclaredFields();
	}
	
	/**
	 * 返回一个类的所有字段
	 * @param _class
	 * @return List
	 */
	public static List getFieldList(Class _class){
		Field[] fs =  _class.getDeclaredFields();
		return Arrays.asList(fs);
	}
	
	/**
	 * 返回一个类的所有public方法
	 * @param _class
	 * @return Method[]
	 */
	public static Method[] getMethods(Class _class){
		return _class.getMethods();
	}
	
	/**
	 * 返回一个类的所有public方法
	 * @param _class
	 * @return List
	 */
	public static List getMethodList(Class _class){
		Method[] ms = _class.getMethods();
		return Arrays.asList(ms);
	}
	
	/**
	 * 返回一个类的所有的public的setter
	 * @param _class
	 * @return Map(fieldName, Method)
	 */
	public static Map getSetMethods(Class _class){
		Map setMethodMap = new HashMap();
		
		Method[] methods = getMethods(_class);
		if(methods == null){
			return setMethodMap;
		}
		
		String regEx = "set(\\w+)";
		Pattern p = Pattern.compile(regEx);
		for(Method m : methods){
			String name = m.getName();
			Matcher match = p.matcher(name); 
			if(match.find()){
				String methodName = match.group(0);
				String tmp = match.group(1);
				String field_top = tmp.substring(0,1).toLowerCase();
				String field = field_top + tmp.substring(1);
				setMethodMap.put(field, m);
			}
		}
		
		return setMethodMap;
	}

}