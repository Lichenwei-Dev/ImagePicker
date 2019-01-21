package com.lcw.library.imagepicker.task;

import android.content.Context;

import com.lcw.library.imagepicker.listener.MediaLoadCallback;
import com.lcw.library.imagepicker.loader.ImageScanner;
import com.lcw.library.imagepicker.loader.VideoScanner;

/**
 * 媒体库扫描任务
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 下午12:31
 * Email: lichenwei.me@foxmail.com
 */
public class MediaLoadTask implements Runnable {

    private ImageScanner mImageScanner;
//    private VideoScanner mVideoScanner;

    public MediaLoadTask(Context context, MediaLoadCallback mediaLoadCallback) {
        mImageScanner = new ImageScanner(context, mediaLoadCallback);
//        mVideoScanner = new VideoScanner(context, mediaLoadCallback);
    }

    @Override
    public void run() {
        if (mImageScanner != null) {
            mImageScanner.execute();
        }
//        if (mVideoScanner != null) {
//            mVideoScanner.execute();
//        }
    }

}
