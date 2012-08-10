/**
 * AjaxModel
 */
function AjaxModel(options) {
	this.loadUrl = null;    // 请求路径
	this.data = null;       // 请求参数对象
	this.dataType = 'json'; // 返回值类型，默认json
	this.timeout = 10000;   // 请求超时时间，默认10秒
	
	$.extend(this, options);
}
/**
 * load()
 */
AjaxModel.prototype.load = function() {
	var _this = this;
	
	$.ajax({
		url: this.loadUrl,
		data: this.data,
		cache: false,
		async: true,
		type: 'POST',
		dataType: this.dataType,
		timeout: this.timeout,
		beforeSend: function() {
			_this.beforeSend();
		},
		error: function(data) {
			//如果定义了errback,就调用errback
			if(_this.hasOwnProperty("errback")){
				_this.errback(data);
			}else{
				alert('jqTree Ajax load error');
			}
		},
		success: function(data) {
			data = _this.xmlParse(data);
			if(data!=null){//如果获取到空root节点(也就是说没有数据)
				_this.success(data);
			}
			_this.callback(data);
		}
	});
}
/**
 * success(data)
 */
AjaxModel.prototype.success = function(data) {}
/**
 * xmlParse(data)
 */
AjaxModel.prototype.xmlParse = function(data) {}
/**
 * beforeSend()
 */
AjaxModel.prototype.beforeSend = function() {}
/**
 * BaseNode
 */
function BaseNode(options) {
	AjaxModel.call(this, options);
	
	this.tree = null;                    // 节点所属树对象
	this.id = null;                      // 节点id
	this.name = null;                    // 节点name
	this.parentNode = null;              // 父节点对象
	this.level = null;                   // 节点级数
	this.indent = 0;                     // 节点缩进距离，默认0
	this.createRequestParam = null;      // 构造请求参数的函数
	this.click = null;                   // 节点单击事件处理函数
	this.dblclick = null;                // 节点双击事件处理函数
	this.subNodes = new Array();         // 子节点对象数组
	this.loaded = false;                 // 是否已加载子节点
	this.isParent = false;               // 是否还有子节点，默认没有
	this.open = true,                    // 下级面板是否展开，默认展开
	this.class_node = 'jqtree-node';     // 节点node div样式
	this.class_name = 'jqtree-name';     // 节点name span样式
	this.class_sub = 'jqtree-sub';       // 下级节点sub div样式
	this.div_node = $('<div></div>');    // 节点node div
	this.span_name = $('<span></span>'); // 节点name span
	this.div_sub = $('<div></div>');     // 下级节点 sub div
	this.class_hover = 'jqtree-node-hover'; // hover高亮效果样式
	this.callback = null;
	$.extend(this, options);
}
/**
 * extend from AjaxModel
 */
BaseNode.prototype = new AjaxModel();
/**
 * load()
 */
BaseNode.prototype.load = function() {
	if(this.loaded) {
		return;
	}
	
	this.loaded = true;
	
	if(this.createRequestParam) {
		this.createRequestParam(this);
	}
	
	AjaxModel.prototype.load.call(this);
}
/**
 * view()
 */
BaseNode.prototype.view = function(node) {
	var _this = this;
	
	this.div_node.addClass(this.class_node);
	for(var key in this){
		if(this[key] && (typeof this[key]=='string')&& this[key].constructor==String && !this.span_name.attr(key.toString())){
		this.span_name.attr(key.toString(),this[key].toString());}
	}
	this.span_name.addClass(this.class_name);
	this.div_sub.addClass(this.class_sub);
	
	this.span_name.append(this.name);
	this.div_node.append(this.span_name);
	
	this.div_node.css('padding-left', this.indent * this.level);
	
	if(this.parentNode) {
		this.parentNode.div_sub.append(this.div_node);
		this.div_node.after(this.div_sub);
	}
	
	this.div_node.hover(function() {
		$(this).addClass(_this.class_hover);
	}, function() {
		$(this).removeClass(_this.class_hover);
	});
}
/**
 * clickBind()
 */
BaseNode.prototype.clickBind = function() {
	var _this = this;
	
	if(this.click) {
		this.div_node.click(function(event) {
			_this.click(_this, event);
			
			// 单击后高亮保持
			_this.tree.container.find('.jqtree-node-selected').removeClass('jqtree-node-selected');
			_this.div_node.addClass('jqtree-node-selected');
		});
	}
}
/**
 * dblclickBind()
 */
