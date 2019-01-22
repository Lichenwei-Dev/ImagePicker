package com.lcw.library.imagepicker.activity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import com.lcw.library.imagepicker.data.MediaFile;
import com.lcw.library.imagepicker.data.MediaFolder;
import com.lcw.library.imagepicker.executors.CommonExecutor;
import com.lcw.library.imagepicker.listener.MediaLoadCallback;
import com.lcw.library.imagepicker.manager.SelectionManager;
import com.lcw.library.imagepicker.task.ImageLoadTask;
import com.lcw.library.imagepicker.task.MediaLoadTask;
import com.lcw.library.imagepicker.task.VideoLoadTask;
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
public class ImagePickerActivity extends BaseActivity implements ImagePickerAdapter.OnItemClickListener, ImageFoldersAdapter.OnImageFolderChangeListener {

    /**
     * 启动参数
     */
    public static final int SELECT_MODE_SINGLE = 0;
    public static final int SELECT_MODE_MULTI = 1;
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_SHOW_CAMERA = "showCamera";
    public static final String EXTRA_SHOW_IMAGE = "showImage";
    public static final String EXTRA_SHOW_VIDEO = "showVideo";
    public static final String EXTRA_MAX_COUNT = "maxCount";
    public static final String EXTRA_IMAGE_PATHS = "imagePaths";
    private String mTitle;
    private boolean isShowCamera;
    private boolean isShowImage;
    private boolean isShowVideo;
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
    private List<MediaFile> mMediaFileList;
    //文件夹数据源
    private List<MediaFolder> mMediaFolderList;

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
    protected int bindLayout() {
        return R.layout.activity_imagepicker;
    }


    /**
     * 初始化配置
     */
    @Override
    protected void initConfig() {
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        isShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        isShowImage = getIntent().getBooleanExtra(EXTRA_SHOW_IMAGE, true);
        isShowVideo = getIntent().getBooleanExtra(EXTRA_SHOW_VIDEO, true);
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
    @Override
    protected void initView() {

        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.scanner_image));

        //顶部栏相关
        mTvTitle = findViewById(R.id.tv_actionBar_title);
        if (TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(getString(R.string.image_picker));
        } else {
            mTvTitle.setText(mTitle);
        }
        mTvCommit = findViewById(R.id.tv_actionBar_commit);

        //滑动悬浮标题相关
        mTvImageTime = findViewById(R.id.tv_image_time);

        //底部栏相关
        mRlBottom = findViewById(R.id.rl_main_bottom);
        mTvImageFolders = findViewById(R.id.tv_main_imageFolders);

        //列表相关
        mRecyclerView = findViewById(R.id.rv_main_images);
        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mMediaFileList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, mMediaFileList, isShowCamera, mSelectionMode);
        mImagePickerAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mImagePickerAdapter);

    }

    /**
     * 初始化控件监听事件
     */
    @Override
    protected void initListener() {

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
                commitSelection();
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
    @Override
    protected void getData() {

        Runnable mediaLoadTask = null;

        //照片、视频全部加载
        if (isShowImage && isShowVideo) {
            mediaLoadTask = new MediaLoadTask(this, new MediaLoader());
        }

        //只加载视频
        if (!isShowImage && isShowVideo) {
            mediaLoadTask = new VideoLoadTask(this, new MediaLoader());
        }

        //只加载图片
        if (isShowImage && !isShowVideo) {
            mediaLoadTask = new ImageLoadTask(this, new MediaLoader());
        }

        //不符合以上场景，采用照片、视频全部加载
        if (mediaLoadTask == null) {
            mediaLoadTask = new MediaLoadTask(this, new MediaLoader());
        }

        CommonExecutor.getInstance().execute(mediaLoadTask);
    }

    /**
     * 处理媒体数据加载成功后的UI渲染
     */
    class MediaLoader implements MediaLoadCallback {

        @Override
        public void loadMediaSuccess(final List<MediaFolder> mediaFolderList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //默认加载全部照片
                    mMediaFileList.addAll(mediaFolderList.get(0).getMediaFileList());
                    mImagePickerAdapter.notifyDataSetChanged();

                    //图片文件夹数据
                    mMediaFolderList = new ArrayList<>(mediaFolderList);
                    mImageFolderPopupWindow = new ImageFolderPopupWindow(ImagePickerActivity.this, mMediaFolderList);
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
        MediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
        if (mediaFile != null) {
            if (mTvImageTime.getVisibility() != View.VISIBLE) {
                mTvImageTime.setVisibility(View.VISIBLE);
            }
            String time = Utils.getImageTime(mediaFile.getDateToken());
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

    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;

    /**
     * 点击图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onImageClick(View view, int position) {
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

//        if (mMediaFileList != null) {
//            ArrayList<String> imagePathList = new ArrayList<>();
//            for (int i = 0; i < mMediaFileList.size(); i++) {
//                imagePathList.add(mMediaFileList.get(i).getImagePath());
//            }
//            Intent intent = new Intent(this, ImagePreActivity.class);
//            if (isShowCamera) {
//                intent.putExtra(ImagePreActivity.IMAGE_POSITION, position - 1);
//            } else {
//                intent.putExtra(ImagePreActivity.IMAGE_POSITION, position);
//            }
//            intent.putStringArrayListExtra(ImagePreActivity.IMAGE_LIST, imagePathList);
//            startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE);
//        }
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
        MediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
        if (mediaFile != null) {
            String imagePath = mediaFile.getPath();
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
        //拍照存放路径
        File fileDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        mFilePath = fileDir.getAbsolutePath() + "/IMG_" + System.currentTimeMillis() + ".jpg";

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
        MediaFolder mediaFolder = mMediaFolderList.get(position);
        //更新当前文件夹名
        String folderName = mediaFolder.getFolderName();
        if (!TextUtils.isEmpty(folderName)) {
            mTvImageFolders.setText(folderName);
        }
        //更新图片列表数据源
        mMediaFileList.clear();
        mMediaFileList.addAll(mediaFolder.getMediaFileList());
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

                List<String> list = new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
                Intent intent = new Intent();
                intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, (ArrayList<String>) list);
                setResult(RESULT_OK, intent);
                finish();
            }

            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                commitSelection();
            }
        }
    }

    /**
     * 选择图片完毕，返回
     */
    private void commitSelection() {
        ArrayList<String> list = new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mImagePickerAdapter.notifyDataSetChanged();
        updateCommitButton();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}
