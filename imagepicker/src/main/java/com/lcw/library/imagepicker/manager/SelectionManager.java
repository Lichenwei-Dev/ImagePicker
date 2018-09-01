package com.lcw.library.imagepicker.manager;

import com.lcw.library.imagepicker.data.ImageFile;

import java.util.HashSet;
import java.util.Set;

/**
 * 媒体选择集合管理类
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:19
 * Email: lichenwei.me@foxmail.com
 */
public class SelectionManager {

    private static volatile SelectionManager mSelectionManager;

    private Set<Integer> mSelectImageIds = new HashSet<>();
    private Set<String> mSelectImagePaths = new HashSet<>();

    private int mMaxCount = 1;

    private SelectionManager() {
    }

    public static SelectionManager getInstance() {
        if (mSelectionManager == null) {
            synchronized (SelectionManager.class) {
                if (mSelectionManager == null) {
                    mSelectionManager = new SelectionManager();
                }
            }
        }
        return mSelectionManager;
    }

    /**
     * 设置最大选择数
     *
     * @param maxCount
     */
    public void setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
    }

    /**
     * 获取当前所选图片集合id
     *
     * @return
     */
    public Set<Integer> getSelectIds() {
        return mSelectImageIds;
    }

    /**
     * 获取当前所选图片集合path
     *
     * @return
     */
    public Set<String> getSelectPaths() {
        return mSelectImagePaths;
    }

    /**
     * 添加/移除图片到选择集合
     *
     * @param imageFile
     * @return
     */
    public boolean addImageToSelectList(ImageFile imageFile) {
        int imageId = imageFile.getImageId();
        String imagePath = imageFile.getImagePath();
        if (mSelectImageIds.contains(imageId)) {
            mSelectImagePaths.remove(imagePath);
            return mSelectImageIds.remove(imageId);
        } else {
            if (mSelectImageIds.size() < mMaxCount) {
                mSelectImagePaths.add(imagePath);
                return mSelectImageIds.add(imageId);
            } else {
                return false;
            }
        }
    }


    /**
     * 判断当前图片是否被选择
     *
     * @param imageId
     * @return
     */
    public boolean isImageSelect(Integer imageId) {
        if (mSelectImageIds.contains(imageId)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 清除已选图片
     */
    public void removeAll() {
        mSelectImagePaths.clear();
        mSelectImageIds.clear();
    }

}
