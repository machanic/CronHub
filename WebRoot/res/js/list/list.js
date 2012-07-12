$(function() {
	// grid hover 高亮
	$('.grid tbody tr').hover(function() {
		$(this).addClass('grid-tr-hover');
	}, function() {
		$(this).removeClass('grid-tr-hover');
	});
	
	// 全选/全不选
	$('.grid thead input[type=checkbox]').click(function() {
		if($(this).attr('checked')) {
			$('.grid tbody input[type=checkbox]').attr('checked', true);
			$('.grid tbody input[type=checkbox]').parent().parent().parent().addClass('grid-tr-selected');
		}
		else {
			$('.grid tbody input[type=checkbox]').attr('checked', false);
			$('.grid tbody input[type=checkbox]').parent().parent().parent().removeClass('grid-tr-selected');
		}
	});
	
	// 单行选中高亮效果
	$('.grid tbody input[type=checkbox]').click(function(event) {
		if($(this).attr('checked')) {
			$(this).parent().parent().parent().addClass('grid-tr-selected');
		}
		else {
			$(this).parent().parent().parent().removeClass('grid-tr-selected');
		}
		event.stopPropagation();
	}).each(function(i, o) {
		if($(this).attr('checked')) {
			$(this).parent().parent().parent().addClass('grid-tr-selected');
		}
		else {
			$(this).parent().parent().parent().removeClass('grid-tr-selected');
		}
	});
	
});

//列表批量操作
function doBatch(url, message, target) {
	var checkCount = 0;
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')) {
			checkCount++;
		}
	});
	if(checkCount == 0) {
		Dialog.alert('请选中一条以上的记录进行操作');
		return;
	}
	if(message) {
		Dialog.confirm(message, function() {
			var targetstr = target ? 'target="'+target+'"' : "";
			var doBatchForm = $('<form id="doBatchForm" name="doBatchForm" action="'+url+'" '+ targetstr +' method="post"></form>');
			$(document.body).append(doBatchForm);
			$('.grid tbody input[type=checkbox]').each(function() {
				if($(this).attr('checked')) {
					doBatchForm.append($('<input type="hidden" name="ids" value="'+$(this).val()+'" />'));
				}
			});
			doBatchForm.submit();
		});
	}
	else {
		var targetstr = target ? 'target="'+target+'"' : "";
		var doBatchForm = $('<form id="doBatchForm" name="doBatchForm" action="'+url+'" '+ targetstr +' method="post"></form>');
		$(document.body).append(doBatchForm);
		$('.grid tbody input[type=checkbox]').each(function() {
			if($(this).attr('checked')) {
				doBatchForm.append($('<input type="hidden" name="ids" value="'+$(this).val()+'" />'));
			}
		});
		doBatchForm.submit();
	}
}

//列表单个操作
function doSingle(url, message, target) {
	var checkCount = 0;
	$('.grid tbody input[type=checkbox]').each(function(i, o) {
		if($(o).attr('checked')) {
			checkCount++;
		}
	});
	if(checkCount != 1) {
		Dialog.alert('请选中一条记录进行操作');
		return;
	}
	if(message) {
		Dialog.confirm(message, function() {
			var targetstr = target ? 'target="'+target+'"' : "";
			var doSingleForm = $('<form id="doSingleForm" name="doSingleForm" action="'+url+'" '+ targetstr +' method="post"></form>');
			$(document.body).append(doSingleForm);
			$('.grid tbody input[type=checkbox]').each(function() {
				if($(this).attr('checked')) {
					doSingleForm.append($('<input type="hidden" name="id" value="'+$(this).val()+'" />'));
				}
			});
			doSingleForm.submit();
		});
	}
	else {
		var targetstr = target ? 'target="'+target+'"' : "";
		var doSingleForm = $('<form id="doSingleForm" name="doSingleForm" action="'+url+'" '+ targetstr +' method="post"></form>');
		$(document.body).append(doSingleForm);
		$('.grid tbody input[type=checkbox]').each(function() {
			if($(this).attr('checked')) {
				doSingleForm.append($('<input type="hidden" name="id" value="'+$(this).val()+'" />'));
			}
		});
		doSingleForm.submit();
	}
}
