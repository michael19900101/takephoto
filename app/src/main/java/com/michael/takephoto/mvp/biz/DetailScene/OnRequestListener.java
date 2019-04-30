package com.michael.takephoto.mvp.biz.DetailScene;

import java.io.File;
import java.util.List;

/**
 * Created by aotuman on 2018/2/6.
 */
public interface OnRequestListener {

    void onSuccess(List<File> datas);

    void onFailed();
}
