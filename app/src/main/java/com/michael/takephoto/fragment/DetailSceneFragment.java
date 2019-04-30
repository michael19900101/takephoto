package com.michael.takephoto.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.michael.takephoto.mvp.presenter.DetailScenePresenter;
import com.michael.takephoto.mvp.view.DetailSceneView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailSceneFragment extends Fragment implements DetailSceneView{
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
    private DetailScenePresenter detailScenePresenter;

    public static DetailSceneFragment newInstance(String fileName) {
        DetailSceneFragment fragment = new DetailSceneFragment();
        Bundle args = new Bundle();
        args.putString("param", fileName);
        IMAGE_PATH_TEMP = android.os.Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID + "/Scene/" + fileName;
        fragment.setArguments(args);
        return fragment;
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
        detailScenePresenter = new DetailScenePresenter(IMAGE_PATH_TEMP,this);
        return ret;
    }

    @Override
    public void onResume() {
        super.onResume();
        detailScenePresenter.initData();
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
                                    detailScenePresenter.initData();
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
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        mLoadingText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mLoading.setVisibility(View.INVISIBLE);
        mLoadingText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDatas(final List<File> files) {
        mRecyclerView.setAdapter(mAdapter = new DetailSceneAdapter(getContext(), files));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setOnItemClickLitener(new DetailSceneAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                File file = files.get(position);
                ((ShowActivity) getActivity()).gotoImageFragment(file);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    @Override
    public void showMessage(String message) {

    }
}