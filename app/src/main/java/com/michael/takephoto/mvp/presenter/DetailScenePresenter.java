package com.michael.takephoto.mvp.presenter;

import android.os.Handler;
import android.os.Looper;

import com.michael.takephoto.mvp.biz.DetailScene.OnRequestListener;
import com.michael.takephoto.mvp.biz.DetailScene.RequestBiz;
import com.michael.takephoto.mvp.biz.DetailScene.RequestBiziml;
import com.michael.takephoto.mvp.view.DetailSceneView;

import java.io.File;
import java.util.List;

/**
 * Created by aotuman on 2018/2/6.
 */

public class DetailScenePresenter {

    private List<File> mFiles;
    private DetailSceneView detailSceneView;
    private RequestBiz requestBiz;
    private Handler myHandler;
    private String filePath;

    public DetailScenePresenter(String filePath,DetailSceneView detailSceneView) {
        this.filePath = filePath;
        this.detailSceneView = detailSceneView;
        this.requestBiz = new RequestBiziml();
        this.myHandler = new Handler(Looper.getMainLooper());
    }

    public void initData() {
        detailSceneView.showLoading();
        requestBiz.requestForData(filePath,new OnRequestListener() {
            @Override
            public void onSuccess(final List<File> datas) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        detailSceneView.hideLoading();
                        detailSceneView.setDatas(datas);
                    }
                });
            }

            @Override
            public void onFailed() {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        detailSceneView.hideLoading();
                        detailSceneView.showMessage("请求失败！");
                    }
                });
            }
        });
    }

    public void onDestroy(){
        detailSceneView = null;
    }
}
