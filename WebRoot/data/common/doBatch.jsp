<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"%><%response.setHeader("pragma", "no-cache");
response.setHeader("cache-control", "no-cache");
response.setDateHeader("expires", 0);

String[] ids = request.getParameterValues("ids");
//System.out.println(ids);

out.println("<style>body {font-size: 12px; font-family: '微软雅黑';}</style>");

out.println("<p>批量处理的ids数组是：</p>");

out.println("<p>[");

for(int i = 0; i < ids.length; i++) {
	if(i > 0) {
		out.println(",");
	}
	out.println("<span>"+ids[i]+"</span>");
}

out.println("]</p>");

out.println("<p><a href='../../list.html'>返回列表页</a></p>");
%>