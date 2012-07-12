<%@ page language="java" import="java.util.*" contentType="application/json; charset=utf-8"%><%response.setHeader("pragma", "no-cache");
response.setHeader("cache-control", "no-cache");
response.setDateHeader("expires", 0);

Thread.sleep(500);

out.print("[");
out.print("    {\"id\": \"01\", \"name\": \"电影\", \"customAttr\": \"a\", \"nodes\": [");
out.print("        {\"id\": \"001\", \"name\": \"盗梦空间\", \"customAttr\": \"aa\", \"nodes\": []},");
out.print("        {\"id\": \"002\", \"name\": \"怪物史瑞克4\", \"customAttr\": \"bb\", \"nodes\": []},");
out.print("        {\"id\": \"003\", \"name\": \"哈利波特7\", \"customAttr\": \"cc\", \"nodes\": []},");
out.print("        {\"id\": \"004\", \"name\": \"生化危机4\", \"customAttr\": \"dd\", \"nodes\": []}");
out.print("    ]},");
out.print("    {\"id\": \"02\", \"name\": \"手机游戏\", \"customAttr\": \"b\", \"nodes\": [");
out.print("        {\"id\": \"005\", \"name\": \"iPhone\", \"customAttr\": \"ee\", \"nodes\": [");
out.print("            {\"id\": \"0001\", \"name\": \"愤怒的小鸟\", \"customAttr\": \"aaa\", \"nodes\": []},");
out.print("            {\"id\": \"0002\", \"name\": \"皇牌空战11\", \"customAttr\": \"bbb\", \"nodes\": []},");
out.print("            {\"id\": \"0003\", \"name\": \"极品飞车热力追踪\", \"customAttr\": \"ccc\", \"nodes\": []}");
out.print("        ]},");
out.print("        {\"id\": \"006\", \"name\": \"Android\", \"customAttr\": \"ff\", \"nodes\": [");
out.print("            {\"id\": \"0004\", \"name\": \"涂鸦勇士\", \"customAttr\": \"ddd\", \"nodes\": []},");
out.print("            {\"id\": \"0005\", \"name\": \"口袋上帝\", \"customAttr\": \"eee\", \"nodes\": []}");
out.print("        ]}");
out.print("    ]},");
out.print("    {\"id\": \"03\", \"name\": \"动漫\", \"customAttr\": \"c\", \"nodes\": [");
out.print("        {\"id\": \"007\", \"name\": \"海贼王\", \"customAttr\": \"gg\", \"nodes\": []},");
out.print("        {\"id\": \"008\", \"name\": \"火影忍者\", \"customAttr\": \"hh\", \"nodes\": []},");
out.print("        {\"id\": \"009\", \"name\": \"死神\", \"customAttr\": \"ii\", \"nodes\": []}");
out.print("    ]}");
out.print("]");
%>