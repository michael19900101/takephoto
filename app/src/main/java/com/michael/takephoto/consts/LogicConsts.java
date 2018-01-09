package com.michael.takephoto.consts;

import com.michael.takephoto.BuildConfig;

import java.io.File;

/**
 * Created by Administrator on 2018/1/7.
 */

public class LogicConsts {

    /**
     * 应用文件存储地址
     */
    public static final String PATH = android.os.Environment.getExternalStorageDirectory().getPath()
            + File.separator + BuildConfig.APPLICATION_ID + File.separator;
}
