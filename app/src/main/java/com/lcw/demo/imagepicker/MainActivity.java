package com.lcw.demo.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lcw.library.imagepicker.ImagePicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;
    private ArrayList<String> mImagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv_select_images);
        findViewById(R.id.bt_select_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.getInstance().setImageLoader(new GlideLoader()).showCamera(true).setMaxCount(9).setImagePaths(mImagePaths).start(MainActivity.this, REQUEST_SELECT_IMAGES_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("当前选中图片路径：\n\n");
            for (int i = 0; i < mImagePaths.size(); i++) {
                stringBuffer.append(mImagePaths.get(i) + "\n\n");
            }
            mTextView.setText(stringBuffer.toString());
        }
    }
}
