package com.lcw.library.imagepicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Create by: chenWei.li
 * Date: 2019/2/3
 * Time: 1:01 AM
 * Email: lichenwei.me@foxmail.com
 */
public class PermissionUtil {

    /**
     * 权限检查
     *
     * @param context
     * @return
     */
    public static boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
