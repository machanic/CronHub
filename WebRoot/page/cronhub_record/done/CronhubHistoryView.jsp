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
<style type="text/css">
.area {
    position: absolute;
    color: #838B8B;
    font-weight: bold;
    width:200px;
    display: none;
    opacity:0.4; /*鼠标没有移动上去时，透明度为40% */
	filter:alpha(opacity=40); /* 针对 IE8 以及更早的版本 */

    /* you may want to set the width/height here as well*/
}

/***
.area:hover {  // 鼠标移上去时，变得不透明
	opacity:1.0;
	filter:alpha(opacity=100); // 针对 IE8 以及更早的版本
}
***/

.fixed {
    position: absolute;
    width:200px;
    top: 20px;
    left: 20px;
    border: 1px solid #9AC0CD;
    padding: 5px;
    z-index: 100px;
    background-color: #B0B0B0;
}
</style>
<script>
var dialog = null;

$(function() {
	$('.area').hover(function(){$(this).fadeTo(1000,1.0);}, function(){$(this).fadeTo(1000,0.3);});
	
	// grid contextmenu，注释掉的是禁用部分菜单项，更改菜单项图标和文字的例子
	$('.grid tbody tr').not('.hiddenReturnStr').contextMenu([
	        /***
	        {'复制(ctrl+c)':{id:$(this).attr("id"),icon:'/res/icons/16x16/comment.png',onclick:function(){
	        	
	        }}},
	        ***/
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
			}}},
			{'执行命令':{id:$(this).attr("id"),icon:'/res/icons/16x16/application_osx_terminal.png',onclick:function(){
				var _this = this;
				var message = $("#btnRedo").attr("message");
				Dialog.confirm(message,function(){
					redoRemoteExec(_this.id);
				});
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
	
	
	$("tr.hiddenReturnStr").contextMenu([
		{'折叠收起':{id:$(this).attr("title"),icon:'/res/icons/16x16/table_sort.png',onclick:function(){
			var _this = this;
			$(".imgUnfoldReturnStr[alt='"+_this.title+"']").click();
		}}},]);                                   
	
	//string的replaceAll方法
	
	//挂接点击运行结果字符串
	$("#btnReturnStr").click(clickReturnStr);
	$(".tab a").click(function(){$(".tab a").removeClass("tab-selected");$(this).addClass("tab-selected");});
	initPagerBtnAndChangeFilter("/record_done/first_page_view.action","/record_done/prev_page_view.action","/record_done/next_page_view.action","/record_done/last_page_view.action");
	hideLongText();
	$("#btnRedo").click(redoBtnClick);
	$("#btnCmdUnfold").bind("click",{"name":"unfoldBtn"},cmdfoldreverse);
	$("#btnCmdfold").bind("click",	{"name":"foldBtn"},cmdfoldreverse);
	// 操作完成提示
	highlight_ok('当前查询的表是: '+$("#hidden_tableName").html());
	setInterval("highlight_ok('自动重执行任务调度检测稳定持续化中...')",11000);
	//点击就隐藏那个返回结果字符串
	$(".imgHideReturnStr").click(function(){
		$(this).parent().parent().parent().hide(); 
		var id = $(this).attr("alt");
		$(".imgUnfoldReturnStr[alt='"+id+"']").click();
	});
	
	/**
	$("table tbody tr[id]").dblclick(function(){
		var id = $(this).find("input[type='checkbox']").val();
		showReturnStrItem(id);
	});
	**/
	imgReturnStrClick();
});
function hideLongText(){
	$(".grid tbody td.cmdClass").each(function(){
		var innerStr = $(this).text();
		if(innerStr.length>18){
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
			var shortStr = innerStr.substr(0,18);
			$(this).html(shortStr).append(ellipsis);
		}
	});
}
function redoBtnClick(){
	var checkCount=0;
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')) {
			checkCount++;
		}
	});
	if(checkCount<=0){
		Dialog.alert("请选中至少一条记录进行操作");
		return;
	}else{
		Dialog.confirm("确定重新执行?",function(){
		var message = $(this).attr("message");
		$('.grid tbody input[type=checkbox]').each(function(i, o) {
			if($(o).attr('checked')){
				var id = $(o).val();
				redoRemoteExec(id);	
			}
		});
		});
	}
}

