package com.lcw.library.imagepicker.task;

import android.content.Context;

import com.lcw.library.imagepicker.listener.MediaLoadCallback;
import com.lcw.library.imagepicker.loader.MediaScanner;

/**
 * 媒体库扫描任务
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 下午12:31
 * Email: lichenwei.me@foxmail.com
 */
public class MediaLoadTask implements Runnable {

    private MediaScanner mMediaScanner;

    public MediaLoadTask(Context context, MediaLoadCallback mediaLoadCallback) {
        mMediaScanner = new MediaScanner(context, mediaLoadCallback);
    }

    @Override
    public void run() {
        if (mMediaScanner != null) {
            mMediaScanner.scanMedia();
        }
    }

}
