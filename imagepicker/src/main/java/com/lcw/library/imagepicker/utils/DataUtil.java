package com.lcw.library.imagepicker.utils;

import com.lcw.library.imagepicker.data.MediaFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据保存类
 * （随着Android版本的提高，对Intent传递数据的大小也做了不同的限制，在一些高版本或者低配机型上可能会发生
 * android.os.TransactionTooLargeException: data parcel size xxxx bytes异常，故用此方案适配）
 * Create by: chenWei.li
 * Date: 2019/1/24
 * Time: 12:38 AM
 * Email: lichenwei.me@foxmail.com
 */
public class DataUtil {

    private static volatile DataUtil mDataUtilInstance;
    private List<MediaFile> mData = new ArrayList<>();

    private DataUtil() {
    }

    public static DataUtil getInstance() {
        if (mDataUtilInstance == null) {
            synchronized (DataUtil.class) {
                if (mDataUtilInstance == null) {
                    mDataUtilInstance = new DataUtil();
                }
            }
        }
        return mDataUtilInstance;
    }

    public List<MediaFile> getMediaData() {
        return mData;
    }

    public void setMediaData(List<MediaFile> data) {
        this.mData = data;
    }


}
