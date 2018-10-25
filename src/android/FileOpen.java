package com.sucsoft.fileopen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class FileOpen extends CordovaPlugin {

	private static final String ACTION = "open";
	private static final String TAG = "FileOpen";
	private String filepath = "";
	private String mimetype = "";

	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		/* action */
		if(!action.equals(ACTION)){
			return false;
		}
		/* args*/
		JSONObject jsonObject = args.getJSONObject(0);

		if(jsonObject.has("filepath")){
			filepath = jsonObject.getString("filepath");
		}
		if(jsonObject.has("mimetype")){
			mimetype = jsonObject.getString("mimetype");
		}

		/* filepath */
		if(filepath.contains("file:///")){
            filepath = filepath.replace("file://", "");
        }

        /* permission */
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{"Manifest.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }

        /* exec */
		if("application/vnd.android.package-archive".equals(mimetype)){
		    try{
			    install(cordova.getContext(), callbackContext);
		    }catch(Exception e){
		        e.printStackTrace();
		        callbackContext.success(e.toString());
		    }
		}else{
			try{
                open(cordova.getContext(), callbackContext);
            }catch(Exception e){
                e.printStackTrace();
                callbackContext.success(e.toString());
            }
		}
		return true;
	}

	/**
     * 通过隐式意图调用系统安装程序安装APK
     */
    private void install(Context context, CallbackContext callbackContext) throws Exception {

        File file = new File(filepath);

        if(!file.exists()){
			callbackContext.error("file not fount");
			return;
		}

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(Build.VERSION.SDK_INT>=24) {
            Uri apkUri = FileProvider.getUriForFile(context, cordova.getActivity().getPackageName()+".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, mimetype);
			Log.d(TAG, "install: " + apkUri);
        }else{
            intent.setDataAndType(Uri.fromFile(file), mimetype);
			Log.d(TAG, "install: " + apkUri);
        }
        context.startActivity(intent);
		callbackContext.success("success");
    }

	private void open(Context context, CallbackContext callbackContext) throws Exception{

		File file = new File(filepath);

		if(!file.exists()){
			callbackContext.error("file not fount");
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri path = FileProvider.getUriForFile(context, cordova.getActivity().getPackageName() + ".fileprovider", file);
		intent.setDataAndType(path, mimetype);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

		Log.d(TAG, "open: " + path);

		cordova.getActivity().startActivity(Intent.createChooser(intent, "选择打开方式"));
		callbackContext.success("success");
	}
}
