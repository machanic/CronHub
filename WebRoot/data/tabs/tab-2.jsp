<%@ page language="java" pageEncoding="UTF-8" %>
<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

Thread.sleep(1000);

out.println("<div class=\"list-item\"><div class=\"list-item-l\"><img src=\"res/icons/16x16/application_form_edit.png\" />快讯：胡锦涛举行仪式欢迎奥巴马首次访华</div><span class=\"list-item-r\">2010-09-08 08:30:00</span></div>");
out.println("<div class=\"list-item\"><div class=\"list-item-l\"><img src=\"res/icons/16x16/application_form_edit.png\" />中国平安首笔赔付山东威海坠车事故532万元</div><span class=\"list-item-r\">2010-09-08 08:30:00</span></div>");
out.println("<div class=\"list-item\"><div class=\"list-item-l\"><img src=\"res/icons/16x16/application_form_edit.png\" />上实发展4亿转让海际地产</div><span class=\"list-item-r\">2010-09-08 08:30:00</span></div>");
out.println("<div class=\"list-item\"><div class=\"list-item-l\"><img src=\"res/icons/16x16/application_form.png\" />ST星美新重组方案最迟5个交易日内揭晓</div><span class=\"list-item-r\">2010-09-08 08:30:00</span></div>");
out.println("<div class=\"list-item\"><div class=\"list-item-l\"><img src=\"res/icons/16x16/application_form.png\" />中国对美汽车\"双反\"调查进入登记应诉阶段</div><span class=\"list-item-r\">2010-09-08 08:30:00</span></div>");
out.println("<div class=\"list-item\"><div class=\"list-item-l\"><img src=\"res/icons/16x16/application_form.png\" />图们江区域开发新规划获国务院批准</div><span class=\"list-item-r\">2010-09-08 08:30:00</span></div>");
out.println("<div class=\"tab-more\"><a href=\"#\">更多»</a></div>");
%>