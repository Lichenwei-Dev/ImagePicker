package com.lcw.library.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lcw.library.imagepicker.data.ImageFile;

import java.util.List;

/**
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 上午12:57
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreViewAdapter extends PagerAdapter {

    private Context mContext;
    private List<ImageFile> mImageFileList;

    public ImagePreViewAdapter(Context context, List<ImageFile> imageFileList) {
        this.mContext = context;
        this.mImageFileList = imageFileList;
    }

    @Override
    public int getCount() {
        return mImageFileList == null ? 0 : mImageFileList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        return super.instantiateItem(container, position);
    }
}
