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

<script type="text/javascript" src="/res/js/list/list.js"></script>

<script type="text/javascript" src="/res/js/highlight/highlight.js"></script>

<script type="text/javascript" src="/res/js/contextmenu/jquery.contextmenu.js"></script>

<script type="text/javascript" src="/res/js/zdialog/zDrag.js"></script>
<script type="text/javascript" src="/res/js/zdialog/zDialog.js"></script>
<script type="text/javascript" src="/res/js/pager/pager.js"></script>
<script type="text/javascript" src="/res/js/utils/json2.js"></script>
<script type="text/javascript" src="/res/js/utils/string_utils.js"></script>


<!-- jquery & jq ui add -->
<link rel="stylesheet" href="/res/js/jquery-ui-1.10.4/css/redmond/jquery-ui-1.10.4.custom.css" />
<script src="/res/js/jquery-ui-1.10.4/js/jquery-1.10.2.js"></script>
<script src="/res/js/jquery-ui-1.10.4/js/jquery-ui-1.10.4.custom.js"></script>  

<script type="text/javascript" src="/res/js/jquery-ui/ui/tooltip.js"></script> 
<link rel="stylesheet" type="text/css" href="/res/skin/all.css" />

<script>
var dialog = null;
var default_mail="请输入邮箱前缀";
var default_user="请输入用户姓名";
$(function() {
	deleteOne();
	
	$("#txtMailName").val(default_mail).attr("title",default_mail).click(function(){if($(this).val()==default_mail) $(this).val("");}).tooltip({
		hide: {
			effect: "explode",
			delay: 250
			}
			});
	
	$("#txtUserName").val(default_user).attr("title",default_user).click(function(){if($(this).val()==default_user) $(this).val("");}).tooltip({
		show: {
			effect: "slideDown",
			delay: 250
			}
			});
	
});

function submitAddUserName(){
	var user_name = $("#txtUserName").val();
	var mail_name = $("#txtMailName").val();
	if(user_name == "" || mail_name==""
			|| user_name==default_user || mail_name==default_mail){
		Dialog.alert("用户名和邮箱前缀不得为空");
		return;
	}
	$("#formSubmitUserName").submit();
}

function deleteOne(){
	$(".grid tr img[name='del']").click(function(){
		var del_id = $(this).attr("title");
		Dialog.confirm("是否要删除相应报警人?将会删除掉所有任务关于此人的报警",function(){
			$.ajax({
				url: "/user/user_delete.action",
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
		<a class="tab-selected"><img src="/res/icons/16x16/application_side_list.png" />用户管理</a>
	</div>
	
	<div class="toolbar">
	<form id="formSubmitUserName" action="/user/user_add_submit.action" method="post">
		<input id="txtMailName"  name="mail_name" class="input-shorttext" type="text" />
		<input id="txtUserName" name="user_name" class="input-shorttext" type="text" />
		<a onclick="submitAddUserName();"><img src="/res/icons/16x16/user_add.png" />添加用户</a>
	</form>
		
	</div>
	
	<table class="grid">
		<thead>
			<tr>
			<th><span>id</span></th>
				<th><span>用户姓名</span></th>
				<th><span>用户邮箱前缀(报警用)</span></th>
				<th><span>删除</span></th>				
			</tr>
		</thead>
		<tbody>
			<s:iterator value="#request.beanlist" id="userbean" status="statu">
				<tr id="<s:property value='#userbean.id' />">
				<td align="center"><span><s:property value='#userbean.id' /></span></td>
					<td align="center"><span><s:property value="#userbean.user_name" /></span></td>
					<td align="center"><span><s:property value="#userbean.mail_name" /></span></td>
					<td align="center"><span><img title="<s:property value='#userbean.id' />" name="del" style="cursor:pointer" src="/res/icons/16x16/cancel.png"/></span></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</body>
</html>
