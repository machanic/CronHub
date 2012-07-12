<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"%><%response.setHeader("pragma", "no-cache");
response.setHeader("cache-control", "no-cache");
response.setDateHeader("expires", 0);

String loginname = request.getParameter("loginname");

//String org = request.getParameter("org");
//String email = request.getParameter("email");
//String idcard = request.getParameter("idcard");

if(loginname.equals("asdasd")) {
	out.print("0");
}
else {
	out.print("1");
}
%>