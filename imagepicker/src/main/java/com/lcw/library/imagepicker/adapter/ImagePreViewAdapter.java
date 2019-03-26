package com.lcw.library.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.data.MediaFile;
import com.lcw.library.imagepicker.manager.ConfigManager;
import com.lcw.library.imagepicker.view.PinchImageView;

import java.util.List;

/**
 * 大图浏览适配器（并不是比较好的方案，后期会用RecyclerView来实现）
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 上午12:57
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreViewAdapter extends PagerAdapter {

    private Context mContext;
    private List<MediaFile> mMediaFileList;

    public ImagePreViewAdapter(Context context, List<MediaFile> mediaFileList) {
        this.mContext = context;
        this.mMediaFileList = mediaFileList;
    }

    @Override
    public int getCount() {
        return mMediaFileList == null ? 0 : mMediaFileList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager_image, null);
        PinchImageView imageView = view.findViewById(R.id.iv_item_image);
        try {
            ConfigManager.getInstance().getImageLoader().loadPreImage(imageView, mMediaFileList.get(position).getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