BaseNode.prototype.dblclickBind = function() {
	var _this = this;
	
	if(this.dblclick) {
		this.div_node.dblclick(function(event) {
			_this.dblclick(_this, event);
		});
	}
}
/**
 * control()
 */
BaseNode.prototype.control = function() {
	var _this = this;
	
	this.clickBind();
	this.dblclickBind();
	
	// 整合右键菜单
	if(this.tree.contextmenu) {
		this.div_node.bind('contextmenu', function(e) {
			_this.tree.contextmenu.show(_this, e);
			
			// 右键菜单选中高亮保持
			_this.tree.container.find('.context-menu-selected').removeClass('context-menu-selected');
			$(this).addClass('context-menu-selected');
			return false;
		});
	}
}
/**
 * toggle()
 */
BaseNode.prototype.toggle = function() {
	if(this.loaded) {
		if(this.open) {
			this.collapse();
		}
		else {
			this.expand();
		}
	}
	else {
		this.load();
	}
}
/**
 * collapse()
 */
BaseNode.prototype.collapse = function() {
	this.div_sub.hide();
	this.open = false;
}
/**
 * expand()
 */
BaseNode.prototype.expand = function() {
	this.div_sub.show();
	this.open = true;
}
/**
 * success(data)
 */
BaseNode.prototype.success = function(data) {
	var _this = this;
	
	$.each(data, function(i, o) {
		$.extend(o, _this.tree.options, {
			parentNode: _this,
			level: _this.level + 1
		});

		if(o.nodes) {
			o.isParent = false;
		}
		else {
			o.nodes = [];
		}
		if(o.isParent) {
			o.nodes = null;
		}
		
		var node = _this.createSubNode(o);
		
		node.view(node);
		node.control();
		
		if(o.nodes) {
			node.loaded = true;
			
			if(o.nodes.length == 0) {
				node.isParent = false;
			}
			else {
				node.isParent = true;
			}
			
			node.success(o.nodes);
		}
		_this.subNodes[i] = node;
	});
	
	if(this.open) {
		this.expand();
	}
	else {
		this.collapse();
	}
}
/**
 * xmlParse()
 */
BaseNode.prototype.xmlParse = function(data) {
	if(this.dataType == 'xml') {
		var parser = new XMLParser();
		data = parser.parse($(data).children().children());
	}
	return data;
}
/**
 * createSubNode(o)
 */
BaseNode.prototype.createSubNode = function(o) {
	return new BaseNode(o);
}
/**
 * BaseTree
 */
function BaseTree(options) {
	AjaxModel.call(this, options);
	
	this.container = null;        // 容器
	this.options = options;       // 树参数对象，也是树节点参数对象，参数对象的大部分属性为树节点所用
	this.rootNodes = new Array(); // 根节点对象数组，树可以有一个根，也可以是多个根
	
	$.extend(this, options);
}
/**
 * extend from AjaxModel
 */
BaseTree.prototype = new AjaxModel();
/**
 * init()
 */
BaseTree.prototype.init = function() {
	this.container.get(0).onselectstart = function() { // 禁止树被鼠标选择
		return false;
	};

	this.options.tree = this; // 树对象作为树节点参数对象的属性
	
	this.load();
}
/**
 * success(data)
 */
BaseTree.prototype.success = function(data) {
	var _this = this;
	
	$.each(data, function(i, o) {
		$.extend(o, _this.options, {
			parentNode: null,
			level: 0
		});

		if(o.nodes) {
			o.isParent = false;
		}
		else {
			o.nodes = [];
		}
		if(o.isParent) {
			o.nodes = null;
		}
		
		var node = _this.createRootNode(o);
		
		node.view(o);
		node.control();
		
		_this.container.append(node.div_node);
		node.div_node.after(node.div_sub);
		
		if(o.nodes) {
			node.loaded = true;
			
			if(o.nodes.length == 0) {
				node.isParent = false;
			}
			else {
				node.isParent = true;
			}
			
			node.success(o.nodes);
		}
		_this.rootNodes[i] = node;
	});
}
/**
 * xmlParse()
 */