function redoRemoteExec(id){
	var tableName = $("#hidden_tableName").html();
	var loadingImg = "<img src='/res/images/gif/loading.gif'/>";
	$("tr[id='"+id+"'] span[class][class!='toolbar']").html(loadingImg);
			$.ajax({
				url: "/record_done/remoteExec.action",
				data: {"id":id,"tableName":tableName},
				cache: false,
				async: true,//注意这个async,只有这样，才能批量打勾,点击重新执行后，批量异步地ajax到后台
				type: 'POST',
				dataType: 'json',
				timeout: 3600000,
				error: function() {
					Dialog.alert('对不起，服务器响应超时，请联系管理员');
				},
				success: function(result) {
					if(result.hasOwnProperty("error")){
						Dialog.alert(result["error"]);
					}
					for(var key in result){
						if(key!="id"){
							$("tr[id='"+result["id"]+"']").find("."+key).html(result[key]);
						}
					}
				}
			});	
}
//命令展开与命令折叠
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
function getCheckCount(){
	var checkCount=0;
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')) {
			checkCount++;
		}
	});
	if(checkCount<=0){
		Dialog.alert("请选中至少一条记录进行操作");
		return false;
	}
	return true;
}

//动态生成grep过滤输入框, return jq elem
//形参: returnStr:需要被过滤的字符串 , float_elem:点击加号图标(为了获取鼠标位置),  target_elem:需要替换成标红结果的text element
function generateGrep(returnStr, float_elem, target_elem){
	
	//获取坐标
	var offsetTop=$(float_elem).offset().top;
	var offsetLeft=$(float_elem).offset().left;
	offsetTop += 20;
	
	$(".area .fixed").css({"top" : offsetTop + "px", "left": offsetLeft + "px"});
	$("#grepDiv").show();
	$("#grepInput").val("").focus().keydown(function(e){
        var curKey = e.which;
        if(curKey == 13){
            $("#btnGrep").click();
            return false;
        }
	});
	
	$("#btnGrep").unbind("click").click(function(){
		if($("#grepInput").val().replace(/(^\s*)|(\s*$)/g, "") == ""){
			target_elem.html(returnStr);
		}else{
			 var new_str = "" ;
             var search = $("#grepInput").val();
             var reg=new RegExp("("+ search.split(" ").join(").*(" ) + ")" ,"g"); //如果用户敲入空格分割的string，则(use).*(time).*(abc)这种形式的正则表达式
             $.each(returnStr.split( "<br/>" ), function() {
                    if(this.match(reg)){ //每一行都是this --> line string
                    	var new_line = this;
                    	
                    	for(var kwd_index =0;kwd_index <  search.split(' ').length;kwd_index++){
                    		var kwd = search.split(' ')[kwd_index];
                    		var repl_kwd = new RegExp("("+kwd+")","g");
                    		new_line = new_line.replace(repl_kwd, "<font color='red'>$1</font>"); //只标红特定的单词
                    	}
                         new_str += new_line + "<br />" ;
                   }
         	});
            target_elem.html(new_str);

		}
	});
}

