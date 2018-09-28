package com.lcw.library.imagepicker.activity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
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
import com.lcw.library.imagepicker.manager.SelectionManager;
import com.lcw.library.imagepicker.task.MediaLoadTask;
import com.lcw.library.imagepicker.utils.Utils;
import com.lcw.library.imagepicker.view.ImageFolderPopupWindow;

import java.io.File;
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
    public static final String EXTRA_IMAGE_PATHS = "imagePaths";
    private String mTitle;
    private boolean isShowCamera;
    private int mMaxCount;
    private int mSelectionMode;
    private List<String> mImagePaths;

    /**
     * 界面UI
     */
    private TextView mTvTitle;
    private TextView mTvCommit;
    private TextView mTvImageTime;
    private RecyclerView mRecyclerView;
    private TextView mTvImageFolders;
    private ImageFolderPopupWindow mImageFolderPopupWindow;
    private ProgressDialog mProgressDialog;
    private RelativeLayout mRlBottom;

    private GridLayoutManager mGridLayoutManager;
    private ImagePickerAdapter mImagePickerAdapter;

    //图片数据源
    private List<ImageFile> mImageFileList;
    //文件夹数据源
    private List<ImageFolder> mImageFolderList;

    //是否显示时间
    private boolean isShowTime;

    //表示屏幕亮暗
    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_ON = 1;

    private Handler mMyHandler = new Handler();
    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideImageTime();
        }
    };

    /**
     * 拍照相关
     */
    private String mFilePath;
    private static final int REQUEST_CODE_CAPTURE = 1000;

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
        isShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        mMaxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, 1);
        SelectionManager.getInstance().setMaxCount(mMaxCount);
        if (mMaxCount > 1) {
            mSelectionMode = SELECT_MODE_MULTI;
        } else {
            mSelectionMode = SELECT_MODE_SINGLE;
        }
        mImagePaths = getIntent().getStringArrayListExtra(EXTRA_IMAGE_PATHS);
        if (mImagePaths == null) {
            SelectionManager.getInstance().removeAll();
        } else {
            SelectionManager.getInstance().addImagePathsToSelectList(mImagePaths);
        }
    }

    /**
     * 初始化布局控件
     */
    private void initView() {

        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.scanner_image));

        mTvTitle = findViewById(R.id.tv_actionBar_title);
        if (!TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(mTitle);
        }
        mTvCommit = findViewById(R.id.tv_actionBar_commit);
        mTvImageTime = findViewById(R.id.tv_image_time);
        mRecyclerView = findViewById(R.id.rv_main_images);
        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mImageFileList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, mImageFileList, isShowCamera, mSelectionMode);
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
                intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
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

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateImageTime();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateImageTime();
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
                        updateCommitButton();
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
     * 隐藏时间
     */
    private void hideImageTime() {
        if (isShowTime) {
            isShowTime = false;
            ObjectAnimator.ofFloat(mTvImageTime, "alpha", 1, 0).setDuration(300).start();
        }
    }

    /**
     * 显示时间
     */
    private void showImageTime() {
        if (!isShowTime) {
            isShowTime = true;
            ObjectAnimator.ofFloat(mTvImageTime, "alpha", 0, 1).setDuration(300).start();
        }
    }

    /**
     * 更新时间
     */
    private void updateImageTime() {
        int position = mGridLayoutManager.findFirstVisibleItemPosition();
        ImageFile imageFile = mImagePickerAdapter.getImageFile(position);
        if (imageFile != null) {
            if (mTvImageTime.getVisibility() != View.VISIBLE) {
                mTvImageTime.setVisibility(View.VISIBLE);
            }
            String time = Utils.getImageTime(imageFile.getImageDateToken());
            mTvImageTime.setText(time);
            showImageTime();
            mMyHandler.removeCallbacks(mHideRunnable);
            mMyHandler.postDelayed(mHideRunnable, 1500);
        }

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
        if (isShowCamera) {
            if (position == 0) {
                int selectCount = SelectionManager.getInstance().getSelectPaths().size();
                if (selectCount == mMaxCount) {
                    Toast.makeText(this, String.format(getString(R.string.select_image_max), mMaxCount), Toast.LENGTH_SHORT).show();
                    return;
                }
                showCamera();
                return;
            }
        }
        //执行选中/取消操作
        ImageFile imageFile = mImagePickerAdapter.getImageFile(position);
        if (imageFile != null) {
            String imagePath = imageFile.getImagePath();
            boolean addSuccess = SelectionManager.getInstance().addImageToSelectList(imagePath);
            if (addSuccess) {
                mImagePickerAdapter.notifyItemChanged(position);
            } else {
                Toast.makeText(this, String.format(getString(R.string.select_image_max), mMaxCount), Toast.LENGTH_SHORT).show();
            }
        }

        updateCommitButton();
    }

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {
        //改变确定按钮UI
        int selectCount = SelectionManager.getInstance().getSelectPaths().size();
        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText(getString(R.string.confirm));
            return;
        }
        if (selectCount < mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, mMaxCount));
            return;
        }
        if (selectCount == mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, mMaxCount));
            return;
        }
    }

    /**
     * 跳转相机拍照
     */
    private void showCamera() {
        mFilePath = Environment.getExternalStorageDirectory().getPath() + "/IMG_" + System.currentTimeMillis() + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, "com.lcw.library.imagepicker.provider", new File(mFilePath));
        } else {
            uri = Uri.fromFile(new File(mFilePath));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
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

    /**
     * 拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE) {
                //通知媒体库刷新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mFilePath)));
                //添加到选中集合
                SelectionManager.getInstance().addImageToSelectList(mFilePath);

                List<String> list =  new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
                Intent intent = new Intent();
                intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, (ArrayList<String>) list);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}