BaseTree.prototype.xmlParse = function(data) {
	if(this.dataType == 'xml') {
		var parser = new XMLParser();
		data = parser.parse($(data).children().children());
	}
	return data;
}
/**
 * createRootNode(o)
 */
BaseTree.prototype.createRootNode = function(o) {
	return new BaseNode(o);
}
/**
 * IconNode
 */
function IconNode(options) {
	BaseNode.call(this, options);
	
	this.arrowClick = null;                        // 箭头单击事件处理函数
	this.class_node_horiz = 'jqtree-node-horiz';   // 节点横向排列样式
	this.class_sub_horiz = 'jqtree-sub-horiz';     // 下级节点横向排列sub div样式
	this.class_icon = 'jqtree-icon';               // 图片div基本样式
	this.class_arrow = 'jqtree-arrow';             // 箭头样式
	this.class_arrow_close = 'jqtree-arrow-close'; // 箭头关闭样式
	this.class_icon_close = 'jqtree-icon-close';   // 图标关闭样式
	this.class_arrow_open = 'jqtree-arrow-open';   // 箭头打开样式
	this.class_icon_open = 'jqtree-icon-open';     // 图标打开样式
	this.class_loading = 'jqtree-loading';         // 加载中样式
	this.div_arrow = $('<div></div>');             // 箭头div
	this.div_icon = $('<div></div>');              // 图标div
	this.div_loading = $('<div></div>');           // 加载中div
	this.horiz = false;                            // 是否横向显示
	this.icon = null;                              // 节点自定义icon
	
	$.extend(this, options);
}
/**
 * extend from BaseTreeNode
 */
IconNode.prototype = new BaseNode();
/**
 * view()
 */
IconNode.prototype.view = function() {
	var _this = this;
	
	this.div_arrow.addClass(this.class_icon).addClass(this.class_arrow).addClass(this.class_arrow_close);
	this.div_icon.addClass(this.class_icon).addClass(this.class_icon_close);
	this.div_loading.addClass(this.class_loading);
	
	this.div_node.append(this.div_arrow);
	this.div_node.append(this.div_icon);
	
	if(this.icon) {
		this.div_icon.css('background', 'url('+this.icon+') no-repeat');
	}

	BaseNode.prototype.view.call(this);
	
	if(this.horiz) {
		this.div_node.addClass(this.class_node_horiz);
		
		this.parentNode.div_sub.addClass(this.class_sub_horiz);
		this.parentNode.div_sub.css('padding-left', this.indent * this.level);
		this.div_node.css('padding-left', '0');
		
		this.parentNode.div_sub.hover(function() {
			$(this).addClass(_this.class_hover);
		}, function() {
			$(this).removeClass(_this.class_hover);
		});
	}
}
/**
 * arrowClickBind()
 */
IconNode.prototype.arrowClickBind = function() {
	var _this = this;
	
	if(this.arrowClick) {
		this.div_arrow.click(function(event) {
			_this.arrowClick(_this, event);
		});
	}
}
/**
 * control()
 */
IconNode.prototype.control = function() {
	BaseNode.prototype.control.call(this);
	
	this.arrowClickBind();
}
/**
 * collapse()
 */
IconNode.prototype.collapse = function() {
	BaseNode.prototype.collapse.call(this);
	
	if(this.subNodes != 0) {
		this.div_arrow.removeClass(this.class_arrow_open).addClass(this.class_arrow_close);
		this.div_icon.removeClass(this.class_icon_open).addClass(this.class_icon_close);
	}
}
/**
 * expand()
 */
IconNode.prototype.expand = function() {
	BaseNode.prototype.expand.call(this);
	
	if(this.subNodes != 0) {
		this.div_arrow.removeClass(this.class_arrow_close).addClass(this.class_arrow_open);
		this.div_icon.removeClass(this.class_icon_close).addClass(this.class_icon_open);
	}
}
/**
 * success(data)
 */
IconNode.prototype.success = function(data) {
	var _this = this;
	
	this.div_loading.remove();
	
	if(data.length == 0) {
		this.div_arrow.removeClass(this.class_arrow_close);
		this.div_icon.removeClass(this.class_icon_close).addClass(this.class_icon_open);
		return;
	}
	
	BaseNode.prototype.success.call(this, data);
}
/**
 * createSubNode(o)
 */
