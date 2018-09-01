package com.lcw.demo.imagepicker;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lcw.library.imagepicker.utils.ImageLoader;

/**
 * 实现自定义图片加载
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 下午11:10
 * Email: lichenwei.me@foxmail.com
 */
public class GlideLoader implements ImageLoader {

    @Override
    public void loadImage(ImageView imageView, String imagePah) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.icon_image_default)
                .error(R.mipmap.icon_image_error);
        Glide.with(imageView.getContext()).load(imagePah).apply(options).into(imageView);
    }

    @Override
    public void loadPreImage(ImageView imageView, String imagePath) {

    }

    @Override
    public void clearMemoryCache() {

    }
}
