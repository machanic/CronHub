
//操作完成高亮提示
function highlight(msg) {
	var tip = $('<a class="highlight"><img src="/res/icons/16x16/exclamation.png" />'+msg+'</a>');
	$('.tab').append(tip);
	tip.fadeOut(10000);
}
function highlight_ok(msg){
	var tip = $('<a class="highlight"><img src="/res/icons/16x16/accept.png" />'+msg+'</a>');
	$('.tab').append(tip);
	tip.fadeOut(10000);
}