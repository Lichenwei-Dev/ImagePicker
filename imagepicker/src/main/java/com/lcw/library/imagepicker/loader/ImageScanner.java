package com.lcw.library.imagepicker.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.data.MediaFile;
import com.lcw.library.imagepicker.data.MediaFolder;
import com.lcw.library.imagepicker.listener.MediaLoadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 媒体库扫描类(图片)
 * Create by: chenWei.li
 * Date: 2018/8/21
 * Time: 上午1:01
 * Email: lichenwei.me@foxmail.com
 */
public class ImageScanner extends AbsMediaScanner<MediaFile> {

    public static final int ALL_IMAGES_FOLDER = -1;//全部图片

    private Context mContext;
    private MediaLoadCallback mMediaLoadCallback;

    public ImageScanner(Context context, MediaLoadCallback mediaLoadCallback) {
        super(context);
        this.mContext = context;
        this.mMediaLoadCallback = mediaLoadCallback;
    }

    /**
     * 根据图片所在文件夹名称聚类图片
     *
     * @param mediaFileList
     */
    @WorkerThread
    private List<MediaFolder> getMediaFolder(ArrayList<MediaFile> mediaFileList) {
        //按图片所在文件夹Id进行聚类
        int size = mediaFileList.size();
        Map<Integer, MediaFolder> mediaFolderMap = new HashMap<>();

        //添加全部图片的文件夹
        MediaFolder allMediaFolder = new MediaFolder(ALL_IMAGES_FOLDER, mContext.getString(R.string.all_images), mediaFileList.get(0).getPath(), mediaFileList);
        mediaFolderMap.put(ALL_IMAGES_FOLDER, allMediaFolder);

        //添加其他的图片文件夹
        for (int i = 0; i < size; i++) {
            MediaFile mediaFile = mediaFileList.get(i);
            int imageFolderId = mediaFile.getFolderId();
            MediaFolder mediaFolder = mediaFolderMap.get(imageFolderId);
            if (mediaFolder == null) {
                mediaFolder = new MediaFolder(imageFolderId, mediaFile.getFolderName(), mediaFile.getPath(), new ArrayList<MediaFile>());
            }
            ArrayList<MediaFile> imageList = mediaFolder.getMediaFileList();
            imageList.add(mediaFile);
            mediaFolder.setMediaFileList(imageList);
            mediaFolderMap.put(imageFolderId, mediaFolder);
        }

        //整理聚类数据
        List<MediaFolder> mediaFolderList = new ArrayList<>();
        for (Integer folderId : mediaFolderMap.keySet()) {
            mediaFolderList.add(mediaFolderMap.get(folderId));
        }

        return mediaFolderList;
    }

    @Override
    protected Uri getScanUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };
    }

    @Override
    protected String getSelection() {
        return MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?" + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
    }

    @Override
    protected String[] getSelectionArgs() {
        return new String[]{"image/jpeg", "image/png", "image/gif"};
    }

    @Override
    protected String getOrder() {
        return MediaStore.Images.Media.DATE_TAKEN + " desc";
    }

    /**
     * 构建媒体对象
     *
     * @param cursor
     * @return
     */
    @Override
    protected MediaFile parse(Cursor cursor) {

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        Integer folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        long dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

        MediaFile mediaFile = new MediaFile();
        mediaFile.setPath(path);
        mediaFile.setMime(mime);
        mediaFile.setFolderId(folderId);
        mediaFile.setFolderName(folderName);
        mediaFile.setDateToken(dateToken);

        return mediaFile;
    }

    /**
     * 媒体库查询结果
     *
     * @param list
     */
    @Override
    protected void allocate(ArrayList<MediaFile> list) {
        mMediaLoadCallback.loadMediaSuccess(getMediaFolder(list));
    }
}
