var Params = function(opt_params_or_query) {
	if (typeof opt_params_or_query === 'string') {
		this.decode_(opt_params_or_query);
		return;
	}
	var params = opt_params_or_query || {};
	this.params = {};
	var self = this;
	_.each(params, function(value, key) {
		self.params[key] = value;
	});
};

Params.prototype.set = function(param, value) {
	this.params[param] = value;
};

Params.prototype.get = function(param, opt_defaultValue) {
	return this.params[param] || opt_defaultValue;
};

Params.prototype.clear = function() {
	this.params = {};
};

Params.prototype.encode = function() {
	return $.param(this.params);
};

Params.prototype.decode_ = function(query) {
	var params = {};
	var vars = query.split('&');
	for (var i = 0; i < vars.length; i++) {
	  var pair = vars[i].split('=');
	  pair[0] = decodeURIComponent(pair[0].replace(/\+/g, '%20'));
	  pair[1] = decodeURIComponent(pair[1].replace(/\+/g, '%20'));
	  	// If first entry with this name
	  if (typeof params[pair[0]] === 'undefined') {
	    params[pair[0]] = pair[1];
	  	// If second entry with this name
	  } else if (typeof params[pair[0]] === 'string') {
	    var arr = [params[pair[0]], pair[1]];
	    params[pair[0]] = arr;
	  	// If third or later entry with this name
	  } else {
	    params[pair[0]].push(pair[1]);
	  }
	}
	this.params = params;
};
