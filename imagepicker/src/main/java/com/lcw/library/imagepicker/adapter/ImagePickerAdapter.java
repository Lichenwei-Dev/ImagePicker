package com.lcw.library.imagepicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.activity.ImagePickerActivity;
import com.lcw.library.imagepicker.data.ItemType;
import com.lcw.library.imagepicker.data.MediaFile;
import com.lcw.library.imagepicker.manager.SelectionManager;
import com.lcw.library.imagepicker.utils.Utils;
import com.lcw.library.imagepicker.view.SquareFrameLayout;
import com.lcw.library.imagepicker.view.SquareImageView;

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
    private List<MediaFile> mMediaFileList;
    private boolean isShowCamera;


    public ImagePickerAdapter(Context context, List<MediaFile> mediaFiles, boolean isShowCamera, int selectionMode) {
        this.mContext = context;
        this.mSelectionMode = selectionMode;
        this.isShowCamera = isShowCamera;
        this.mMediaFileList = mediaFiles;
    }


    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return ItemType.ITEM_TYPE_CAMERA;
            }
            //如果有相机存在，position位置需要-1
            position--;
        }
        if (mMediaFileList.get(position).getDuration() > 0) {
            return ItemType.ITEM_TYPE_VIDEO;
        } else {
            return ItemType.ITEM_TYPE_IMAGE;
        }
    }

    @Override
    public int getItemCount() {
        if (mMediaFileList == null) {
            return 0;
        }
        return isShowCamera ? mMediaFileList.size() + 1 : mMediaFileList.size();
    }

    /**
     * 获取item所对应的数据源
     *
     * @param position
     * @return
     */
    public MediaFile getImageFile(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return null;
            }
            return mMediaFileList.get(position - 1);
        }
        return mMediaFileList.get(position);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ItemType.ITEM_TYPE_CAMERA) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_camera, null);
            return new CameraHolder(view);
        }
        if (viewType == ItemType.ITEM_TYPE_IMAGE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_image, null);
            return new ImageHolder(view);
        }
        if (viewType == ItemType.ITEM_TYPE_VIDEO) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_video, null);
            return new VideoHolder(view);
        }
        return null;
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

            MediaFile mediaFile = getImageFile(position);
            String imagePath = mediaFile.getPath();

            MediaHolder mediaHolder = (MediaHolder) holder;

            //选择状态（仅是UI表现，真正数据交给SelectionManager管理）
            if (mSelectionMode == ImagePickerActivity.SELECT_MODE_MULTI) {
                //多选状态
                mediaHolder.mImageCheck.setVisibility(View.VISIBLE);
                if (SelectionManager.getInstance().isImageSelect(imagePath)) {
                    mediaHolder.mImageView.setColorFilter(Color.parseColor("#77000000"));
                    mediaHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_checked));
                } else {
                    mediaHolder.mImageView.setColorFilter(null);
                    mediaHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_check));
                }
            } else {
                //单选状态
                mediaHolder.mImageCheck.setVisibility(View.GONE);
                mediaHolder.mImageView.setColorFilter(null);
                mediaHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_check));
            }

            try {
                ImagePicker.getInstance().getImageLoader().loadImage(mediaHolder.mImageView, imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //设置点击事件监听
            if (mOnItemClickListener != null) {
                mediaHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onImageClick(view, position);
                    }
                });

                mediaHolder.mImageCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onImageCheck(view, position);
                    }
                });
            }

            if (itemType == ItemType.ITEM_TYPE_VIDEO) {
                String duration = Utils.getVideoDuration(mediaFile.getDuration());
                ((VideoHolder) mediaHolder).mVideoDuration.setText(duration);

            }

        }


    }

    /**
     * 图片Item
     */
    class ImageHolder extends MediaHolder {

        public ImageHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 视频Item
     */
    class VideoHolder extends MediaHolder {
        private TextView mVideoDuration;

        public VideoHolder(View itemView) {
            super(itemView);
            mVideoDuration = itemView.findViewById(R.id.tv_item_videoDuration);
        }
    }

    /**
     * 媒体Item
     */
    class MediaHolder extends RecyclerView.ViewHolder {

        public SquareImageView mImageView;
        public ImageView mImageCheck;

        public MediaHolder(View itemView) {
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
