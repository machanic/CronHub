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
		error: function() {
			alert('jqTree Ajax load error');
		},
		success: function(data) {
			data = _this.xmlParse(data);
			_this.success(data);
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
	
	this.tree = null;                       // 节点所属树对象
	this.id = null;                         // 节点id
	this.name = null;                       // 节点name
	this.parentNode = null;                 // 父节点对象
	this.level = null;                      // 节点级数
	this.indent = 0;                        // 节点缩进距离，默认0
	this.createRequestParam = null;         // 构造请求参数的函数
	this.click = null;                      // 节点单击事件处理函数
	this.dblclick = null;                   // 节点双击事件处理函数
	this.subNodes = new Array();            // 子节点对象数组
	this.loaded = false;                    // 是否已加载子节点
	this.isParent = false;                  // 是否还有子节点，默认没有
	this.open = false,                      // 下级面板是否展开，默认在jqNavi中展开
	this.class_node = 'jqnavi-node';        // 节点node div样式
	this.class_name = 'jqnavi-name';        // 节点name span样式
	this.class_sub = 'jqnavi-sub';          // 下级节点sub div样式
	this.div_node = $('<div></div>');       // 节点node div
	this.span_name = $('<span></span>');    // 节点name span
	this.div_sub = $('<div></div>');        // 下级节点 sub div
	this.class_hover = 'jqnavi-node-hover'; // hover高亮效果样式
	
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
BaseNode.prototype.view = function() {
	var _this = this;
	
	this.div_node.addClass(this.class_node);
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
			
			_this.tree.container_vertical.find('.jqnavi-node-selected').removeClass('jqnavi-node-selected');
			_this.div_node.addClass('jqnavi-node-selected');
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
	this.clickBind();
	this.dblclickBind();
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
		
		node.view();
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
		
		node.view();
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
	this.class_node_horiz = 'jqnavi-node-horiz';   // 节点横向排列样式
	this.class_sub_horiz = 'jqnavi-sub-horiz';     // 下级节点横向排列sub div样式
	this.class_icon = 'jqnavi-icon';               // 图片div基本样式
	this.class_arrow = 'jqnavi-arrow';             // 箭头样式
	this.class_arrow_close = 'jqnavi-arrow-close'; // 箭头关闭样式
	this.class_icon_close = 'jqnavi-icon-close';   // 图标关闭样式
	this.class_arrow_open = 'jqnavi-arrow-open';   // 箭头打开样式
	this.class_icon_open = 'jqnavi-icon-open';     // 图标打开样式
	this.class_loading = 'jqnavi-loading';         // 加载中样式
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
		this.div_icon.css('background', 'url('+this.icon+') 0 7px no-repeat');
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
 * NaviNode
 */
function NaviNode(options) {
	BaseNode.call(this, options);
	
	this.class_node = 'jqnavi-node-horiz';        // 节点node div样式
	this.div_node = $('<div></div>');             // 节点node div
	this.class_hover = 'jqnavi-node-horiz-hover'; // hover高亮效果样式
	
	$.extend(this, options);
}
/**
 * extend from BaseNode
 */
NaviNode.prototype = new BaseNode();
/**
 * view()
 */
NaviNode.prototype.view = function() {
	var _this = this;
	
	this.div_node.addClass(this.class_node);
	this.div_sub.addClass(this.class_sub);
	
	this.div_node.append(this.name);
	
//	if(this.icon) {
//		this.div_node.css('background', 'url('+this.icon+') 13px 17px no-repeat');
//	}
	
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
NaviNode.prototype.clickBind = function() {
	var _this = this;
	
	this.div_node.click(function(event) {
		$.each(_this.tree.rootNodes, function(i, rootNode) {
			rootNode.collapse();
		});
		_this.expand();
		
		$.each(_this.tree.rootNodes, function(i, o) {
			o.div_node.removeClass('jqnavi-node-horiz-selected');
		});
		_this.div_node.addClass('jqnavi-node-horiz-selected');
		
		$('#td-left').show();
	});
}
/**
 * createSubNode(o)
 */
NaviNode.prototype.createSubNode = function(o) {
	return new IconNode(o);
}
/**
 * NaviTree
 */
function NaviTree(options) {
	BaseTree.call(this, options);
	
	this.container_horiz = null;
	this.container_vertical = null;
	
	$.extend(this, options);
}
/**
 * extend from BaseTree
 */
NaviTree.prototype = new BaseTree();
/**
 * init()
 */
NaviTree.prototype.init = function() {
	this.options.tree = this; // 树对象作为树节点参数对象的属性
	
	this.load();
}
/**
 * success(data)
 */
NaviTree.prototype.success = function(data) {
	var _this = this;
	
	$.each(data, function(i, o) {
		$.extend(o, _this.options, {
			parentNode: null,
			level: -1
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
		
		node.view();
		node.control();
		
		_this.container_horiz.append(node.div_node);
		_this.container_vertical.append(node.div_sub);
		
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
		
		if(i == 0) {
			node.div_node.click();
		}
	});
}
/**
 * createRootNode(o)
 */
NaviTree.prototype.createRootNode = function(o) {
	return new NaviNode(o);
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
