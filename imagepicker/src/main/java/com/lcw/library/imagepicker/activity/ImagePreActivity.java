package com.lcw.library.imagepicker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.adapter.ImagePreViewAdapter;
import com.lcw.library.imagepicker.view.HackyViewPager;

import java.util.List;

/**
 * 大图预览界面
 * Create by: chenWei.li
 * Date: 2018/10/3
 * Time: 下午11:32
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreActivity extends AppCompatActivity {

    public static final String IMAGE_LIST = "imageList";
    public static final String IMAGE_POSITION = "imagePosition";
    private List<String> mImagePaths;
    private int mPosition = 0;

    private TextView mTvTitle;
    private TextView mTvCommit;
    private HackyViewPager mViewPager;
    private ImagePreViewAdapter mImagePreViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_image);

        initView();
        initListener();
        getData();

    }


    private void initView() {
        mTvTitle = findViewById(R.id.tv_actionBar_title);
        mTvCommit = findViewById(R.id.tv_actionBar_commit);
        mViewPager = findViewById(R.id.vp_main_preImage);

        mTvCommit.setVisibility(View.GONE);
    }


    private void initListener() {

        findViewById(R.id.iv_actionBar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(String.format("%d/%d", position + 1, mImagePaths.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void getData() {
        mImagePaths = getIntent().getStringArrayListExtra(IMAGE_LIST);
        mPosition = getIntent().getIntExtra(IMAGE_POSITION, 0);
        mImagePreViewAdapter = new ImagePreViewAdapter(this, mImagePaths);
        mViewPager.setAdapter(mImagePreViewAdapter);
        mViewPager.setCurrentItem(mPosition);
        mTvTitle.setText(String.format("%d/%d", mPosition + 1, mImagePaths.size()));

    }

}
