package com.lcw.library.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.manager.ConfigManager;

import java.util.List;

/**
 * 大图浏览适配器
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 上午12:57
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreViewAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mImagePaths;

    public ImagePreViewAdapter(Context context, List<String> imagePaths) {
        this.mContext = context;
        this.mImagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return mImagePaths == null ? 0 : mImagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //加载图片
        try {
            ConfigManager.getInstance().getImageLoader().loadPreImage(imageView, mImagePaths.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
