
/*
 * 唯一性校验方法，后台返回"1"或"0"
 * validateName: 该校验规则的Name，在校验规则中使用
 * url: Ajax请求url
 * extend: 向后台传递的附加字段，需要传给后台的其他输入元素的id数组
 * message: 控件值不唯一时，页面显示的信息
 */
$.extend({
	uniqueValidate: function(validateName, url, extend, message) {
		jQuery.validator.addMethod(validateName, function(value, element) {
			var data = new Object();
			$.each(extend, function(i, o) {
				data[o] = $('#' + o).val();
			});
			data[element.id] = value;
			var returnBoolean = false;
			$.ajax({
				url: url,
				data: data,
				cache: false,
				async: false,
				type: 'POST',
				dataType: 'text',
				timeout: 10000,
				error: function() {
					jQuery.validator.messages[validateName] = '对不起，服务器响应超时，请联系管理员';
				},
				success: function(result) {
					if(result == '1') {
						returnBoolean = true;
					}
					else {
						returnBoolean = false;
					}
				}
			});
			return returnBoolean;
		}, message);
	}
});
$.extend({
	validatePingPort: function(validateName,ip_id,port_id) {
		jQuery.validator.addMethod(validateName,function(value,element){
			var returnBoolean = false;
			var ip = $("#"+ip_id).val();
			var port = $("#"+port_id).val();
			if(ip =="" || port==""){return true}
			$.ajax({
						url: "/validation/validate_ping.action",
						data: {"ip":$("#"+ip_id).val(),"port":$("#"+port_id).val()},
						cache: false,
						async: false,
						type: 'POST',
						dataType: 'text',
						timeout: 10000,
						error: function() {
							jQuery.validator.messages[validateName] = '对不起，服务器响应超时，请联系管理员';
						},
						success: function(result) {
							if(result.trim() == "success") {
								returnBoolean = true;
							}
							else {
								returnBoolean = false;
							}
						}
					});
			return returnBoolean;
		},"无法与服务器:"+$("#"+ip_id).val()+"上端口:"+$("#"+port_id).val()+"的daemon程序取得通信联络!");
	}
});

jQuery.validator.addMethod("isValidCronexp",function(value,element){
	var returnBoolean = false;
	$.ajax({
				url: "/validation/validate_cronexp.action",
				data: {"cron_exp":value},
				cache: false,
				async: false,
				type: 'POST',
				dataType: 'text',
				timeout: 10000,
				error: function() {
					jQuery.validator.messages[validateName] = '对不起，服务器响应超时，请联系管理员';
				},
				success: function(result) {
					if(result.trim() == "success") {
						returnBoolean = true;
					}
					else {
						returnBoolean = false;
					}
				}
			});
	return returnBoolean;
},"请填写正确的crontab表达式!");

/*
 * jQuery validation 验证类型扩展
 */
// 邮政编码验证
jQuery.validator.addMethod("isZipCode", function(value, element) {
  var zip = /^[0-9]{6}$/;
  return this.optional(element) || (zip.test(value));
}, "请正确填写您的邮政编码!");

// 身份证号码验证
jQuery.validator.addMethod("isIdCardNo", function(value, element) {
  var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;
  return this.optional(element) || (idCard.test(value));
}, "请输入正确的身份证号码!");

// 手机号码验证
jQuery.validator.addMethod("isMobile", function(value, element) {
  var length = value.length;
  return this.optional(element) || (length == 11 && /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/.test(value));
}, "请正确填写您的手机号码!");

// 电话号码验证
jQuery.validator.addMethod("isPhone", function(value, element) {
  var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
  return this.optional(element) || (tel.test(value));
}, "请正确填写您的电话号码!");

// 用户名字符验证
jQuery.validator.addMethod("userName", function(value, element) {
  return this.optional(element) || /^[\u0391-\uFFE5\w]+$/.test(value);
}, "用户名只能包括中文字、英文字母、数字和下划线!");

// 联系电话(手机/电话皆可)验证
jQuery.validator.addMethod("isTel", function(value,element) {
    var length = value.length;
    var mobile = /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/;
    var tel = /^\d{3,4}-?\d{7,9}$/;
    return this.optional(element) || (tel.test(value) || mobile.test(value));
}, "请正确填写您的联系电话!");

// IP地址验证
jQuery.validator.addMethod("ip", function(value, element) {
  return this.optional(element) || /^((?:(?:25[0-5]|2[0-4]\d|[01]?\d?\d)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d?\d))$/.test(value);
}, "请填写正确的IP地址！");
// 程序版本字符串验证
jQuery.validator.addMethod("contains_date", function(value, element) {
  return this.optional(element) || /^.*\d{4}-\d{2}-\d{2}.*$/.test(value);
}, "为了描述日期,请填写'包含'诸如2011-04-03这样的日期字符串！");
// 含有中文的最大字符长度校验
jQuery.validator.addMethod("cnRangelength", function(value, element, param) {
	var length = value.length;
	for(var i = 0; i < value.length; i++) {
		if(value.charCodeAt(i) > 127) {
			length++;
		}
	}
	return this.optional(element) || ( length >= param[0] && length <= param[1] );
}, jQuery.validator.format("请输入一个长度介于 {0} 和 {1} 之间的字符串(一个中文长度为2)"));

// 只允许输入英文字符，数字和下划线
jQuery.validator.addMethod("charNo",function(value,element) {
	var length = value.length;
	var your_tel =/[/^\W+$/]/g;
	return this.optional(element) || (!your_tel.test(value));
},"请输入英文字符、数字、下划线！");

// 只允许输入中文、英文字符，数字和下划线
jQuery.validator.addMethod("stringCheck", function(value, element) {
	return this.optional(element) || /^[\u0391-\uFFE5\w]+$/.test(value);
}, "只能包括中文字、英文字母、数字和下划线");

// 手机号码验证
jQuery.validator.addMethod("mobile", function(value, element) {
    var length = value.length;
    var mobile =  /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/;
    return this.optional(element) || (length == 11 && mobile.test(value));
}, "手机号码格式错误");

// 电话号码验证
jQuery.validator.addMethod("phone", function(value, element) {
    var tel = /^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$/;
    return this.optional(element) || (tel.test(value));
}, "电话号码格式错误");

// 邮政编码验证
jQuery.validator.addMethod("zipCode", function(value, element) {
    var tel = /^[0-9]{6}$/;
    return this.optional(element) || (tel.test(value));
}, "邮政编码格式错误");

// QQ号码验证
jQuery.validator.addMethod("qq", function(value, element) {
    var tel = /^[1-9]\d{4,9}$/;
    return this.optional(element) || (tel.test(value));
}, "qq号码格式错误");

// 字母和数字的验证
jQuery.validator.addMethod("chrnum", function(value, element) {
    var chrnum = /^([a-zA-Z0-9]+)$/;
    return this.optional(element) || (chrnum.test(value));
}, "只能输入数字和字母(字符A-Z, a-z, 0-9)");

// 中文的验证
jQuery.validator.addMethod("chinese", function(value, element) {
    var chinese = /^[\u4e00-\u9fa5]+$/;
    return this.optional(element) || (chinese.test(value));
}, "只能输入中文");
