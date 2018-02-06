package com.michael.takephoto.mvp.view;

import java.io.File;
import java.util.List;

/**
 * Created by aotuman on 2018/2/6.
 */

public interface DetailSceneView {
    void showLoading();

    void hideLoading();

    void setDatas(List<File> files);

    void showMessage(String message);
}
