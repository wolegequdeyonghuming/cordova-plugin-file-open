## a simple cordova-plugin-file-open plugin for android.


* How to use:
   ```
    fileOpen.open(
        function(successCallback){
            //successCallback, not really in use
        },
        function (errorCallback) {
            //errorCallback, usually be the exception
        },{
            filepath,
            mimetype
        }
    )
   ```

* filepath:
    > android file uri, e.g. 'file:///storage/emulated/0/test.txt'
     or be like
     '/storage/emulated/0/Android/data/package.name/app-debug.apk'

* mimetype:
    > your file's mimetype, not support filetype yet
