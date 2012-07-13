<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "  http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>list</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<script type="text/javascript" src="/res/js/jquery/jquery-1.6.2.min.js"></script>

<script type="text/javascript" src="/res/js/list/list.js"></script>

<script type="text/javascript" src="/res/js/highlight/highlight.js"></script>

<script type="text/javascript" src="/res/js/contextmenu/jquery.contextmenu.js"></script>

<script type="text/javascript" src="/res/js/zdialog/zDrag.js"></script>
<script type="text/javascript" src="/res/js/zdialog/zDialog.js"></script>
<script type="text/javascript" src="/res/js/pager/pager.js"></script>
<script type="text/javascript" src="/res/js/utils/json2.js"></script>
<script type="text/javascript" src="/res/js/utils/string_utils.js"></script>
<link rel="stylesheet" type="text/css" href="/res/skin/all.css" />

<script>
var dialog = null;

$(function() {
	// grid contextmenu，注释掉的是禁用部分菜单项，更改菜单项图标和文字的例子
	$('.grid tbody tr').contextMenu([
			{'查看该daemon执行器的简介':{id:$(this).attr("id"),icon:'/res/icons/16x16/comment.png',onclick:function(){
				var _this = this;
				var comment = $("#comment_"+_this.id).html();
				comment = comment.replaceAll(" ","&nbsp;",false).replaceAll("\n","<br/>",false);
				Dialog.alert("<font color='#1874CD'>daemon_id:"+_this.id+"执行器的简介:</font>"+comment);
			}}}
		]/*,
		{
			beforeShow: function(o) {
				// 禁用某菜单项
				if($(o).attr('value') % 2 == 0) {
					$('#item1').addClass('context-menu-item-disabled');
				}
				// 改变菜单项名称和图标
				if($(o).attr('value') % 2 == 0) {
					$('#item2').find('.context-menu-item-inner').text('启用');
					$('#item2').find('div').css('background-image', 'url(/res/icons/16x16/drive_network.png)');
				}
				else {
					$('#item2').find('.context-menu-item-inner').text('禁用');;
					$('#item2').find('div').css('background-image', 'url(/res/icons/16x16/calculator_delete.png)');
				}
			}
		}*/
	);
	
	// 操作完成提示
	//highlight('还有失败的任务!');
	
	// 查询对话框
	dialog = new Dialog({
		Animator: false,
		Width: 420,
		Height: 200,
		Title: '查询对话框',
		InvokeElementId: 'dialog-form',
		OKEvent: function() {
			dialog.close();
			$('#form1').submit();
		}
	});
	/**
	if($("#hiddenLastIp").html()!=""){
		var json = eval($("#hiddenLastIp").html());
		var ip = json[0][1]["machine_ip"];
		$("#txtMachineIp").val(ip);
		filter = $("#hiddenLastIp").html();
	}
	一定要在initBtnPager之前执行，因为initBtnPager要用filter
	**/
	//initBtnPager("/daemon/daemon_first_page_view.action","/daemon/daemon_prev_page_view.action","/daemon/daemon_next_page_view.action","/daemon/daemon_last_page_view.action");
	initPagerBtnAndChangeFilter("/daemon/daemon_first_page_view.action","/daemon/daemon_prev_page_view.action","/daemon/daemon_next_page_view.action","/daemon/daemon_last_page_view.action");
	//initBtnSearchByIp("/daemon/daemon_first_page_view.action");
	deleteOne();
	highlight_ok('通信检测监控中...');
	setInterval("highlight_ok('通信检测监控中...')",11000);
});
function validationIp(current_ip){
	var isIp = /^[\d|\.]+$/.test(current_ip);
	if(!isIp){
		return false;
	}
	return true;
}
function deleteOne(){
	$(".grid tr img[name='del']").click(function(){
		var del_id = $(this).attr("title");
		Dialog.confirm("将关联删除该daemon下管理的所有任务task以及相关调度记录,是否继续?",function(){
			$.ajax({
				url: "/daemon/deleteOne.action",
				data: {"id":del_id},
				cache: false,
				async: true,
				type: 'POST',
				dataType: 'text',
				timeout: 10000,
				error: function() {
					Dialog.alert('对不起，服务器响应超时，请联系管理员');
				},
				success: function(data) {
					if(data == "success"){
					Dialog.alert("删除成功");
					$(".grid tr#"+del_id).hide();
					}else{
						Dialog.alert("删除id:"+del_id+"时出现错误.");
					}
				}
			});	
		});
	});
}
</script>
<style type="text/css">
img[src="/res/icons/16x16/magnifier.png"]{
	cursor:pointer;
}
</style>
</head>

