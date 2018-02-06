package com.michael.takephoto.mvp.biz.DetailScene;

import com.blankj.utilcode.utils.FileUtils;
import com.michael.takephoto.threadpool.ThreadTaskObject;

import java.io.File;
import java.util.List;

/**
 * Created by aotuman on 2018/2/6.
 */
public class RequestBiziml implements RequestBiz{

    @Override
    public void requestForData(final String filePath,final OnRequestListener listener) {

        //开线程初始化数据
        new ThreadTaskObject() {
            @Override
            public void run() {
                try {
                    List<File> mFiles = FileUtils.listFilesInDir(filePath,false);
                    if(null != listener){
                        listener.onSuccess(mFiles);
                    }
                } catch (Exception e) {
                    if(null != listener){
                        listener.onFailed();
                    }
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
