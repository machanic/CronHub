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
			{'查看该task任务的简介':{id:$(this).attr("id"),icon:'/res/icons/16x16/comment.png',onclick:function(){
				var _this = this;
				var comment = $("#task_comment_"+_this.id).html();
				comment = comment.replaceAll(" ","&nbsp;",false).replaceAll("\n","<br/>",false);
				Dialog.alert(comment);
			}}},
			{'查看该daemon执行器的简介':{id:$(this).attr("id"),icon:'/res/icons/16x16/comment.png',onclick:function(){
				var _this = this;
				var comment = $("#daemon_comment_"+_this.id).html();
				comment = comment.replaceAll(" ","&nbsp;",false).replaceAll("\n","<br/>",false);
				Dialog.alert(comment);
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
	
	$("#btnKill").click(function(){Dialog.alert("此功能暂未开发完成");});
	// 操作完成提示
	highlight('还有未完成的任务');
	
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
	
	initPagerBtnAndChangeFilter("/record_undo/first_page_view.action","/record_undo/prev_page_view.action","/record_undo/next_page_view.action","/record_undo/last_page_view.action");
	hideLongText();
	$("#btnCmdUnfold").bind("click",{"name":"unfoldBtn"},cmdfoldreverse);
	$("#btnCmdfold").bind("click",	{"name":"foldBtn"},cmdfoldreverse);
});
function hideLongText(){
	$(".grid tbody td.cmdClass").each(function(){
		var innerStr = $(this).text();
		if(innerStr.length>20){
			var ellipsis = $("<span class='toolbar' title='"+innerStr+"' name='unfoldBtn'><a>...</a></span>").click(function(){
				var wholeStr = $(this).attr('title');
				var currentFold = $(this).parent().contents().clone(true);
				var parent = $(this).parent();
				var foldBtn = $("<span class='toolbar' name='foldBtn'><a>&lt;&lt;</a>").click(function(){
					currentFold.find(".span[class='toolbar']").click();
					parent.html("").append(currentFold);
				});
				$(this).parent().html(wholeStr).append(foldBtn);
			});
			var shortStr = innerStr.substr(0,20);
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
function validationIp(current_ip){
	var isIp = /^[\d|\.]+$/.test(current_ip);
	if(!isIp){
		return false;
	}
	return true;
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
		<a class="tab-selected"><img src="/res/icons/16x16/application_side_list.png" />当天任务列表</a>
		<a style="display:none"><img src="/res/icons/16x16/application_side_list.png" />所有日期任务列表</a>
	</div>
	<div class="toolbar">
		<a id="btnCmdUnfold"><img src="/res/icons/16x16/table_sort.png" />命令展开</a>
		<a id="btnCmdfold"><img src="/res/icons/16x16/table_sort.png" />命令折叠</a>
		<a id="btnKill"><img src="/res/icons/16x16/kill_application_osx_terminal.png" />杀死</a>
		<a onclick="doBatch('data/common/doBatch.jsp', '确定要删除选中的用户吗？');"><img src="/res/icons/16x16/application_osx_terminal.png" />杀死并重新执行</a>
		<input id="txtMachineIp" type="text" valid="validationIp" stat="like" trigger="click" trigger_target="btnSearchByIp" invalid_msg="请填入正确ip的一部分" name="filter_state_machine_ip" value="请输入机器ip" class="input-shorttext" onclick="if('请输入机器ip' == $(this).val()){$(this).val('');}"/><a id="btnSearchByIp"><img src="/res/icons/16x16/application_form_magnify.png" />按照ip搜索</a>
		<input id="txtRealCmd" type="text" valid="validationCmd" invalid_msg="请输入命令.." stat="like" trigger="click" trigger_target="btnRealCmd"  name="filter_state_real_cmd" value="请输入命令..." class="input-shorttext" onclick="if('请输入命令...' == $(this).val()){$(this).val('');}"/><a id="btnRealCmd"><img src="/res/icons/16x16/application_form_magnify.png" />按照命令过滤</a>
		<!-- <a id="returnStr"><img src="/res/icons/16x16/script.png" />运行返回结果字符串</a> -->
	</div>
	<table class="grid">
		<thead>
			<tr>
				<th width="30"><span><input type="checkbox" /></span></th>
				<th width="30"><span>id</span></th>
				<th width="30"><span>任务id</span></th>
				<th width="30"><span>状态</span></th>
				<th><span>机器ip</span></th>
				<th><span>cron_exp</span></th>
				<th><span>真实命令</span></th>
				<th><span>运行时长</span></th>
				<th><span>执行开始时间</span></th>
				<th><span><select name="filter_state_run_mode" trigger="change" stat="equal" trigger_target="this"><option value="-1">模式</option><option  value="0">被动</option><option value="1">主动</option></select></span></th>
				<th><span><select name="filter_state_exec_type" trigger="change" stat="equal" trigger_target="this"><option value="-1">执行类型</option><option  value="0">crontab执行</option><option value="1">手动重执行</option><option value="2">自动重执行</option></select></span></th>
				<th><span><select name="filter_state_is_process_node" trigger="change" stat="equal" trigger_target="this"><option value="-1">任务类型</option><option  value="0">单任务</option><option value="1">流程节点</option></select></span></th>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="#request.beanlist" id="recordbean" status="stat">
			<tr id="<s:property value='#recordbean.id' />">
				<td align="center"><span><input type="checkbox" value="<s:property value='#recordbean.id' />" /></span></td>
				<td align="center"><span><s:property value="#recordbean.id" /></span></td>
				<td align="center"><span><s:property value="#recordbean.task_id" /></span></td>
				<td align="center"><span><img src="/res/icons/16x16/hourglass.png"/></span></td>
				<td align="center"><span><s:property value="#recordbean.task.daemon.machine_ip" /></span></td>
				<td align="center"><span><s:property value="#recordbean.task.cron_exp"/></span></td>
				<td align="center" class="cmdClass"><s:property value="#recordbean.real_cmd"/></td>
				<td align="center"><span><s:property value="#recordbean.duration"/></span></td>
				<td align="center"><span><s:date name="#recordbean.start_datetime" format="yyyy-MM-dd HH:mm:ss"/></span></td>
				<td align="center"><span><s:if test="#recordbean.task.run_mode==true"><font style="color:green">主动</font></s:if><s:else><font style="color:#D2691E">被动</font></s:else></span></td>
				<td align="center"><span><s:property escape="false" value="#recordbean.exec_type_ISO"/></span></td>
				<td align="center"><span><s:if test="#recordbean.task.is_process_node==true">流程节点</s:if><s:else>单任务</s:else></span></td>
			</tr>
			<div id="task_comment_<s:property value='#recordbean.id' />" style="display:none"><s:property value='#recordbean.task.comment' /></div>
			<div id="daemon_comment_<s:property value='#recordbean.id' />" style="display:none"><s:property value='#recordbean.task.daemon.comment' /></div>
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
		<div id="hidden_tableName" style="display:none"><s:property value="#request.tableName"/></div>
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
