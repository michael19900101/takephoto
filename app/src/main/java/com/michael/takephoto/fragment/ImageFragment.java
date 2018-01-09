package com.michael.takephoto.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.michael.takephoto.R;
import com.michael.takephoto.adapter.ImageAdapter;
import com.michael.takephoto.util.ACache;
import com.michael.takephoto.util.AppSharePreferenceMgr;
import com.michael.takephoto.util.SelectPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private ImageAdapter mAdapter;
    private Gson mGson;
    private ImageView mLoading;
    private TextView mLoadingText;
    private ACache mCatch;
    private SharedPreferences mPreferences;
    private FloatingActionButton fabAddPhoto;
    private static final int PHOTO_TAKEN = 1111;       //拍照
    private static String IMAGE_PATH_TEMP = "";
    private TextView tvNodata;
    private static String FILE_NAME = "";
    private static int MAX_PHOTOS = -1;

    public static ImageFragment newInstance(File fileDir) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("param", fileDir.getAbsolutePath());
        IMAGE_PATH_TEMP = fileDir.getAbsolutePath() + "/";
        FILE_NAME = fileDir.getName();
        if("无线环境照片".equals(FILE_NAME)){
            MAX_PHOTOS = 12;
        }
        fragment.setArguments(args);
        return fragment;
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecyclerView.setAdapter(mAdapter = new ImageAdapter(getContext(), mFiles,myHandler));
                    if(MAX_PHOTOS != -1 &&  mFiles.size() == MAX_PHOTOS){
                        fabAddPhoto.setVisibility(View.GONE);
                    }else {
                        fabAddPhoto.setVisibility(View.VISIBLE);
                    }
                    if(mFiles.size() == 0){
                        tvNodata.setVisibility(View.VISIBLE);
                    }else {
                        tvNodata.setVisibility(View.GONE);
                    }
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.setOnItemClickLitener(new ImageAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private String mPhotoPath;


    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_image, container, false);
        tvNodata = (TextView) ret.findViewById(R.id.tv_nodata);
        TextView title = (TextView) ret.findViewById(R.id.title);
        title.setText("照片");
        ImageView reicon = (ImageView)ret.findViewById(R.id.return_index);
        reicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        mLoading = (ImageView) ret.findViewById(R.id.loading_gif);
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mLoadingText = (TextView) ret.findViewById(R.id.loading_text);
        Glide.with(getContext()).load(R.drawable.loading)
                .asGif().into(mLoading);
        mFiles = new ArrayList<>();
        mGson = new Gson();
        mCatch = ACache.get(getContext());
        fabAddPhoto = ret.findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        initData();
        return ret;
    }

    private void startCamera() {
        try {
            mPhotoPath = getNewPhotoPath();
            File picFile = new File(mPhotoPath);
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

    private String getWireLessFileName(){
        String[] numArrays = getResources().getStringArray(R.array.wireless_num);
        List<String> nums = new ArrayList<>();
        for(String name:numArrays){
            nums.add(FILE_NAME + name);
        }
        List<String> fileNames = new ArrayList<>();
        List<String> diffList = new ArrayList<>();
        if(mFiles.size() == 0){
            return nums.get(0);
        }else {
            for(File file:mFiles){
                String fileName = FileUtils.getFileNameNoExtension(file);
                fileNames.add(fileName);
            }
            for(String item:nums){
                if(!fileNames.contains(item)){
                    diffList.add(item);
                }
            }
        }
        return diffList.get(0);
    }

    private String getNewPhotoPath(){
        String path = "";
        if("无线环境照片".equals(FILE_NAME)){
            String name = getWireLessFileName();
            path = IMAGE_PATH_TEMP + name + ".jpeg";
        }else {
            int maxNum = (int)AppSharePreferenceMgr.get(getContext(),FILE_NAME,0);
            File directory = new File(IMAGE_PATH_TEMP);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            maxNum += 1;
            AppSharePreferenceMgr.put(getContext(),FILE_NAME, maxNum);
            path = IMAGE_PATH_TEMP + FILE_NAME + maxNum + ".jpeg";
        }

        return path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拍照结果
        if (requestCode == PHOTO_TAKEN && resultCode != 0) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Bitmap bitmap = SelectPath.revitionImageSize(mPhotoPath);
                        saveBitmap(bitmap, mPhotoPath);
                        mFiles = FileUtils.listFilesInDirWithFilter(IMAGE_PATH_TEMP, ".jpeg");
                        Message message = Message.obtain();
                        message.what = 1;
                        myHandler.sendMessage(message);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "添加照片成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "添加照片失败！", Toast.LENGTH_SHORT).show();
                    }

                }
            }).start();
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

    private void initData() {
        //开线程初始化数据

        new Thread(new Runnable() {

            @Override
            public void run() {
//                judge();
                mFiles = FileUtils.listFilesInDirWithFilter(IMAGE_PATH_TEMP, ".jpeg");
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        }).start();
    }

}