IconNode.prototype.createSubNode = function(o) {
	return new IconNode(o);
}
/**
 * beforeSend()
 */
IconNode.prototype.beforeSend = function() {
	this.div_loading.css('background-position', this.indent * (this.level + 1) + 'px 0');
	this.div_node.after(this.div_loading);
}
/**
 * IconTree
 */
function IconTree(options) {
	BaseTree.call(this, options);
	
	this.arrowClick = null; // 箭头单击事件处理函数
	
	$.extend(this, options);
}
/**
 * extend from BaseTree
 */
IconTree.prototype = new BaseTree();
/**
 * createRootNode(o)
 */
IconTree.prototype.createRootNode = function(o) {
	return new IconNode(o);
}
/**
 * CheckNode
 */
function CheckNode(options) {
	IconNode.call(this, options);
	
	this.checkClick = null;                            // 复选框单击事件处理函数
	this.checked = false;                              // 复选框是否选中
	this.halfChecked = false;                          // 复选框是否半选中
	this.checkbox = true;                              // 节点是否具有复选框，默认值true
	this.checkInteractParent = true;                   // 选中是否影响父节点，默认值true
	this.checkInteractSub = true;                      // 选中是否影响子节点，默认值true
	this.uncheckInteractParent = true;                 // 取消选中是否影响父节点，默认值true
	this.uncheckInteractSub = true;                    // 取消选中是否影响子节点，默认值true
	this.class_checked_true = 'jqtree-checked-true';   // 复选框选中样式
	this.class_checked_false = 'jqtree-checked-false'; // 复选框未选中样式
	this.class_checked_half = 'jqtree-checked-half';   // 复选框半选中样式
	this.div_checkbox = $('<div></div>');              // 复选框div
	
	$.extend(this, options);
}
/**
 * extend from IconNode
 */
CheckNode.prototype = new IconNode();
/**
 * view()
 */
CheckNode.prototype.view = function() {
	this.div_checkbox.addClass(this.class_icon);
	if(this.checked) {
		this.div_checkbox.addClass(this.class_checked_true);
	}
	else {
		this.div_checkbox.addClass(this.class_checked_false);
	}
	
	IconNode.prototype.view.call(this);
	
	if(this.checkbox) {
		this.div_arrow.after(this.div_checkbox);
	}
}
/**
 * checkClickBind()
 */
CheckNode.prototype.checkClickBind = function() {
	var _this = this;
	
	if(this.checkClick) {
		this.div_checkbox.click(function(event) {
			_this.checkClick(_this, event);
		});
	}
}
/**
 * control()
 */
CheckNode.prototype.control = function() {
	IconNode.prototype.control.call(this);
	
	this.checkClickBind();
}
/**
 * checkToggle()
 */
CheckNode.prototype.checkToggle = function() {
	if(this.checked) {
		this.uncheck();

		if(this.uncheckInteractSub) {
			this.interactSubNodes();
		}
		
		if(this.parentNode && this.uncheckInteractParent) {
			this.parentNode.interactedBySubNodes();
		}
	}
	else {
		this.check();

		if(this.checkInteractSub) {
			this.interactSubNodes();
		}
		
		if(this.parentNode && this.checkInteractParent) {
			this.parentNode.interactedBySubNodes();
		}
	}
}
/**
 * interactSubNodes()
 */
CheckNode.prototype.interactSubNodes = function() {
	var _this = this;
	
	$.each(this.subNodes, function(i, o) {
		if(_this.checked) {
			o.check();
			
			if(o.checkInteractSub) {
				o.interactSubNodes();
			}
		}
		else {
			o.uncheck();
			
			if(o.uncheckInteractSub) {
				o.interactSubNodes();
			}
		}
	});
}
/**
 * interactedBySubNodes()
 */
CheckNode.prototype.interactedBySubNodes = function() {
	var checkedCount = 0;
	var halfCheckedCount = 0;
	
	$.each(this.subNodes, function(i, o) {
		if(o.checked) {
			checkedCount++;
		}
		if(o.halfChecked) {
			halfCheckedCount++;
		}
	});
	
	if(checkedCount == this.subNodes.length) {
		this.check();
		
		if(this.parentNode && this.checkInteractParent) {
			this.parentNode.interactedBySubNodes();
		}
	}
	else if(checkedCount == 0) {
		this.uncheck();
		
		if(this.parentNode && this.uncheckInteractParent) {
			this.parentNode.interactedBySubNodes();
		}
	}
	else {
		this.halfCheck();
		
		if(this.parentNode && this.checkInteractParent) {
			this.parentNode.interactedBySubNodes();
		}
	}
	
	if(halfCheckedCount > 0) {
		this.halfCheck();
		
		if(this.parentNode && this.checkInteractParent) {
			this.parentNode.interactedBySubNodes();
		}
	}
}
/**
 * check()
 */
