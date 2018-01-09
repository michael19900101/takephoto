package com.michael.takephoto.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.michael.takephoto.BuildConfig;
import com.michael.takephoto.R;
import com.michael.takephoto.ShowActivity;
import com.michael.takephoto.adapter.DetailSceneAdapter;
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
public class DetailSceneFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private DetailSceneAdapter mAdapter;
    private Gson mGson;
    private ImageView mLoading;
    private TextView mLoadingText;
    private SharedPreferences mPreferences;
    private FloatingActionButton fabAddPhoto;
    private static final int PHOTO_TAKEN = 1111;       //拍照
    private static String IMAGE_PATH_TEMP = "";

    public static DetailSceneFragment newInstance(String fileName) {
        DetailSceneFragment fragment = new DetailSceneFragment();
        Bundle args = new Bundle();
        args.putString("param", fileName);
        IMAGE_PATH_TEMP = android.os.Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID + "/Scene/" + fileName;
        fragment.setArguments(args);
        return fragment;
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecyclerView.setAdapter(mAdapter = new DetailSceneAdapter(getContext(), mFiles));
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.setOnItemClickLitener(new DetailSceneAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            File file = mFiles.get(position);
                            Log.e("jbjb",file.getAbsolutePath());
                            ((ShowActivity) getActivity()).gotoImageFragment(file);
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


    public DetailSceneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_image, container, false);

        TextView title = (TextView) ret.findViewById(R.id.title);
        title.setText("场所明细");
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
        fabAddPhoto = ret.findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDetailScene();
            }
        });

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        initData();
        return ret;
    }

    private void addDetailScene(){
        final EditText et = new EditText(getContext());

        new AlertDialog.Builder(getContext()).setTitle("添加场所明细")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getContext(), "场所名称不能为空！" + input, Toast.LENGTH_SHORT).show();
                        }else {
                            String fileDir = IMAGE_PATH_TEMP + "/" + input;
                            if(FileUtils.isFileExists(fileDir)){
                                Toast.makeText(getContext(), "该场所已存在！", Toast.LENGTH_SHORT).show();
                            }else {
                                boolean result = FileUtils.createOrExistsDir(fileDir);
                                if(result){
                                    Toast.makeText(getContext(), "创建场所成功！", Toast.LENGTH_SHORT).show();
                                    initData();
                                }else {
                                    Toast.makeText(getContext(), "创建场所失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
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
                mFiles = FileUtils.listFilesInDir(IMAGE_PATH_TEMP,false);
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        }).start();
    }


}