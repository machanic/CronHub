/**
 * Tab
 * @param options
 * @return
 */
function Tab(options) {
	this.selectedIndex = 0;
	
	this.tabs = null;
	this.panels = null;
	
	$.extend(this, options);
	
	this.init();
}
/**
 * init()
 * @return
 */
Tab.prototype.init = function() {
	var _this = this;
	
	this.container.addClass('tab-container');
	
	$('> div', this.container).hide();
	
	this.tabs = $('> div:first', this.container);
	this.tabs.show();
	this.tabs.addClass('tab');
	
	this.panels = $('> div:hidden', this.container);
	
	$('a', this.tabs).each(function(i, a) {
		$(this).click(function() {
			$('a', _this.tabs).removeClass('tab-selected');
			$(this).addClass('tab-selected');
			
			_this.panels.hide();
			_this.panels.eq(i).show();
		});
	});
	
	//$('a:first', this.tabs).click();
	$('a', this.tabs).eq(this.selectedIndex).click();
}

/**
 * AjaxTab
 * @param options
 * @return
 */
function AjaxTab(options) {
	this.selectedIndex = 0;
	
	this.container = null;
	this.items = null;
	
	this.tabs = $('<div class="tab"></div>');
	this.panel = $('<div class="tab-panel"></div>');
	
	$.extend(this, options);
	
	this.init();
}
/**
 * init()
 * @return
 */
AjaxTab.prototype.init = function() {
	var _this = this;
	
	this.container.addClass('tab-container');
	this.container.append(this.tabs);
	this.container.append(this.panel);
	
	$.each(this.items, function(i, item) {
		var a = $('<a></a>');
		_this.tabs.append(a);
		_this.tabs.append('<span></span>');
		if(this.icon) {
			var img = $('<img src="'+this.icon+'" />');
			a.append(img);
			a.append(this.text);
		}
		a.click(function() {
			$('a', _this.tabs).removeClass('tab-selected');
			$(this).addClass('tab-selected');
			
			_this.panel.html('<div class="loading"></div>');
			_this.panel.load(item.loadUrl);
		});
	});
	
	//$('a:first', this.tabs).click();
	$('a', this.tabs).eq(this.selectedIndex).click();
}