CheckNode.prototype.check = function() {
	this.checked = true;
	this.halfChecked = false;
	this.div_checkbox.removeClass(this.class_checked_false).removeClass(this.class_checked_half).addClass(this.class_checked_true);
}
/**
 * uncheck()
 */
CheckNode.prototype.uncheck = function() {
	this.checked = false;
	this.halfChecked = false;
	this.div_checkbox.removeClass(this.class_checked_true).removeClass(this.class_checked_half).addClass(this.class_checked_false);
}
/**
 * halfCheck()
 */
CheckNode.prototype.halfCheck = function() {
	this.checked = false;
	this.halfChecked = true;
	this.div_checkbox.removeClass(this.class_checked_true).removeClass(this.class_checked_false).addClass(this.class_checked_half);
}
/**
 * createSubNode(o)
 */
CheckNode.prototype.createSubNode = function(o) {
	return new CheckNode(o);
}
/**
 * getCheckedNodes()
 */
CheckNode.prototype.getCheckedNodes = function() {
	if(this.checked && this.checkbox || this.halfChecked && this.checkbox) {
		this.tree.checkedNodes[this.tree.checkedNodes.length] = this;
	}
	
	$.each(this.subNodes, function(i, o) {
		o.getCheckedNodes();
	});
}
/**
 * CheckTree
 */
function CheckTree(options) {
	IconTree.call(this, options);
	
	this.checkClick = null;   // 复选框单击事件处理函数
	this.checkedNodes = null; // 选中节点的数组
	
	$.extend(this, options);
}
/**
 * extend from IconTree
 */
CheckTree.prototype = new IconTree();
/**
 * createRootNode(o)
 */
CheckTree.prototype.createRootNode = function(o) {
	return new CheckNode(o);
}
/**
 * getCheckedNodes()
 */
CheckTree.prototype.getCheckedNodes = function() {
	this.checkedNodes = new Array();
	
	$.each(this.rootNodes, function(i, o) {
		o.getCheckedNodes();
	});
	
	return this.checkedNodes;
}
/**
 * val(attrName)
 */
CheckTree.prototype.val = function(attrName) {
	this.getCheckedNodes();
	var str = '';
	
	$.each(this.checkedNodes, function(i, o) {
		str += ',' + o[attrName];
	});
	str = str.substring(1, str.length);
	
	return str;
}
/**
 * RadioNode
 */
function RadioNode(options) {
	IconNode.call(this, options);
	
	this.radioClick = null;                                             // 单选框单击事件处理函数
	this.checked = false;                                               // 单选框是否选中
	this.radio = true;                                                  // 节点是否具有单选框，默认值true
	this.class_radio = 'jqtree-radio';                                  // 单选框div样式
	this.div_radio = $('<div></div>');                                  // 单选框div
	this.input_radio = $('<input type="radio" name="jqtreeradio" />');  // 单选框input
	
	$.extend(this, options);
}
/**
 * extend from IconTreeNode
 */
RadioNode.prototype = new IconNode();
/**
 * view()
 */
RadioNode.prototype.view = function() {
	this.input_radio.attr('value', this.id);
	this.div_radio.addClass(this.class_icon).addClass(this.class_radio);
	this.div_radio.append(this.input_radio);
	
	if(this.checked) {
		this.input_radio.attr('checked', true);
		this.tree.checkedNode = this;
	}
	
	IconNode.prototype.view.call(this);
	
	if(this.radio) {
		this.div_arrow.after(this.div_radio);
	}
}
/**
 * checkClickBind()
 */
RadioNode.prototype.checkClickBind = function() {
	var _this = this;
	
	this.input_radio.click(function(event) {
		_this.tree.checkedNode = _this;
		
		if(_this.radioClick) {
			_this.radioClick(_this, event);
		}
	});
}
/**
 * control()
 */
