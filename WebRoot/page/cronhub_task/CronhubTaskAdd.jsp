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
<script type="text/javascript" src="../../res/js/validate/extension.js"></script><!-- 这个是我用来自定义一些验证函数的,比如到后台验证crontab表达式的有效性等 -->
<script type="text/javascript" src="/res/js/zdialog/zDialog.js"></script> 
<link rel="stylesheet" type="text/css" href="../../res/skin/all.css" />

<script>
$(function(){
	// 表单校验属性设置
	$.metadata.setType("attr", "validate");
	
	// 表单校验
	var validator = $("#formTaskAdd").validate({
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
	//$.uniqueValidate('uniqueUserName', 'data/validate/renameValidate.jsp', ['org'], '对不起，这个名称重复了');
	
	$("#ddl_cronhub_daemon").change(function(){
		$("#machineIdTR,#machinePortTR,#daemonIdTR,#div_daemonid,#div_machineip").hide();
		if($(this).val() == "daemon_id"){
			$("#div_daemonid").show();
		}else{
			$("#div_machineip").show();
		}
	});
	initByParentDaemonPage();
	
	
	
	//一期暂不支持被动模式
	$("input[@type=radio][name='task.run_mode']").change(function(){
		if("false"!=$(this).val())
			{
				Dialog.alert('暂不支持"主动模式"功能,即daemon无需中央服务器通知的模式.');
				$("input[@type=radio][name='task.run_mode'][value='false']").attr("checked",'checked'); 
			}
		});
});
function initByParentDaemonPage(){
	if(parent.daemon_id==0 || parent.machine_ip=="" || parent.machine_port==0){return;}
	$("#txt_daemonid").val(parent.daemon_id).attr("readonly","readonly");
	$("#ddl_cronhub_daemon").attr("readonly","true");
	$("#daemon_id_machine_ip").val(parent.machine_ip);
	$("#daemon_id_machine_port").val(parent.machine_port);
	$("#machineIdTR").fadeIn('slow');
	$("#machinePortTR").fadeIn('slow');
	parent.daemon_id=0;
	parent.machine_ip="";
	parent.machine_port=0;
}
</script>
</head>

<body>
	<div class="tab">
		<a class="tab-selected"><img src="../../res/icons/16x16/application_side_list.png" />新增调度任务</a>
	</div>
	<form id="formTaskAdd" name="formCronhubTaskAdd" action="/task/task_add_form_submit.action" method="post">
		<table class="formtable">
		
		<tr>
			<td width="200">
				<select id="ddl_cronhub_daemon" class="input-shortselect" ><option value="daemon_id">daemon执行器id：</option><option value="machine_ip">机器IP</option></select>
			</td>
			<td>
				<div id="div_daemonid">
				<input type="text" id="txt_daemonid" onChange="if($(this).val()!=''){$('#machineIdTR,#machinePortTR').fadeIn('slow');}" name="task.daemon_id" class="input-text" validate="{digits:true}" />
				<b>*</b>
				<span>daemon只能为已经添加成功的daemon执行器id，数字形式</span>
				</div>
				<div id="div_machineip" style="display:none">
				<input type="text" id="txt_machineip" onChange="if($(this).val()!=''){$('#daemonIdTR').fadeIn('slow');}" class="input-text" validate="{ip:true}" />
				<b>*</b>
				<span>只能添加机器的IP，且为已经存在daemon表中的机器IP</span>
				</div>
			</td>
		</tr>
		<tr id="machineIdTR" style="display:none">
			<td width="200">
				<label>机器IP：</label>
			</td>
			<td>
				<div>
				<input readonly="readonly" type="text"  id="daemon_id_machine_ip" class="input-text" validate="{required: true,ip:true}" />
				<b>*</b>
				<span>不可编辑,为daemon管理器表中申请的机器ip,机器IP为只能为点分十进制方式表示</span>
				</div>
			</td>
		</tr>
		<tr  id="machinePortTR" style="display:none">
			<td width="200">
				<label>执行机器daemon端口：</label>
			</td>
			<td>
				<div>
				<input readonly="readonly" type="text" id="daemon_id_machine_port" class="input-text" validate="{required: true,digits:true}" />
				<b>*</b>
				<span>不可编辑,为daemon管理器表中申请的daemon执行器端口,端口号只能是数字</span>
				</div>
			</td>
		</tr>
		
		<tr id="daemonIdTR" style="display:none">
			<td width="200">
				<label>daemonid：</label>
			</td>
			<td>
				<div>
				<select class="input-shortselect" ><option>2321</option><option>4332</option></select>
				<b>*</b>
				<a><img src="/res/icons/16x16/table_go.png"/></a>
				<span>为指定机器ip在daemon表中的id</span>
				</div>
				
			</td>
			
		</tr>
		
		<tr>
			<td width="200">
				<label>crontab表达式(cron_exp)：</label>
			</td>
			<td>
				<div>
				<input type="text" id="loginname" name="task.cron_exp" class="input-text" validate="{required: true, isValidCronexp: true}" />
				<b>*</b>
				<span>请采用unix/linux的crontab的时间格式表达式</span>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<label>执行脚本命令：</label>
			</td>
			<td>
				<div>
				<input type="text" id="shellCmd" name="task.shell_cmd" class="input-text" validate="{required: true}"/>
				<b>*</b>
				<span>请采用unix/linux的shell支持的命令</span>
				</div>
			</td>
		</tr>
		<tr>
			<td>
			<label>是否支持参数替换(命令中参数还原为现场时间)：</label>
			</td>
			<td>
			<div>
			<input type="radio" name="task.must_replace_cmd" value="true" checked="checked" validate="{required: true}"></radio> 支持 &nbsp;&nbsp;&nbsp;<input type="radio"  name="task.must_replace_cmd" value="false"></radio> 不支持
			<b>*</b>
			<span>[说明]支持:命令中的`中间所夹的内容全部被替换为shell执行后的值,而存入历史数据库记录中的参数为所夹内容执行的返回值.  不支持:命令中的`所夹内容不会被替换,而存入数据库中的仍为``参数,在隔天运行且要得到当时现场的值时有风险</span>
			</div>
			</td>
		</tr>
		<tr>
			<td>
				<label>运行模式：</label></td><td>
				<div>
				<input type="radio" name="task.run_mode" value="true"  validate="{required: true}" ></radio> 主动 &nbsp;&nbsp;&nbsp;<input type="radio" name="task.run_mode" value="false" checked="checked" validate="{required: true}" ></radio> 被动
				<b>*</b>
				<span>主动模式:crontab刷至执行daemon程序,由daemon执行程序自主执行,无需中央服务器通知调度.被动模式:由中央服务器定时调度</span>
				</div>
			</td>
			</tr>
			<tr>
			<td>
				<label>失败时自动重新执行</label></td><td>
				<div>
				<select id="ddl_is_redo" class="input-shortselect" name="task.is_redo" onChange="$(this).val() == 'true' ? $('#div_redoEndTimes').fadeIn('slow') : $('#div_redoEndTimes').hide();$('#txtRedoEndTimes').val('0')"><option value="false" checked="checked">否</option><option value="true">是</option></select>
				<span id="div_redoEndTimes" style="display:none">
				<input type="text" id="txtRedoEndTimes"  name="task.end_redo_times" value="0" onClick="if($(this).val()=='请填入截止执行次数...'){$(this).val('');}"  class="input-text" validate="{required: true,digits:true}" />
				</span>
				<b>*</b>
				<span>失败时自动重新执行的截止次数</span>
				</span>
				</div>
			</td>
		</tr>
			<tr>
			<td>
				<label>描述：</label>
			</td>
			<td>
				<div>
				<textarea id="txtComment" name="task.comment" class="input-textarea" validate="{required: true}"></textarea>
				<b>*</b>
				</div>
			</td>
		</tr>
		
		<tr style="display:none">
			<td>
				<label>汇报<font style="font-weight:bold;color:red">开始执行</font>的http地址：(主动模式)</label></td><td>
				<div>
				<input type="text" id="report_result" value="请填入汇报开始执行的http地址...可以不输入"  class="input-text"  />
				<b>*</b>
				<span>刚开始执行向中央服务器http汇报</span>
				</div>
			</td>
		</tr>
		
		<tr style="display:none">
			<td>
				<label>汇报<font style="font-weight:bold;color:red">执行结束</font>http地址：(主动模式)</label></td><td>
				<span>
				<input type="text" id="report_result" value="请填入汇报结果的http地址...可以不输入"  class="input-text"  />
				<b>*</b>
				<span>执行完了向中央服务器http汇报</span>
				</<span>
			</td>
		</tr>
		
		<tr style="display:none">
			<td>
				<div>
				<label>是否属于流程环节：</label></td><td>
				<input type="checkbox" class="input-checkbox" value="false" onChange="if($(this).attr('checked')){$('#hidden_is_process_node').val('true');$('#process_id').removeAttr('readonly');}else{$('#hidden_is_process_node').val('false');$('#process_id').attr('readonly','readonly');}"/>
				<input type="hidden" id="hidden_is_process_node" name="task.is_process_node"/>
				<input type="text" id="process_id" value="请填入调度流程task的id" readonly ="readonly" class="input-text" validate="{required: true, rangelength: [3, 16], uniqueUserName: true}" />
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
				<label>任务task id：</label>
			</td>
			<td>
				<lable>1143</lable>
				<span>由数据库自增形成</span>
			</td>
		</tr>
		</table>
	    <div class="toolbar">
		    <button type="submit"><img src="../../res/icons/16x16/tick.png">确定</button>
			<button type="button" onclick="validator.resetForm()"><img src="/res/icons/16x16/arrow_redo.png">清空</button>
	    </div>
	</form>
</body>
</html>
</html>
