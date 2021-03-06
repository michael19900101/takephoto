package com.michael.takephoto;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.michael.takephoto.fragment.DetailSceneFragment;
import com.michael.takephoto.fragment.ImageFragment;
import com.michael.takephoto.fragment.SceneFragment;

import java.io.File;


public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.show_detial, new SceneFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void gotoDeatilSceneFragment(String fileName) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DetailSceneFragment detailSceneFragment = DetailSceneFragment.newInstance(fileName);
        transaction.replace(R.id.show_detial, detailSceneFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void gotoImageFragment(File fileDir) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ImageFragment detailSceneFragment = ImageFragment.newInstance(fileDir);
        transaction.replace(R.id.show_detial, detailSceneFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();屏蔽掉系统的返回事件
        popBackStack();
    }

    public void popBackStack(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount()>0){
            fragmentManager.popBackStack();
        }else {
            finish();
        }
    }
}
