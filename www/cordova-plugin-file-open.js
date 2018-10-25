var exec = require('cordova/exec');


var fileOpen = {

    open: function(successCallback, errorCallback, params){
        exec(
            successCallback,
            errorCallback,
            'FileOpen',
            'open',
            [params]
        )
    }
}

module.exports = fileOpen;