function imgReturnStrClick(){
	$(".imgUnfoldReturnStr").click(function(event){
		
		var id = $(this).attr("alt");
		var title  = $(this).attr("title");
		if ("close" == title){
			showReturnStrItem($(this),id);
			$(this).attr("title","open");
			$(this).attr("src","/res/icons/16x16/minus.png");
		}else if("open" == title){
			$(".hiddenReturnStr[name='"+id+"']").hide();
			
			$("#grepDiv").hide();
			$(this).attr("title","close");
			$(this).attr("src","/res/icons/16x16/plus.png");
		}
	});
}
function showReturnStrItem(float_elem, id){
	var tableName = $("#hidden_tableName").html();
	$(".hiddenReturnStr[name='"+id+"'] img[name='loading']").show();
	$(".hiddenReturnStr[name='"+id+"'] span[name='returnStrSpan']").hide();
	$(".hiddenReturnStr[name='"+id+"']").show();
	$.ajax({
		url: "/record_done/getExecReturnString.action",
		data: {"id":id,"tableName":tableName},
		cache: false,
		async: true,
		type: 'POST',
		dataType: 'json',
		timeout: 3600000,
		error: function() {
			Dialog.alert('对不起，访问id:'+id+'时,服务器响应超时，请联系管理员');
		},
		success: function(result) {
			if(result.hasOwnProperty("error")){
				Dialog.alert(result["error"]);
			}
			var ret_id = result["id"];
			var return_str = result["return_str"];
			if(return_str==""){return_str="无运行返回结果"}
			return_str = return_str.replaceAll(" ","&nbsp;",false).replaceAll("\n","<br/>",false);
			$(".hiddenReturnStr[name='"+ret_id+"'] img[name='loading']").hide();
			$(".hiddenReturnStr[name='"+ret_id+"'] span[name='returnStrSpan']").html(return_str).show();
			
			generateGrep(return_str, float_elem, $(".hiddenReturnStr[name='"+ret_id+"'] span[name='returnStrSpan']"));
		}
	});	
}
//点击"运行返回结果字符串"按钮
function clickReturnStr(){
	if(getCheckCount()==false)return;
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')){
			var id = $(o).val();
			//showReturnStrItem(id);
			$(".imgUnfoldReturnStr[alt='"+id+"']").click();
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
function validationNum(val){
	var isNum = /^[>|<|>=|<=|=]\d+$/.test(val);
	return isNum?true:false;
}
function validationCmd(cmd){
	cmd = cmd.replace(/[ ]/g,"");
	if(cmd=="" || cmd.indexOf("请输入命令")>=0)return false;
	return true;
}
function validationNumber(val){
	var isNum = /^-?\d+$/.test(val);
	return isNum?true:false;
}
</script>
<style type="text/css">
img[src="/res/icons/16x16/magnifier.png"]{
	cursor:pointer;
}
</style>
</head>

<body>

<div id="grepDiv" class="toolbar area" >
    <div class="fixed">
	<input id="grepInput" type="text" class="input-shorttext"/> <a id="btnGrep"><img src="/res/icons/16x16/script.png" />grep</a>
	</div>
</div>


	<div class="tab">
		<a class="tab-selected"><img src="/res/icons/16x16/application_side_list.png" />当天任务列表</a>
		<a style="display:none"><img src="/res/icons/16x16/application_side_list.png" />所有日期任务列表</a>
	</div>
	<div class="toolbar">
		<a id="btnReturnStr"><img src="/res/icons/16x16/script.png" />运行返回结果字符串</a>
		<a id="btnRedo"  message="确定重新执行?"><img src="/res/icons/16x16/application_osx_terminal.png" />重新执行</a>
		<a id="btnCmdUnfold"><img src="/res/icons/16x16/table_sort.png" />命令展开</a>
		<a id="btnCmdfold"><img src="/res/icons/16x16/table_sort.png" />命令折叠</a>
		<input id="txtMachineIp" type="text" valid="validationIp" stat="like" trigger="click" trigger_target="btnSearchByIp" invalid_msg="请填入正确ip的一部分" name="filter_state_machine_ip" value="请输入机器ip" class="input-shorttext" onclick="if('请输入机器ip' == $(this).val()){$(this).val('');}"/><a id="btnSearchByIp"><img src="/res/icons/16x16/application_form_magnify.png" />按照ip搜索</a>
		<input id="txtRealCmd" type="text" valid="validationCmd" invalid_msg="请输入命令.." stat="like" trigger="click" trigger_target="btnRealCmd"  name="filter_state_real_cmd" value="请输入命令..." class="input-shorttext" onclick="if('请输入命令...' == $(this).val()){$(this).val('');}"/><a id="btnRealCmd"><img src="/res/icons/16x16/application_form_magnify.png" />按照命令过滤</a>
		<input id="txtExitCode" type="text" valid="validationNumber" invalid_msg="请输入正确的数字.." stat="equal" trigger="click" trigger_target="btnExitCode"  name="filter_state_exit_code" value="请输入结果码..." class="input-shorttext" onclick="if('请输入结果码...' == $(this).val()){$(this).val('-99');}"/><a id="btnExitCode"><img src="/res/icons/16x16/application_form_magnify.png" />按照结果码过滤</a>
		
		<!-- <input id="txtBiggerCurrentRedoTimes" name="filter_state_current_redo_times" value=">=1"  stat="custom" trigger="click" invalid_msg="请输入'>数字'(如>2)" trigger_target="btnBiggerCurrentRedoTimes" type="hidden" class="input-shorttext"/> 
		<a id="btnBiggerCurrentRedoTimes" trigger="click"><img src="/res/icons/16x16/application_form_magnify.png" />大于等于一次的自动重执行任务</a>-->
	</div>
	<table class="grid">
		<thead>
			<tr>
				<th width="30"><span><input type="checkbox" value="0"/></span></th>
				<th width="30"><span>id</span></th>
				<th width="30"><span>任务id</span></th>
				<th width="30"><span><select name="filter_state_complete_success" trigger="change" stat="equal" trigger_target="this"><option value="-1">状态</option><option value="0">失败</option><option  value="1">成功</option></select></span></th>
				<th width="30"><span>结果码</span></th>
				<th><span>机器ip</span></th>
				<th><span>cron_exp</span></th>
				<th><span>真实命令</span></th>
				<th><span>运行时长</span></th>
				<th><span>执行时段</span></th>
				<!-- 
				<th><span><select name="filter_state_run_mode" trigger="change" stat="equal" trigger_target="this"><option value="-1">模式</option><option  value="0">被动</option><option value="1">主动</option></select></span></th>
				 -->
				<th><span><select name="filter_state_exec_type" trigger="change" stat="equal" trigger_target="this"><option value="-1">执行类型</option><option  value="0">crontab执行</option><option value="1">手动重执行</option><option value="2">自动重执行</option><option value="3">当场执行</option></select></span></th>
				<!-- 
				<th><span><select name="filter_state_is_process_node" trigger="change" stat="equal" trigger_target="this"><option value="-1">任务类型</option><option  value="0">单任务</option><option value="1">流程节点</option></select></span></th>
				 -->
				<th><span>重执行</span></th>
				<th><span>备注</span></th>
			</tr>
			
		</thead>
		<tbody>
		<s:iterator value="#request.beanlist" id="recordbean" status="stat">
			<tr id="<s:property value='#recordbean.id' />" title="<s:property value='#recordbean.task.usersISO' />">
				<td align="center"><span><input type="checkbox" value="<s:property value='#recordbean.id' />" /></span></td>
				<td align="center"><span><s:property value="#recordbean.id" /></span></td>
				<td align="center"><span><s:property value="#recordbean.task_id" /></span></td>
				<td align="center"><span class="complete_stats"><s:property escape="false" value="#recordbean.complete_success_ISO"/></span><img alt="<s:property value='#recordbean.id' />" class="imgUnfoldReturnStr" title='close' style="cursor:pointer" src="/res/icons/16x16/plus.png" /></td>
				<td align="center"><span class="exit_code"><s:property escape="false" value="#recordbean.exit_code_ISO" /></span></td>
				<td align="center" title="<s:property value='#recordbean.task.daemon.daemon_version_name' />"><span><s:property value="#recordbean.task.daemon.machine_ip" /></span></td>
				<td align="center"><span><s:property value="#recordbean.task.cron_exp"/></span></td>
				<td align="center" class="cmdClass"><s:property value="#recordbean.real_cmd"/></td>
				<td align="center"><span class="duration"><s:property escape="false" value="#recordbean.duration_ISO"/></span></td>
				<td align="center"><span class="datetime_interval"><s:property escape="false" value="#recordbean.datetime_interval_ISO"/></span></td>
				<!-- 
				<td align="center"><span><s:if test="#recordbean.task.run_mode==true"><font style="color:green">主动</font></s:if><s:else><font style="color:#D2691E">被动</font></s:else></span></td>
				 -->
				<td align="center"><span class="exec_type"><s:property escape="false" value="#recordbean.exec_type_ISO"/></span></td>
				<!-- 
				<td align="center"><span><s:if test="#recordbean.task.is_process_node==true">流程节点</s:if><s:else>单任务</s:else></span></td>
				 -->
				<td align="center"><span><s:property value="#recordbean.current_redo_times"/></span>&#47;<span><s:property value="#recordbean.task.end_redo_times"/></span></td>
				<td align="center" class="cmdClass"><span><s:property value='#recordbean.task.comment' /></span></span></td>
			</tr>
			<tr class='hiddenReturnStr' style='display:none' title='<s:property value="#recordbean.id" />' name='<s:property value="#recordbean.id" />'><td align="center"><span><img  alt="<s:property value='#recordbean.id' />" src='/res/icons/16x16/comment_up.png' class="imgHideReturnStr" style="cursor:pointer"/></span></td><td colspan="15" ><span><img name="loading" src='/res/images/gif/loading.gif'/><span name="returnStrSpan"></span></span></td></tr>
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
		<div id="hidden_filter" style="display:none"><s:property  escape="false"  value="#request.filter"/></div>
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
	<div id="copyright"></div>
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
