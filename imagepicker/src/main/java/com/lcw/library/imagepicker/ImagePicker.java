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

import java.util.ArrayList;

/**
 * 统一调用入口
 * Create by: chenWei.li
 * Date: 2018/8/26
 * Time: 下午6:31
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePicker {

    public static final String EXTRA_SELECT_IMAGES = "selectItems";

    private static volatile ImagePicker mImagePicker;

    private String mTitle;//标题
    private boolean mShowCamera;//是否显示拍照Item，默认不显示
    private boolean mShowImage = true;//是否显示图片，默认显示
    private boolean mShowVideo = true;//是否显示视频，默认显示
    private int mMaxCount;//最大选择数量
    private ArrayList<String> mImagePaths;//上一次选择的图片地址集合

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
     * 是否展示图片
     *
     * @param showImage
     * @return
     */
    public ImagePicker showImage(boolean showImage) {
        this.mShowImage = showImage;
        return mImagePicker;
    }

    /**
     * 是否展示视频
     *
     * @param showVideo
     * @return
     */
    public ImagePicker showVideo(boolean showVideo) {
        this.mShowVideo = showVideo;
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
     * 设置图片加载器
     *
     * @param imageLoader
     * @return
     */
    public ImagePicker setImageLoader(ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
        return mImagePicker;
    }


    public ImagePicker setImagePaths(ArrayList<String> imagePaths) {
        this.mImagePaths = imagePaths;
        return mImagePicker;
    }


    /**
     * 获取图片加载器
     *
     * @return
     */
    public ImageLoader getImageLoader() throws Exception {
        if (mImageLoader == null) {
            throw new Exception("imageLoader is null");
        }
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
            intent.putExtra(ImagePickerActivity.EXTRA_TITLE, mTitle);
            intent.putExtra(ImagePickerActivity.EXTRA_SHOW_CAMERA, mShowCamera);
            intent.putExtra(ImagePickerActivity.EXTRA_MAX_COUNT, mMaxCount);
            intent.putExtra(ImagePickerActivity.EXTRA_IMAGE_PATHS, mImagePaths);
            intent.putExtra(ImagePickerActivity.EXTRA_SHOW_IMAGE, mShowImage);
            intent.putExtra(ImagePickerActivity.EXTRA_SHOW_VIDEO, mShowVideo);
            activity.startActivityForResult(intent, requestCode);
        } else {
            Toast.makeText(activity, activity.getString(R.string.permission_tip), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 权限检查
     *
     * @param context
     * @return
     */
    private boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }


}
