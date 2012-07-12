<%@ page language="java" import="java.util.*" contentType="application/json; charset=utf-8"%><%response.setHeader("pragma", "no-cache");
response.setHeader("cache-control", "no-cache");
response.setDateHeader("expires", 0);

String id = request.getParameter("id");
Thread.sleep(500);

if(id == null) {
	out.print("[");
	out.print("    {\"id\": \"01\", \"name\": \"电影\", \"isParent\": true},");
	out.print("    {\"id\": \"02\", \"name\": \"手机游戏\", \"isParent\": true},");
	out.print("    {\"id\": \"03\", \"name\": \"动漫\", \"isParent\": true}");
	out.print("]");
}
else if(id.equals("01")) {
	out.print("[");
	out.print("    {\"id\": \"001\", \"name\": \"盗梦空间\", \"isParent\": true},");
	out.print("    {\"id\": \"002\", \"name\": \"怪物史瑞克4\", \"isParent\": true},");
	out.print("    {\"id\": \"003\", \"name\": \"哈利波特7\", \"isParent\": true},");
	out.print("    {\"id\": \"004\", \"name\": \"生化危机4\", \"isParent\": true}");
	out.print("]");
}
else if(id.equals("02")) {
	out.print("[");
	out.print("    {\"id\": \"005\", \"name\": \"iPhone\", \"isParent\": true},");
	out.print("    {\"id\": \"006\", \"name\": \"Android\", \"isParent\": true}");
	out.print("]");
}
else if(id.equals("03")) {
	out.print("[");
	out.print("    {\"id\": \"007\", \"name\": \"海贼王\", \"isParent\": true},");
	out.print("    {\"id\": \"008\", \"name\": \"火影忍者\", \"isParent\": true},");
	out.print("    {\"id\": \"009\", \"name\": \"死神\", \"isParent\": true}");
	out.print("]");
}
else if(id.equals("005")) {
	out.print("[");
	out.print("    {\"id\": \"0001\", \"name\": \"愤怒的小鸟\", \"isParent\": true},");
	out.print("    {\"id\": \"0002\", \"name\": \"皇牌空战11\", \"isParent\": true},");
	out.print("    {\"id\": \"0003\", \"name\": \"极品飞车热力追踪\", \"isParent\": true}");
	out.print("]");
}
else if(id.equals("006")) {
	out.print("[");
	out.print("    {\"id\": \"0004\", \"name\": \"涂鸦勇士\", \"isParent\": true},");
	out.print("    {\"id\": \"0005\", \"name\": \"口袋上帝\", \"isParent\": true}");
	out.print("]");
}
else {
	out.print("[]");
}
%>