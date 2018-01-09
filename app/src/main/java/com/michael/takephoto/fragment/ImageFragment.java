package com.michael.takephoto.fragment;


import android.content.Context;
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
import com.michael.takephoto.BuildConfig;
import com.michael.takephoto.R;
import com.michael.takephoto.adapter.ImageAdapter;
import com.michael.takephoto.util.ACache;
import com.michael.takephoto.util.SelectPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    public static ImageFragment newInstance(String fileAbsolutePath) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("param", fileAbsolutePath);
        IMAGE_PATH_TEMP = fileAbsolutePath + "/";
        fragment.setArguments(args);
        return fragment;
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecyclerView.setAdapter(mAdapter = new ImageAdapter(getContext(), mFiles));
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
            File directory = new File(IMAGE_PATH_TEMP);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File picFile;
            mPhotoPath = IMAGE_PATH_TEMP + UUID.randomUUID() + ".jpeg";
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

    /**
     * 判断缓存是否存在，初始化数据
     */
    private void judge() {
        try {
            mPreferences = getContext().getSharedPreferences("table", Context.MODE_PRIVATE);
        } catch (Exception e) {
            //子线程未销毁可能时执行
        }
        boolean first = mPreferences.getBoolean("firstImage", true);
        int num = mPreferences.getInt("numImage", 0);

        long time = mPreferences.getLong("ImageTime", 0);
        long cha = System.currentTimeMillis() - time;
        //判断缓存时间是否过期

        if (!first && time != 0 & cha < 86400000) {
            for (int i = 0; i < num; i++) {
                String s = String.valueOf(i);
                String string = mCatch.getAsString(s);
                if (string!=null) {
                    File file = mGson.fromJson(string, File.class);
                    mFiles.add(file);
                }

            }
        } else {

            mFiles = FileUtils.listFilesInDirWithFilter(IMAGE_PATH_TEMP, ".jpg");
            addCatch();
        }
    }

    /**
     * 添加缓存
     */
    public void addCatch() {

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < mFiles.size(); i++) {
            String s = mGson.toJson(mFiles.get(i));
            strings.add(s);
        }
        for (int i = 0; i < strings.size(); i++) {
            String s = String.valueOf(i);
            mCatch.put(s, strings.get(i), ACache.TIME_DAY);
        }


        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("firstImage", false);
        edit.putInt("numImage", strings.size());
        edit.putLong("ImageTime", System.currentTimeMillis());
        edit.commit();
    }

}