<body>
	<div class="tab">
		<a class="tab-selected"><img src="/res/icons/16x16/application_side_list.png" />daemon执行者程序管理</a>
	</div>
	
	<div class="toolbar">
		<!-- <img src="/res/icons/16x16/script_code.png"/><input type="text" value="请输入升级后的重启daemon的shell脚本" class="input-text" onclick="if('请输入升级后的重启daemon的shell脚本' == $(this).val()){$(this).val('');}"/>
		<img src="/res/icons/16x16/folder_edit.png"/><input type="text" value="/opt/modules/tomcat/webapp/daemon_program" class="input-text" />
		 -->
		<a onclick="Dialog.alert('此功能呢暂不开放');"><img src="/res/icons/16x16/application_osx_terminal.png" />升级daemon包</a>
		
		<input id="txtMachineIp" type="text" valid="validationIp" stat="like" trigger="click" trigger_target="btnSearchByIp" invalid_msg="请填入正确ip的一部分" name="filter_state_machine_ip" value="请输入机器ip" class="input-shorttext" onclick="if('请输入机器ip' == $(this).val()){$(this).val('');}"/><a id="btnSearchByIp"><img src="/res/icons/16x16/application_form_magnify.png" />按照ip搜索</a>
		<!-- <a onclick="window.location='/page/cronhub_daemon/CronhubDaemonView.jsp';"><img src="/res/icons/16x16/arrow_refresh.png" />刷新</a> -->
	</div>
	
	<table class="grid">
		<thead>
			<tr>
				<th width="5%"><span><input type="checkbox" value="0"/></span></th>
				<th width="5%"><span>id</span></th>
				 <th width="20%"><span>daemon名字</span></th> 
				<th width="10%"><span>添加新任务</span></th>
				<th width="10%"><span>机器ip</span></th>
				<th width="10%"><span>机器port</span></th>
				<th width="10%"><span><select name="filter_state_conn_status" trigger="change" stat="equal" trigger_target="this"><option value="-1">通信状态</option><option  value="1">通信成功</option><option value="0">通信失败</option></select><img src="/res/icons/16x16/magnifier.png"/></span></th>
				<!--<th width="10%"><span>刷入crontab</span></th> -->
				<th width="10%"><span><select name="filter_state_must_lostconn_email" trigger="change" stat="equal" trigger_target="this"><option value="-1">是否邮件报警</option><option  value="1">是</option><option value="0">否</option></select><img src="/res/icons/16x16/magnifier.png"/></span></th>
				<th width="5%"><span>修改日期</span></th>
				<th width="5%"><span>删除</span></th>				
			</tr>
		</thead>
		<tbody>
			<s:iterator value="#request.beanlist" id="daemonbean" status="statu">
				<tr id="<s:property value='#daemonbean.id' />">
					<td align="center"><span><input type="checkbox"  value="<s:property value='#daemonbean.id' />" /></span></td>
					<td align="center"><span><s:property value="#daemonbean.id" /></span></td>
					<td align="center"><span><s:property value="#daemonbean.daemon_version_name" /></span></td>
					<td align="center"><span><img onClick="parent.daemon_id=<s:property value='#daemonbean.id'/>;parent.machine_ip='<s:property value="#daemonbean.machine_ip" />';parent.machine_port=<s:property value="#daemonbean.machine_port" />;window.location='/page/cronhub_task/CronhubTaskAdd.jsp';" src="/res/icons/16x16/table_add.png" title="添加新任务" style="cursor:pointer"/></span></td>
					<td align="center"><span><s:property value="#daemonbean.machine_ip" /></span></td>
					<td align="center"><span><s:property value="#daemonbean.machine_port" /></span></td>
					<!--<td align="center"><span><s:if test="#daemonbean.tasks==null">0</s:if><s:else><s:property value="#daemonbean.tasks.size()" /></s:else>&nbsp;<img onClick="window.location='/page/cronhub_task/CronhubTaskView.jsp';" src="/res/icons/16x16/table_go.png" style="cursor:pointer"/></span></td>-->
					<td align="center"><span>${daemonbean.conn_status == true ? '<font style="color:green">通信正常</font>':'<font style="color:red">通信失败</font>'}</span></td>
					<!--<td align="center"><span><a><img src="/res/icons/16x16/server_lightning.png" onClick='Dialog.alert("此功能暂不开放")'/></span></a></td> -->
					<td align="center"><span>${daemonbean.must_lostconn_email == true ? '<font style="color:green">是</font>':'<font style="color:red">否</font>'}</span></td>
					<td align="center"><span><s:date name="#daemonbean.update_time" format="yyyy-MM-dd HH:mm:ss"/></span></td>
					<td align="center"><span><img title="<s:property value='#daemonbean.id' />" name="del" style="cursor:pointer" src="/res/icons/16x16/cancel.png"/></span></td>
				</tr>
				<div id="comment_<s:property value='#daemonbean.id' />" style="display:none"><s:property value='#daemonbean.comment' /></div>
			</s:iterator>
		</tbody>
	</table>
	<div id="divPager" class="toolbar">
		<a id="btnFirstPage" ><img src="/res/icons/16x16/arrow_redo.png" />首页</a>
		<a id="btnPrevPage"><img src="/res/icons/16x16/arrow_left.png" />上一页</a>
		<a id="btnNextPage"><img src="/res/icons/16x16/arrow_right.png" />下一页</a>
		<a id="btnLastPage"><img src="/res/icons/16x16/arrow_undo.png" />末页</a>
		<div id="hiddenPageNo" style='display:none'><s:property value='#request["current_page_no"]'/></div>
		<div id="hiddenMaxPerPage" style='display:none'><s:property value='#request["max_per_page"]'/></div>
		<div id="hidden_filter" style="display:none"><s:property value="#request.filter"/></div>
		共<font style="color:#8B0A50"><s:property value='#request["total_count"]'/>条</font>记录 共计<font style="color:#00688B"><span id="spanTotalPageNo"><s:property value='#request["total_page_count"]'/></span>页</font> 当前<font style="color:#00688B">第<span id="spanCurrentPageNo">0</span>页</font>
		</select>
		每页显示
		<select id="ddlMaxPerPage">
			<option value="20">20条</option>
			<option value="50">50条</option>
			<option value="100">100条</option>
		</select>
	</div>
	<!-- 
	<div id="dialog-form" style="display: none;">
		<form id="form1" name="form1" action="list.html" target="main" method="post">
			<table class="formtable">
			<tr>
				<td width="80">
					<label>用户账号：</label>
				</td>
				<td>
					<input type="text" id="loginname" name="loginname" class="input-text" />
				</td>
			</tr>
			<tr>
				<td>
					<label>用户状态：</label>
				</td>
				<td>
					<input type="radio" id="state_0" name="state" value="0" /><img src="/res/icons/16x16/user_comment.png" />在线&nbsp;&nbsp;&nbsp;
					<input type="radio" id="state_1" name="state" value="1" /><img src="/res/icons/16x16/user_gray.png" />离线&nbsp;&nbsp;&nbsp;
					<input type="radio" id="state_2" name="state" value="2" /><img src="/res/icons/16x16/user_delete.png" />停用
				</td>
			</tr>
			<tr>
				<td>
					<label>用户单位：</label>
				</td>
				<td>
					<input type="text" id="org" name="org" class="input-text" />
				</td>
			</tr>
			<tr>
				<td>
					<label>用户角色：</label>
				</td>
				<td>
					<select id="role" name="role" class="input-select">
						<option value="">请选择</option>
						<option value="1">超级管理员</option>
						<option value="2">系统管理员</option>
						<option value="3">管理员</option>
						<option value="4">普通用户</option>
				    </select>
				</td>
			</tr>
			</table>
		</form>
	</div>
	 -->
</body>
</html>
