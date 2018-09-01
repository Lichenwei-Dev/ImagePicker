package com.lcw.library.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.lcw.library.imagepicker.activity.ImagePickerActivity;
import com.lcw.library.imagepicker.utils.ImageLoader;

/**
 * 统一调用入口
 * Create by: chenWei.li
 * Date: 2018/8/26
 * Time: 下午6:31
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePicker {

    public static final String EXTRA_RESULT = "selectItems";

    private static volatile ImagePicker mImagePicker;

    private String mTitle;
    private boolean mShowCamera;
    private int mMaxCount;
    private boolean mSaveState;

    private ImageLoader mImageLoader;


    private ImagePicker() {
    }

    /**
     * 创建对象
     *
     * @return
     */
    public static ImagePicker getInstance() {
        if (mImagePicker == null) {
            synchronized (ImagePicker.class) {
                if (mImagePicker == null) {
                    mImagePicker = new ImagePicker();
                }
            }
        }
        return mImagePicker;
    }


    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public ImagePicker setTitle(String title) {
        this.mTitle = title;
        return mImagePicker;
    }

    /**
     * 是否支持相机
     *
     * @param showCamera
     * @return
     */
    public ImagePicker showCamera(boolean showCamera) {
        this.mShowCamera = showCamera;
        return mImagePicker;
    }


    /**
     * 图片最大选择数
     *
     * @param maxCount
     * @return
     */
    public ImagePicker setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
        return mImagePicker;
    }

    /**
     * 是否保存选中状态
     *
     * @param saveState
     * @return
     */
    public ImagePicker saveState(boolean saveState) {
        this.mSaveState = saveState;
        return mImagePicker;
    }

    /**
     * 设置图片加载器
     *
     * @param imageLoader
     * @return
     */
    public ImagePicker setImageLoader(ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
        return mImagePicker;
    }

    /**
     * 获取图片加载器
     *
     * @return
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * 启动
     *
     * @param activity
     */
    public void start(Activity activity, int requestCode) {
        if (checkPermission(activity)) {
            Intent intent = new Intent(activity, ImagePickerActivity.class);
            intent.putExtra(ImagePickerActivity.EXTRA_SHOW_CAMERA, mShowCamera);
            intent.putExtra(ImagePickerActivity.EXTRA_MAX_COUNT, mMaxCount);
            activity.startActivityForResult(intent, requestCode);
        } else {
            Toast.makeText(activity, "当前没有读取存储卡的权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 权限检查
     *
     * @param context
     * @return
     */
    private boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


}
