/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michael.takephoto.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.utils.FileUtils;
import com.michael.takephoto.R;

public class CameraActivity extends AppCompatActivity {

    private String FILE_NAME;
    private String IMAGE_PATH_TEMP;
    private String SCENE_FILE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            FILE_NAME = intent.getStringExtra("FILE_NAME");
            IMAGE_PATH_TEMP = intent.getStringExtra("IMAGE_PATH_TEMP");
            SCENE_FILE_NAME = intent.getStringExtra("SCENE_FILE_NAME");
        }

        setContentView(R.layout.activity_camera);
        FileUtils.createOrExistsDir(IMAGE_PATH_TEMP);
        if (null == savedInstanceState) {
            Camera2BasicFragment fragment = Camera2BasicFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("FILE_NAME", FILE_NAME);
            bundle.putString("IMAGE_PATH_TEMP", IMAGE_PATH_TEMP);
            bundle.putString("SCENE_FILE_NAME", SCENE_FILE_NAME);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }
}
