package com.lcw.library.imagepicker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcw.library.imagepicker.R;

/**
 * Create by: chenWei.li
 * Date: 2018/8/29
 * Time: 下午10:43
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreViewActivity extends AppCompatActivity {

    /**
     * 界面UI
     */
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvCommit;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_actionBar_back);
        mTvTitle = findViewById(R.id.tv_actionBar_title);
        mTvCommit = findViewById(R.id.tv_actionBar_commit);
        mViewPager = findViewById(R.id.vp_image_detail);
    }
}
