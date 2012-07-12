<%@ page language="java" import="java.util.*" contentType="text/xml; charset=utf-8"%><%response.setHeader("pragma", "no-cache");
response.setHeader("cache-control", "no-cache");
response.setDateHeader("expires", 0);

String id = request.getParameter("id");
Thread.sleep(500);

out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
out.print("<root>");

if(id == null) {
	out.print("    <node id=\"01\" name=\"美食\" customId=\"meishi\" checkbox=\"false\">");
	out.print("        <node id=\"001\" name=\"火锅\" customId=\"huoguo\" checkInteractSub=\"false\">");
	out.print("            <node id=\"0001\" name=\"孔亮\" customId=\"kongliang\" uncheckInteractParent=\"false\" horiz=\"true\"></node>");
	out.print("            <node id=\"0002\" name=\"九记\" customId=\"jiuji\" uncheckInteractParent=\"false\" checked=\"false\" horiz=\"true\"></node>");
	out.print("            <node id=\"0003\" name=\"秦妈\" customId=\"qinma\" uncheckInteractParent=\"false\" checked=\"false\" horiz=\"true\"></node>");
	out.print("        </node>");
	out.print("        <node id=\"002\" name=\"焖锅\" customId=\"menguo\">");
	out.print("            <node id=\"0004\" name=\"兄弟三汁焖锅\" customId=\"xiongdi\" checked=\"true\" horiz=\"true\"></node>");
	out.print("            <node id=\"0005\" name=\"黄记煌三汁焖锅\" customId=\"huangjihuang\" horiz=\"true\"></node>");
	out.print("        </node>");
	out.print("    </node>");
	out.print("    <node id=\"02\" name=\"汽车\" customId=\"qiche\" checkbox=\"false\" isParent=\"true\"></node>");
	out.print("    <node id=\"03\" name=\"测试一下\" customId=\"ceshiyixia\" checkbox=\"false\" isParent=\"true\"></node>");
}
else if(id.equals("02")) {
	
	out.print("        <node id=\"003\" name=\"奔驰\">");
	out.print("            <node id=\"0006\" name=\"S600\"></node>");
	out.print("            <node id=\"0007\" name=\"S400\"></node>");
	out.print("            <node id=\"0008\" name=\"E320\"></node>");
	out.print("            <node id=\"0009\" name=\"G550\"></node>");
	out.print("        </node>");
	out.print("        <node id=\"004\" name=\"路虎\" isParent=\"true\"></node>");
	out.print("        <node id=\"005\" name=\"保时捷\">");
	out.print("            <node id=\"0013\" name=\"卡宴\" isParent=\"true\"></node>");
	out.print("        </node>");
}
else if(id.equals("004")) {
	out.print("            <node id=\"0010\" name=\"卫士\"></node>");
	out.print("            <node id=\"0011\" name=\"发现\"></node>");
	out.print("            <node id=\"0012\" name=\"览胜\"></node>");
}
out.print("</root>");
%>