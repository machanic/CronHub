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
<script type="text/javascript" src="/res/js/utils/json2.js"></script>
<script type="text/javascript" src="/res/js/pager/pager.js"></script>
<script type="text/javascript" src="/res/js/utils/string_utils.js"></script>
<link rel="stylesheet" type="text/css" href="/res/skin/all.css" />

<script>
var dialog = null;

$(function() {
	// grid contextmenu，注释掉的是禁用部分菜单项，更改菜单项图标和文字的例子
	$('.grid tbody tr').contextMenu([
			{'查看该task任务的简介':{id:$(this).attr("id"),icon:'/res/icons/16x16/comment.png',onclick:function(){
				var _this = this;
				var comment = $("#comment_"+_this.id).html();
				comment = comment.replaceAll(" ","&nbsp;",false).replaceAll("\n","<br/>",false);
				Dialog.alert("<font color='#1874CD'>task:"+_this.id+"任务的简介:</font>"+comment);
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
	initPagerBtnAndChangeFilter("/task/task_first_page_view.action","/task/task_prev_page_view.action","/task/task_next_page_view.action","/task/task_last_page_view.action");
	deleteOne();
	$("#btnImmediateExec").click(btnImmediateExecClick);
	hideLongText();
	$("#btnCmdUnfold").bind("click",{"name":"unfoldBtn"},cmdfoldreverse);
	$("#btnCmdfold").bind("click",	{"name":"foldBtn"},cmdfoldreverse);
	highlight_ok('crontab调度任务持续进行中...');
	setInterval("highlight_ok('crontab调度任务持续进行中...')",11000);
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
		Dialog.confirm("将关联删除该task所发出的所有相关调度记录,是否继续?",function(){
			$.ajax({
				url: "/task/deleteOne.action",
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

function btnImmediateExecClick(){
	var checkCount=0;
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')) {
			checkCount++;
		}
	});
	if(checkCount<=0){
		Dialog.alert("请选中至少一条记录进行操作");
		return;
	}
	Dialog.confirm("确认要当场执行吗?执行结果会记入到调度结果记录中",function(){
	var loadingImg = "/res/images/gif/loading.gif";
	var successImg = "/res/icons/16x16/accept.png";
	var failImg ="/res/icons/16x16/exclamation.png"
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')){
			var id = $(o).val();
			$("tr[id='"+id+"'] img[title='"+id+"'][name='status']").attr("src",loadingImg);
			$.ajax({
				url: "/task/remoteExec.action",
				data: {"id":id},
				cache: false,
				async: true,
				type: 'POST',
				dataType: 'json',
				timeout: 8640000,
				error: function() {
					Dialog.alert('对不起，服务器响应超时，请联系管理员');
				},
				success: function(result) {
					if(result.hasOwnProperty("error")){
						Dialog.alert(result["error"]);
					}
					var status = result["status"];
					var ret_id = result["id"];
					$("tr[id='"+ret_id+"'] span img[title='"+ret_id+"'][name='status']").attr("src",status=="success"?successImg:failImg);
				}
				});
			}
		});		
	});
}

function hideLongText(){
	$(".grid tbody td.cmdClass").each(function(){
		var innerStr = $(this).text();
		var innerStr = $.trim(innerStr);	
		if(innerStr.length>11){
			var ellipsis = $("<span class='toolbar' title='"+innerStr+"' name='unfoldBtn'><a>...</a></span>").click(function(){
				var wholeStr = $(this).attr('title');
				var currentFold = $(this).parent().contents().clone(true);
				var parent = $(this).parent();
				var foldBtn = $("<span class='toolbar' name='foldBtn'><a>&lt;&lt;</a>").click(function(){
					parent.html("").append(currentFold);
				});
				$(this).parent().html(wholeStr).append(foldBtn);
			});
			var shortStr = innerStr.substr(0,11);
			$(this).html(shortStr).append(ellipsis);
		}
	});
}
function cmdfoldreverse(event){
	var checkCount=0;
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')) {
			checkCount++;
		}
	});
	if(checkCount<=0){
		Dialog.alert("请选中至少一条记录进行操作");
		return;
	}
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')){
			var id = $(o).val();
			$("tr[id='"+id+"']").find(".cmdClass .toolbar[name='"+event.data.name+"']").click();
		}
	});
}
function validationCmd(cmd){
	cmd = cmd.replace(/[ ]/g,"");
	if(cmd=="" || cmd.indexOf("请输入命令")>=0)return false;
	return true;
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
		<a class="tab-selected"><img src="/res/icons/16x16/application_side_list.png" />task任务列表</a>
	</div>
	<div class="toolbar">
		<a id="btnImmediateExec"><img src="/res/icons/16x16/application_osx_terminal.png" />当场执行</a>
		<a id="btnCmdUnfold"><img src="/res/icons/16x16/table_sort.png" />展开</a>
		<a id="btnCmdfold"><img src="/res/icons/16x16/table_sort.png" />折叠</a>
		<input id="txtMachineIp" type="text" valid="validationIp" stat="like" trigger="click" trigger_target="btnSearchByIp" invalid_msg="请填入正确ip的一部分" name="filter_state_machine_ip" value="请输入机器ip" class="input-shorttext" onclick="if('请输入机器ip' == $(this).val()){$(this).val('');}"/><a id="btnSearchByIp"><img src="/res/icons/16x16/application_form_magnify.png" />按照ip搜索</a>
		<input id="txtShellCmd" valid="validationCmd" invalid_msg="请输入命令.." type="text" stat="like" trigger="click" trigger_target="btnShellCmd"  name="filter_state_shell_cmd" value="请输入命令..." class="input-shorttext" onclick="if('请输入命令...' == $(this).val()){$(this).val('');}"/><a id="btnShellCmd"><img src="/res/icons/16x16/application_form_magnify.png" />按照命令过滤</a>
		<!-- <a onclick="doBatch('data/common/doBatch.jsp', '确定要删除task任务吗？');"><img src="/res/icons/16x16/cancel.png" />删除</a> -->		
	</div>
	<table class="grid">
		<thead>
			<tr>
				<th width="30"><span><input type="checkbox" value="0"/></span></th>
				<th width="15"><span>task id</span></th>
				<th width="15"><span>daemon id</span></th>
				<th><span>daemon名字</span></th>
				<th><span>机器ip</span></th>
				<th><span>端口号</span></th>
				<th><span>状态</span></th>
				<th><span>cron_exp</span></th>
				<th><span>任务命令</span></th>
				<th><span>简介</span></th>
				<th width="15"><span>替换参数</span></th>
				<th><span>重执行</span></th>
				<th><span>截止次数</span></th>
				<th><span><select name="filter_state_run_mode" trigger="change" stat="equal" trigger_target="this"><option value="-1">模式</option><option  value="1">主动</option><option value="0">被动</option></select><img src="/res/icons/16x16/magnifier.png"/></span></th>
				<th><span><select name="filter_state_is_process_node" trigger="change" stat="equal" trigger_target="this"><option value="-1">任务类型</option><option  value="0">单任务</option><option value="1">流程任务</option></select><img src="/res/icons/16x16/magnifier.png"/></span></th>
				<th><span>通信</span></th>
				<th><span>更新时间</span></th>
				<th width="15"><span>删除</span></th>				
			</tr>
		</thead>
		<tbody>
			<s:iterator value="#request.beanlist" id="taskbean" status="statu">
			<tr id="<s:property value='#taskbean.id' />">
				<td align="center"><span><input type="checkbox" value="<s:property value='#taskbean.id' />" /></span></td>
				<td align="center"><span><s:property value="#taskbean.id" /></span></td>
				<td align="center"><span><s:property value="#taskbean.daemon_id" /><img title="指派新任务" onClick="parent.daemon_id=<s:property value="#taskbean.daemon_id" />;parent.machine_ip='<s:property value="#taskbean.daemon.machine_ip" />';parent.machine_port=<s:property value="#taskbean.daemon.machine_port" />;window.location='/page/cronhub_task/CronhubTaskAdd.jsp';" src="/res/icons/16x16/table_add.png" style="cursor:pointer"/></span></td>
				<td align="center" class="cmdClass"><s:property value="#taskbean.daemon.daemon_version_name" /></td>
				<td align="center"><span><s:property value="#taskbean.daemon.machine_ip" /></span></td>
				<td align="center"><span><s:property value="#taskbean.daemon.machine_port" /></span></td>
				<td align="center"><span><img title="<s:property value='#taskbean.id' />" name="status" src="/res/icons/16x16/picture_empty.png"/></span></td>
				<td align="center"><span><s:property value="#taskbean.cron_exp" /></span></td>
				<td align="center" class="cmdClass"><s:property value="#taskbean.shell_cmd" /></td>
				<td align="center" class="cmdClass"><s:property value='#taskbean.comment' /></td>
				<td align="center"><span>${taskbean.must_replace_cmd == true ? '<font style="color:green">是</font>':'<font style="color:red">否</font>'}</span></td>
				<td align="center"><span>${taskbean.is_redo == true ? '<font style="color:green">是</font>':'<font style="color:red">否</font>'}</span></td>
				<td align="center"><span><s:property value="#taskbean.end_redo_times" /></span></td>
				<td align="center"><span>${taskbean.run_mode == true ? '<font style="color:green">主动</font>':'<font style="color:#8B4513">被动</font>'}</span></td>
				<td align="center"><span>${taskbean.is_process_node == false ? '单任务':'流程任务'}</span></td>
				<td align="center"><span><s:if test="#taskbean.daemon.conn_status==true"><font style="color:green">通信正常</font></s:if><s:else><font style="color:red">通信失败</font></s:else></span></td>
				<td align="center"><span><s:date name="#taskbean.update_time" format="yyyy-MM-dd HH:mm:ss"/></span></td>
				<td align="center"><span><img title="<s:property value='#taskbean.id' />" name="del" style="cursor:pointer" src="/res/icons/16x16/cancel.png"/></span></td>
				
			</tr>
			<div id="comment_<s:property value='#taskbean.id' />" style="display:none"><s:property value='#taskbean.comment' /></div>
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
