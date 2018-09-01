package com.lcw.library.imagepicker.utils;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * 开放图片加载接口
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 下午11:07
 * Email: lichenwei.me@foxmail.com
 */
public interface ImageLoader extends Serializable {

    void loadImage(ImageView imageView, String imagePath);

    void loadPreImage(ImageView imageView, String imagePath);

    void clearMemoryCache();

}
