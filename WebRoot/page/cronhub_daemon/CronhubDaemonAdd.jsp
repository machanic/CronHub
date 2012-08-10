<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "  http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>form</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<script type="text/javascript" src="../../res/js/jquery/jquery-1.6.2.min.js"></script>

<script type="text/javascript" src="../../res/js/validate/jquery.metadata.js"></script>
<script type="text/javascript" src="../../res/js/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="../../res/js/validate/messages_cn.js"></script>
<script type="text/javascript" src="../../res/js/validate/extension.js"></script>

<link rel="stylesheet" type="text/css" href="../../res/skin/all.css" />

<script>
function getEmails(){
	var emailAddress = "";
	$('#emailBox option').each(function(index,value){
		emailAddress+=$(this).val();
		if(index < $('#emailBox option').length-1){
			emailAddress+="#";
		}
	});
	return emailAddress;
}
$(function(){
	// 表单校验属性设置
	$.metadata.setType("attr", "validate");
	
	// 表单校验
	var validate = $("#formCronhubDaemonAdd").validate({
	    success: function(label) {
		   label.html("&nbsp;").addClass("valid");
	    }
	});
	
	// Ajax重命名校验
	//     用法参数说明：
	//     1.这个检验规则的名字(自定义)
	//     2.校验请求url
	//     3.附加传的参数数组，为其他表单域的id数组，注意是id
	//     4.校验错误的提示信息
	$.uniqueValidate('uniqueUserName', 'data/validate/renameValidate.jsp', ['org'], '对不起，这个名称重复了');
	$.validatePingPort('pingPort','machine_ip','machine_port');
	$("#hiddenEmail").val(getEmails());
	
	$("#cancelEmailBox").click(function(){
		$('#emailBox option:selected').each(function(){$(this).remove()});
		$("#hiddenEmail").val(getEmails());
	});
	
	$.get("/daemon/getAlertMail.action", function(data){
 		eval("var email = "+data);
 		optionStr= "";
 		for(var key in email){
 			optionStr += "<option value='"+key+"'>"+email[key]+"("+key+")</option>";
 		}
 		$("#emailBox").html(optionStr);
	}); 
});
</script>
</head>

<body>
	<div class="tab">
		<a class="tab-selected"><img src="/res/icons/16x16/application_side_list.png" />新增调度daemon执行者</a>
	</div>
	<form id="formCronhubDaemonAdd" name="formCronhubDaemonAdd" action="/daemon/daemon_add_form_submit.action" method="post">
		<table class="formtable">
		
		<tr  id="machinePortTR" >
			<td width="200">
				<label>daemon名字：</label>
			</td>
			<td>
				<div>
				<input type="text" id="daemon_version_name" name="daemon.daemon_version_name" class="input-text" validate="{required: true}" />
				<b>*</b>
				<span>daemon名字必填</span>
				</div>
			</td>
		</tr>
		<tr id="machineIdTR" >
			<td width="200">
				<label>机器IP：</label>
			</td>
			<td>
				<div>
				<input type="text" id="machine_ip" name="daemon.machine_ip" class="input-text" validate="{required: true,ip:true,pingPort:true}" />
				<b>*</b>
				<span>为daemon管理器表中申请的机器ip,机器IP为只能为点分十进制方式表示</span>
				</div>
			</td>
		</tr>
		<tr  id="machinePortTR" >
			<td width="200">
				<label>执行机器daemon端口：</label>
			</td>
			<td>
				<div>
				<input type="text" id="machine_port" name="daemon.machine_port" class="input-text" validate="{required: true,digits:true,rangelength: [1, 4],pingPort:true}" />
				<b>*</b>
				<span>为daemon管理器表中申请的daemon执行器端口,端口号只能是数字,且别超过4位</span>
				</div>
			</td>
		</tr>
		
		<tr>
			<td>
				<label>通信检测邮件报警：</label>
			</td>
			<td>
				<div>
				<select id="emailBox" multiple="multiple" size="4">
					
				</select>
				<a id="cancelEmailBox"><img src="/res/icons/16x16/cancel.png" /></a>
				<input id="hiddenEmail" name="daemon.lostconn_emailaddress" type="hidden" />
				<input id="hiddenIsEmail" name="daemon.must_lostconn_email" type="hidden" value="true" />
				<input id="hiddenConnStatus" name="daemon.conn_status" type="hidden" value="true" />
				<b>*</b>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<label>描述：</label>
			</td>
			<td>
				<div>
				<textarea id="comment" name="daemon.comment" class="input-textarea" validate="{required: true}"></textarea>
				<b>*</b>
				</div>
			</td>
		</tr>
		<tr style='display:none'>
			<td>
				<label>操作人员：</label>
			</td>
			<td>
				admin
			</td>
		</tr>
		<tr style='display:none'>
			<td>
				<label>操作时间：</label>
			</td>
			<td>
				2011-03-21 12:00:00
			</td>
		</tr>
		<tr id="taskIdTR" style="display:none">
			<td width="200">
				<label>daemon id：</label>
			</td>
			<td>
				<lable>1143</lable>
				<span>由数据库自增形成</span>
			</td>
		</tr>
		</table>
	    <div class="toolbar">
		    <button type="submit"><img src="../../res/icons/16x16/tick.png">确定提交</button>
			<button style="display:none" type="button" onclick="window.location=''"><img src="/res/icons/16x16/arrow_redo.png">返回</button>
	    </div>
	</form>
</body>
</html>
</html>
