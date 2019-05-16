package com.lcw.library.imagepicker.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    /**
     * 获取图片格式化时间
     *
     * @param timestamp
     * @return
     */
    public static String getImageTime(long timestamp) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());
        Calendar imageCalendar = Calendar.getInstance();
        imageCalendar.setTimeInMillis(timestamp);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) == imageCalendar.get(Calendar.DAY_OF_YEAR) && currentCalendar.get(Calendar.YEAR) == imageCalendar.get(Calendar.YEAR)) {
            return "今天";
        } else if (currentCalendar.get(Calendar.WEEK_OF_YEAR) == imageCalendar.get(Calendar.WEEK_OF_YEAR) && currentCalendar.get(Calendar.YEAR) == imageCalendar.get(Calendar.YEAR)) {
            return "本周";
        } else if (currentCalendar.get(Calendar.MONTH) == imageCalendar.get(Calendar.MONTH) && currentCalendar.get(Calendar.YEAR) == imageCalendar.get(Calendar.YEAR)) {
            return "本月";
        } else {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
            return sdf.format(date);
        }
    }

    /**
     * 获取视频时长（格式化）
     *
     * @param timestamp
     * @return
     */
    public static String getVideoDuration(long timestamp) {
        if (timestamp < 1000) {
            return "00:01";
        }
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(date);
    }


}
