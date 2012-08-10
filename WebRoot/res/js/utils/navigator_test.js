
function check(reg) {
	var ug = navigator.userAgent.toLowerCase();
	return reg.test(ug);
}
function isFireFox() {
	var ug = navigator.userAgent.toLowerCase();
	var userAgent = navigator.userAgent;
	userAgent.innerHTML = "\u6d4f\u89c8\u5668\u7684\u7528\u6237\u4ee3\u7406\u62a5\u5934\uff1a" + ug;
	var browserType = "";
	var ver = "";
 
  //检测IE及版本
	var IE = ug.match(/msie\s*\d\.\d/); //提取浏览器类型及版本信息，注match()方法返回的是数组而不是字符串
	var isIE = check(/msie/);
	
  //检测firefox及版本
	var firefox = ug.match(/firefox\/\d\.\d/gi);
	var isFirefox = check(/firefox/);
	return !isIE && isFirefox;
}

