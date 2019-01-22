package com.lcw.library.imagepicker.listener;

import com.lcw.library.imagepicker.data.MediaFolder;

import java.util.List;

/**
 * 图片扫描数据回调接口
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 下午9:55
 * Email: lichenwei.me@foxmail.com
 */
public interface MediaLoadCallback {

    void loadMediaSuccess(List<MediaFolder> mediaFolderList);
}
