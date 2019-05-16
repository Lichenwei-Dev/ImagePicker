package com.lcw.library.imagepicker.manager;

import com.lcw.library.imagepicker.utils.MediaFileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体选择集合管理类
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:19
 * Email: lichenwei.me@foxmail.com
 */
public class SelectionManager {

    private static volatile SelectionManager mSelectionManager;

    private ArrayList<String> mSelectImagePaths = new ArrayList<>();

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
     * 获取当前设置最大选择数
     *
     * @return
     */
    public int getMaxCount() {
        return this.mMaxCount;
    }

    /**
     * 获取当前所选图片集合path
     *
     * @return
     */
    public ArrayList<String> getSelectPaths() {
        return mSelectImagePaths;
    }

    /**
     * 添加/移除图片到选择集合
     *
     * @param imagePath
     * @return
     */
    public boolean addImageToSelectList(String imagePath) {
        if (mSelectImagePaths.contains(imagePath)) {
            return mSelectImagePaths.remove(imagePath);
        } else {
            if (mSelectImagePaths.size() < mMaxCount) {
                return mSelectImagePaths.add(imagePath);
            } else {
                return false;
            }
        }
    }

    /**
     * 添加图片到选择集合
     *
     * @param imagePaths
     */
    public void addImagePathsToSelectList(List<String> imagePaths) {
        if (imagePaths != null) {
            for (int i = 0; i < imagePaths.size(); i++) {
                String imagePath = imagePaths.get(i);
                if (!mSelectImagePaths.contains(imagePath) && mSelectImagePaths.size() < mMaxCount) {
                    mSelectImagePaths.add(imagePath);
                }
            }
        }
    }


    /**
     * 判断当前图片是否被选择
     *
     * @param imagePath
     * @return
     */
    public boolean isImageSelect(String imagePath) {
        if (mSelectImagePaths.contains(imagePath)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否还可以继续选择图片
     *
     * @return
     */
    public boolean isCanChoose() {
        if (getSelectPaths().size() < mMaxCount) {
            return true;
        }
        return false;
    }

    /**
     * 是否可以添加到选择集合（在singleType模式下，图片视频不能一起选）
     *
     * @param currentPath
     * @param filePath
     * @return
     */
    public static boolean isCanAddSelectionPaths(String currentPath, String filePath) {
        if ((MediaFileUtil.isVideoFileType(currentPath) && !MediaFileUtil.isVideoFileType(filePath)) || (!MediaFileUtil.isVideoFileType(currentPath) && MediaFileUtil.isVideoFileType(filePath))) {
            return false;
        }
        return true;
    }

    /**
     * 清除已选图片
     */
    public void removeAll() {
        mSelectImagePaths.clear();
    }

}
