package com.lcw.library.imagepicker.data;

/**
 * 图片实体类
 * Create by: chenWei.li
 * Date: 2018/8/22
 * Time: 上午12:36
 * Email: lichenwei.me@foxmail.com
 */
public class ImageFile {

    private Integer imageId;
    private String imagePath;
    private String imageMime;
    private Integer imageFolderId;
    private String imageFolderName;
    private long imageDateToken;

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageMime() {
        return imageMime;
    }

    public void setImageMime(String imageMime) {
        this.imageMime = imageMime;
    }

    public Integer getImageFolderId() {
        return imageFolderId;
    }

    public void setImageFolderId(Integer imageFolderId) {
        this.imageFolderId = imageFolderId;
    }

    public String getImageFolderName() {
        return imageFolderName;
    }

    public void setImageFolderName(String imageFolderName) {
        this.imageFolderName = imageFolderName;
    }

    public long getImageDateToken() {
        return imageDateToken;
    }

    public void setImageDateToken(long imageDateToken) {
        this.imageDateToken = imageDateToken;
    }
}

