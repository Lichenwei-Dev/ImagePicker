package com.lcw.library.imagepicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.activity.ImagePickerActivity;
import com.lcw.library.imagepicker.data.ImageFile;
import com.lcw.library.imagepicker.data.ItemType;
import com.lcw.library.imagepicker.manager.SelectionManager;
import com.lcw.library.imagepicker.view.SquareFrameLayout;
import com.lcw.library.imagepicker.view.SquareImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片列表适配器
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:18
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mSelectionMode;
    private List<ImageFile> mImageFileList;
    private boolean isShowCamera;


    public ImagePickerAdapter(Context context, List<ImageFile> imageFiles, boolean isShowCamera, int selectionMode) {
        this.mContext = context;
        this.mSelectionMode = selectionMode;
        this.isShowCamera = isShowCamera;
        this.mImageFileList = imageFiles;
    }


    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return ItemType.ITEM_TYPE_CAMERA;
            }
        }
        return ItemType.ITEM_TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
        if (mImageFileList == null) {
            return 0;
        }
        return isShowCamera ? mImageFileList.size() + 1 : mImageFileList.size();
    }

    /**
     * 获取item所对应的数据源
     *
     * @param position
     * @return
     */
    public ImageFile getImageFile(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return null;
            }
            return mImageFileList.get(position - 1);
        }
        return mImageFileList.get(position);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ItemType.ITEM_TYPE_CAMERA) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_camera, null);
            return new CameraHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_image, null);
            return new ImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        int itemType = getItemViewType(position);

        if (itemType == ItemType.ITEM_TYPE_CAMERA) {
            CameraHolder cameraHolder = (CameraHolder) holder;
            if (mOnItemClickListener != null) {
                cameraHolder.mSquareFrameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onImageClick(view, position);
                    }
                });
            }
        } else {
            ImageHolder imageHolder = (ImageHolder) holder;
            ImageFile imageFile = getImageFile(position);
            Integer imageId = imageFile.getImageId();
            String imagePath = imageFile.getImagePath();

            //选择状态（仅是UI表现，真正数据交给SelectionManager管理）
            if (mSelectionMode == ImagePickerActivity.SELECT_MODE_MULTI) {
                //多选状态
                imageHolder.mImageCheck.setVisibility(View.VISIBLE);
                if (SelectionManager.getInstance().isImageSelect(imageId)) {
                    imageHolder.mImageView.setColorFilter(Color.parseColor("#77000000"));
                    imageHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_checked));
                } else {
                    imageHolder.mImageView.setColorFilter(null);
                    imageHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_check));
                }
            } else {
                //单选状态
                imageHolder.mImageCheck.setVisibility(View.GONE);
                imageHolder.mImageView.setColorFilter(null);
                imageHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_check));
            }

            //加载图片
            ImagePicker.getInstance().getImageLoader().loadImage(imageHolder.mImageView, imagePath);

            //设置点击事件监听
            if (mOnItemClickListener != null) {
                imageHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onImageClick(view, position);
                    }
                });

                imageHolder.mImageCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onImageClick(view, position);
                    }
                });
            }
        }
    }

    /**
     * 图片Item
     */
    class ImageHolder extends RecyclerView.ViewHolder {

        private SquareImageView mImageView;
        private ImageView mImageCheck;

        public ImageHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_item_image);
            mImageCheck = itemView.findViewById(R.id.iv_item_check);
        }
    }

    /**
     * 相机Item
     */
    class CameraHolder extends RecyclerView.ViewHolder {

        private SquareFrameLayout mSquareFrameLayout;

        public CameraHolder(View itemView) {
            super(itemView);
            mSquareFrameLayout = itemView.findViewById(R.id.sfl_item_camera);
        }
    }


    /**
     * 接口回调，将点击事件向外抛
     */
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onImageClick(View view, int position);

        void onImageCheck(View view, int position);
    }
}
