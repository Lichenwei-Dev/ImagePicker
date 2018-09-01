package com.lcw.library.imagepicker.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 屏幕相关工具类
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 上午1:53
 * Email: lichenwei.me@foxmail.com
 */
public class Utils {

    /**
     * 获取屏幕的宽和高
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

}
