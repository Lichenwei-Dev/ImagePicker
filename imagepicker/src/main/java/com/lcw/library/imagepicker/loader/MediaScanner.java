package com.lcw.library.imagepicker.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.data.ImageFile;
import com.lcw.library.imagepicker.data.ImageFolder;
import com.lcw.library.imagepicker.listener.MediaLoadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 媒体库扫描类
 * Create by: chenWei.li
 * Date: 2018/8/21
 * Time: 上午1:01
 * Email: lichenwei.me@foxmail.com
 */
public class MediaScanner {

    public static final int ALL_IMAGES_FOLDER = -1;//全部图片

    /**
     * 查询媒体库所需的查询参数
     */
    public static final Uri SCAN_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String[] PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
    };
    private static final String SELECTION = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
    private static final String[] SELECTION_ARGS = new String[]{"image/jpeg", "image/png", "image/gif"};
    private static final String ORDER = MediaStore.Images.Media.DATE_TAKEN + " desc";


    private Context mContext;
    private ArrayList<ImageFile> mImageFileList;
    private MediaLoadCallback mMediaLoadCallback;


    public MediaScanner(Context context, MediaLoadCallback mediaLoadCallback) {
        this.mContext = context;
        this.mMediaLoadCallback = mediaLoadCallback;
        this.mImageFileList = new ArrayList<>();
    }

    /**
     * 扫描媒体库中的图片文件
     */
    @WorkerThread
    public void scanMedia() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(SCAN_IMAGE_URI, PROJECTION, SELECTION, SELECTION_ARGS, ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(0);
                String imageMime = cursor.getString(1);
                Integer imageFolderId = cursor.getInt(2);
                String imageFolderName = cursor.getString(3);
                long imageDateToken = cursor.getLong(4);

                ImageFile imageFile = new ImageFile();
                imageFile.setImagePath(imagePath);
                imageFile.setImageMime(imageMime);
                imageFile.setImageFolderId(imageFolderId);
                imageFile.setImageFolderName(imageFolderName);
                imageFile.setImageDateToken(imageDateToken);
                mImageFileList.add(imageFile);
            }
            cursor.close();

            if (mMediaLoadCallback != null) {
                List<ImageFolder> imageFolderList = getMediaFolder(mImageFileList);
                if (imageFolderList.size() > 0) {
                    mMediaLoadCallback.loadMediaSuccess(imageFolderList);
                } else {
                    mMediaLoadCallback.loadMediaFailed(mContext.getString(R.string.scanner_image_no_found));
                }
            }
        }
    }


    /**
     * 根据图片所在文件夹名称聚类图片
     *
     * @param imageFileList
     */
    @WorkerThread
    private List<ImageFolder> getMediaFolder(ArrayList<ImageFile> imageFileList) {
        //按图片所在文件夹Id进行聚类
        int size = imageFileList.size();
        Map<Integer, ImageFolder> mediaFolderMap = new HashMap<>();

        //添加全部图片的文件夹
        ImageFolder allImageFolder = new ImageFolder(ALL_IMAGES_FOLDER, mContext.getString(R.string.all_images), imageFileList.get(0).getImagePath(), imageFileList);
        mediaFolderMap.put(ALL_IMAGES_FOLDER, allImageFolder);

        //添加其他的图片文件夹
        for (int i = 0; i < size; i++) {
            int imageFolderId = imageFileList.get(i).getImageFolderId();
            ImageFolder imageFolder = mediaFolderMap.get(imageFolderId);
            if (imageFolder == null) {
                imageFolder = new ImageFolder(imageFolderId, imageFileList.get(i).getImageFolderName(), imageFileList.get(i).getImagePath(), new ArrayList<ImageFile>());
            }
            ArrayList<ImageFile> imageList = imageFolder.getImageFileList();
            imageList.add(imageFileList.get(i));
            imageFolder.setImageFileList(imageList);
            mediaFolderMap.put(imageFolderId, imageFolder);
        }

        //整理聚类数据
        List<ImageFolder> imageFolderList = new ArrayList<ImageFolder>();
        for (Integer folderId : mediaFolderMap.keySet()) {
            imageFolderList.add(mediaFolderMap.get(folderId));
        }

        return imageFolderList;
    }

}
