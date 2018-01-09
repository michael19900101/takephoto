package com.michael.takephoto;

import android.app.Application;

import com.blankj.utilcode.utils.FileUtils;
import com.michael.takephoto.util.AppSharePreferenceMgr;

/**
 * Created by Administrator on 2018/1/9.
 */

public class MyApplication extends Application{
    private String IMAGE_PATH_TEMP = android.os.Environment.getExternalStorageDirectory().getPath()
            + "/" + BuildConfig.APPLICATION_ID + "/Scene/";

    @Override
    public void onCreate() {
        super.onCreate();
        boolean firstLaunch = (boolean) AppSharePreferenceMgr.get(this,"firstLaunch",true);
        if(firstLaunch){
            FileUtils.deleteDir(android.os.Environment.getExternalStorageDirectory().getPath()
                    + "/" + BuildConfig.APPLICATION_ID );
            AppSharePreferenceMgr.put(this,"firstLaunch", false);
        }
        FileUtils.createOrExistsDir(IMAGE_PATH_TEMP);
    }
}
