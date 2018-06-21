cordova.define("cordova-sharingreceptor.SharingReceptor", function(require, exports, module) {

var exec = require('cordova/exec');

module.exports = {
    listen: function(success, error) {
	return exec(success, error, 'SharingReceptor', 'listen', []);	
    }
};


});
