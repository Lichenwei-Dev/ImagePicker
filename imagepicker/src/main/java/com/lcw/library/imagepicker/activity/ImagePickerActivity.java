package com.lcw.library.imagepicker.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.adapter.ImageFoldersAdapter;
import com.lcw.library.imagepicker.adapter.ImagePickerAdapter;
import com.lcw.library.imagepicker.data.ImageFile;
import com.lcw.library.imagepicker.data.ImageFolder;
import com.lcw.library.imagepicker.executors.CommonExecutor;
import com.lcw.library.imagepicker.listener.MediaLoadCallback;
import com.lcw.library.imagepicker.task.MediaLoadTask;
import com.lcw.library.imagepicker.manager.SelectionManager;
import com.lcw.library.imagepicker.view.ImageFolderPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * 多图选择器主页面
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:10
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePickerActivity extends AppCompatActivity implements ImagePickerAdapter.OnItemClickListener, ImageFoldersAdapter.OnImageFolderChangeListener {

    /**
     * 启动参数
     */
    public static final int SELECT_MODE_SINGLE = 0;
    public static final int SELECT_MODE_MULTI = 1;
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_SHOW_CAMERA = "showCamera";
    public static final String EXTRA_MAX_COUNT = "maxCount";
    public static final String EXTRA_SAVE_STATE = "saveState";
    private String mTitle;
    private boolean mShowCamera;
    private int mMaxCount;
    private boolean mSaveState;
    private int mSelectionMode;

    /**
     * 界面UI
     */
    private TextView mTvTitle;
    private TextView mTvCommit;
    private RecyclerView mRecyclerView;
    private TextView mTvImageFolders;
    private ImageFolderPopupWindow mImageFolderPopupWindow;
    private ProgressDialog mProgressDialog;
    private RelativeLayout mRlBottom;

    private ImagePickerAdapter mImagePickerAdapter;

    //图片列表/文件夹数据源
    private List<ImageFile> mImageFileList;
    private List<ImageFolder> mImageFolderList;

    //表示屏幕亮暗
    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_ON = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepicker);

        initConfig();
        initView();
        initListener();
        getData();
    }


    /**
     * 初始化配置
     */
    private void initConfig() {
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        mMaxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, 1);
        mSaveState = getIntent().getBooleanExtra(EXTRA_SAVE_STATE, false);
        if (!mSaveState) {
            SelectionManager.getInstance().removeAll();
        }
        SelectionManager.getInstance().setMaxCount(mMaxCount);
        if (mMaxCount > 1) {
            mSelectionMode = SELECT_MODE_MULTI;
        } else {
            mSelectionMode = SELECT_MODE_SINGLE;
        }
    }

    /**
     * 初始化布局控件
     */
    private void initView() {

        mProgressDialog = ProgressDialog.show(this, null, "正在扫描图片..");

        mTvTitle = findViewById(R.id.tv_actionBar_title);
        if (!TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(mTitle);
        }
        mTvCommit = findViewById(R.id.tv_actionBar_commit);
        mRecyclerView = findViewById(R.id.rv_main_images);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mImageFileList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, mImageFileList, mSelectionMode);
        mImagePickerAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mImagePickerAdapter);
        mRlBottom = findViewById(R.id.rl_main_bottom);
        mTvImageFolders = findViewById(R.id.tv_main_imageFolders);


    }

    /**
     * 初始化控件事件
     */
    private void initListener() {

        findViewById(R.id.iv_actionBar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
                Intent intent = new Intent();
                intent.putStringArrayListExtra(ImagePicker.EXTRA_RESULT, list);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mTvImageFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageFolderPopupWindow != null) {
                    setLightMode(LIGHT_OFF);
                    mImageFolderPopupWindow.showAsDropDown(mRlBottom, 0, 0);
                }
            }
        });
    }

    /**
     * 获取数据源
     */
    private void getData() {
        MediaLoadTask mediaLoadTask = new MediaLoadTask(this, new MediaLoadCallback() {
            @Override
            public void loadMediaSuccess(final List<ImageFolder> imageFolderList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //默认加载全部照片
                        mImageFileList.addAll(imageFolderList.get(0).getImageFileList());
                        mImagePickerAdapter.notifyDataSetChanged();

                        //图片文件夹数据
                        mImageFolderList = new ArrayList<>(imageFolderList);
                        mImageFolderPopupWindow = new ImageFolderPopupWindow(ImagePickerActivity.this, mImageFolderList);
                        mImageFolderPopupWindow.setAnimationStyle(R.style.imageFolderAnimator);
                        mImageFolderPopupWindow.getAdapter().setOnImageFolderChangeListener(ImagePickerActivity.this);
                        mImageFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                setLightMode(LIGHT_ON);
                            }
                        });

                        mProgressDialog.cancel();
                    }
                });
            }

            @Override
            public void loadMediaFailed(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ImagePickerActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        CommonExecutor.getInstance().execute(mediaLoadTask);
    }

    /**
     * 设置屏幕的亮度模式
     *
     * @param lightMode
     */
    private void setLightMode(int lightMode) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        switch (lightMode) {
            case LIGHT_OFF:
                layoutParams.alpha = 0.7f;
                break;
            case LIGHT_ON:
                layoutParams.alpha = 1.0f;
                break;
        }
        getWindow().setAttributes(layoutParams);
    }


    /**
     * 点击图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onImageClick(View view, int position) {
        // TODO: 2018/8/25 进行大图预览
        onImageCheck(view, position);
    }

    /**
     * 选中/取消选中图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onImageCheck(View view, int position) {
        //执行选中/取消操作
        ImageFile imageFile = mImageFileList.get(position);
        if (imageFile != null) {
            boolean addSuccess = SelectionManager.getInstance().addImageToSelectList(imageFile);
            if (addSuccess) {
                mImagePickerAdapter.notifyItemChanged(position);
            } else {
                Toast.makeText(this, String.format("最多选择%d张图片", mMaxCount), Toast.LENGTH_SHORT).show();
            }
        }
        //改变确定按钮UI
        int selectCount = SelectionManager.getInstance().getSelectIds().size();
        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText("确定");
            return;
        }
        if (selectCount < mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format("确定（%d/%d）", selectCount, mMaxCount));
            return;
        }
        if (selectCount == mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format("确定（%d/%d）", selectCount, mMaxCount));
            return;
        }
    }

    /**
     * 当图片文件夹切换时，刷新图片列表数据源
     *
     * @param view
     * @param position
     */
    @Override
    public void onImageFolderChange(View view, int position) {
        ImageFolder imageFolder = mImageFolderList.get(position);
        //更新当前文件夹名
        String folderName = imageFolder.getFolderName();
        if (!TextUtils.isEmpty(folderName)) {
            mTvImageFolders.setText(folderName);
        }
        //更新图片列表数据源
        mImageFileList.clear();
        mImageFileList.addAll(imageFolder.getImageFileList());
        mImagePickerAdapter.notifyDataSetChanged();

        mImageFolderPopupWindow.dismiss();
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}