RadioNode.prototype.control = function() {
	IconNode.prototype.control.call(this);
	
	this.checkClickBind();
}
/**
 * createSubNode(o)
 */
RadioNode.prototype.createSubNode = function(o) {
	return new RadioNode(o);
}
/**
 * RadioTree
 */
function RadioTree(options) {
	IconTree.call(this, options);
	
	this.checkedNode = null; // 单选中的节点
	
	$.extend(this, options);
}
/**
 * extend from IconTree
 */
RadioTree.prototype = new IconTree();
/**
 * createRootNode(o)
 */
RadioTree.prototype.createRootNode = function(o) {
	return new RadioNode(o);
}
/**
 * getCheckedNode()
 */
RadioTree.prototype.getCheckedNode = function() {
	return this.checkedNode;
}
/**
 * val(attrName)
 */
RadioTree.prototype.val = function(attrName) {
	if(this.checkedNode) {
		return this.checkedNode[attrName];
	}
	else {
		return '';
	}
}
/**
 * DropdownCheckTree
 */
function DropdownCheckTree(options) {
	this.div_jqtree = $('<div></div>');
	this.container = null;
	this.treeoptions = options;
	
	$.extend(this, options);
	
	this.init();
}
/**
 * init()
 * @return
 */
DropdownCheckTree.prototype.init = function() {
	var _this = this;
	
	this.div_jqtree.addClass('jqtree-dropdown');
	$(document.body).append(this.div_jqtree);
	
	this.div_jqtree.css('top', this.top);
	this.div_jqtree.css('left', this.left);
	this.div_jqtree.width(this.width);
	this.div_jqtree.height(this.height);
	
	// 阻止事件传播
	this.container.click(function(event) {
		event.stopPropagation();
		_this.div_jqtree.show();
	});
	
	this.div_jqtree.click(function(event) {
		event.stopPropagation();
	});
	
	this.treeoptions.container = this.div_jqtree;
	
	var tree = new CheckTree(this.treeoptions);
	tree.init();
	
	// 点击页面其他地方时层消失并取值
	$(document).click(function() {
		_this.div_jqtree.hide();
	});
}
/**
 * DropdownRadioTree
 */
function DropdownRadioTree(options) {
	this.div_jqtree = $('<div></div>');
	this.container = null;
	this.treeoptions = options;
	
	$.extend(this, options);
	
	this.init();
}
/**
 * init()
 * @return
 */
DropdownRadioTree.prototype.init = function() {
	var _this = this;
	
	this.div_jqtree.addClass('jqtree-dropdown');
	$(document.body).append(this.div_jqtree);
	
	this.div_jqtree.css('top', this.top);
	this.div_jqtree.css('left', this.left);
	this.div_jqtree.width(this.width);
	this.div_jqtree.height(this.height);
	
	// 阻止事件传播
	this.container.click(function(event) {
		event.stopPropagation();
		_this.div_jqtree.show();
	});
	
	this.div_jqtree.click(function(event) {
		event.stopPropagation();
	});
	
	this.treeoptions.container = this.div_jqtree;
	
	var tree = new RadioTree(this.treeoptions);
	tree.init();
	
	// 点击页面其他地方时层消失并取值
	$(document).click(function() {
		_this.div_jqtree.hide();
	});
}
/**
 * XMLParser
 */
function XMLParser() {}
/**
 * parse(_$_XMLDocument)
 */
XMLParser.prototype.parse = function(_$_XMLDocument) {
	var _this = this;
	
	var json = null; // nodes属性的默认值null
	
	if(_$_XMLDocument.length != 0) {
		json = new Array();
	}
	
	_$_XMLDocument.each(function(i, o) {
		json[i] = new Object();
		
		var attrs = o.attributes;
		
		for(var j = 0; j < attrs.length; j++) {
			var name = attrs[j].name;
			json[i][name] = attrs[j].value;
			
			// 对布尔值的处理，从xml获得为string类型
			if(json[i][name] == 'true') {
				json[i][name] = true;
			}
			else if(json[i][name] == 'false') {
				json[i][name] = false;
			}
		}
		
		var nodes = _this.parse($(o).children()); // 递归转换下级节点xml对象
		
		json[i].nodes = nodes; // nodes属性赋值
	});
	
	return json;
}
