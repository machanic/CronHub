<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"%><%response.setHeader("pragma", "no-cache");
response.setHeader("cache-control", "no-cache");
response.setDateHeader("expires", 0);

String id = request.getParameter("id");
//System.out.println(id);

out.println("<style>body {font-size: 12px; font-family: '微软雅黑';}</style>");

out.println("<p>处理的id是：</p>");

out.println("<p><span>"+id+"</span></p>");

out.println("<p><a href='../../list.html'>返回列表页</a></p>");
%>