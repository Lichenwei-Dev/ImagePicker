package com.lcw.library.imagepicker.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * 媒体库查询任务基类
 * Create by: chenWei.li
 * Date: 2019/1/21
 * Time: 8:35 PM
 * Email: lichenwei.me@foxmail.com
 */
public abstract class AbsMediaScanner<T> {

    /**
     * 查询URI
     *
     * @return
     */
    protected abstract Uri getScanUri();

    /**
     * 查询列名
     *
     * @return
     */
    protected abstract String[] getProjection();

    /**
     * 查询条件
     *
     * @return
     */
    protected abstract String getSelection();

    /**
     * 查询条件值
     *
     * @return
     */
    protected abstract String[] getSelectionArgs();

    /**
     * 查询排序
     *
     * @return
     */
    protected abstract String getOrder();

    /**
     * 对外暴露游标，让开发者灵活构建对象
     *
     * @param cursor
     * @return
     */
    protected abstract T parse(Cursor cursor);

    private Context mContext;

    public AbsMediaScanner(Context context) {
        this.mContext = context;
    }

    /**
     * 根据查询条件进行媒体库查询，隐藏查询细节，让开发者更专注业务
     *
     * @return
     */
    public ArrayList<T> queryMedia() {
        ArrayList<T> list = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(getScanUri(), getProjection(), getSelection(), getSelectionArgs(), getOrder());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                T t = parse(cursor);
                list.add(t);
            }
            cursor.close();
        }
        return list;
    }

}
