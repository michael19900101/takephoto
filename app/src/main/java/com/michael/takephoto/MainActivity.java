package com.michael.takephoto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.michael.takephoto.util.SelectPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private GridView noScrollgridview;
    private SelectPicAdapter adapter;
    private String mPhotoPath;
    private File mPhotoFile;
    private UUID uid;
    public static final int PHOTO_TAKEN = 1111;       //拍照
    private String IMAGE_PATH_TEMP = android.os.Environment.getExternalStorageDirectory().getPath()
            + "/" + BuildConfig.APPLICATION_ID + "/Image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new SelectPicAdapter(this, SelectPath.select_list, noScrollgridview);
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("ShowToast")
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (SelectPath.select_list.size() == arg2) {
                    startCamera();
                } else {
                    // 预览
                    Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                    intent.putExtra("photoUri", SelectPath.select_path_list.get(arg2)); // 使用原图进行预览
                    intent.putExtra("isAlbumMode", false);
                    startActivity(intent);
                }
            }
        });
    }

    private void startCamera() {
        try {
            File directory = new File(IMAGE_PATH_TEMP);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File picFile;
            mPhotoPath = IMAGE_PATH_TEMP + getUUID() + ".jpeg";
            picFile = new File(mPhotoPath);
            if (!picFile.exists()) {
                picFile.createNewFile();
            }
            Intent cameraIntent = new Intent();
            cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE); // 默认系统相机
            cameraIntent.addCategory("android.intent.category.DEFAULT");
            Uri pictureUri = Uri.fromFile(picFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            startActivityForResult(cameraIntent, PHOTO_TAKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUUID() {
        uid = UUID.randomUUID();
        return uid.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拍照结果
        if (requestCode == PHOTO_TAKEN && resultCode != 0) {
            try {
                Bitmap bitmap = SelectPath.revitionImageSize(mPhotoPath);
                saveBitmap(bitmap, mPhotoPath);
                SelectPath.select_list.add(mPhotoPath);
                SelectPath.select_path_list.add(mPhotoPath);
                SelectPath.file_list.add(uid.toString());
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存下压缩的图片
     *
     *
     */
    public void saveBitmap(Bitmap bitmap, String path) {
        File mImgFile = new File(path);
        if (mImgFile != null && mImgFile.exists()) {
            mImgFile.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(mImgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
