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

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.data.ItemType;
import com.lcw.library.imagepicker.data.MediaFile;
import com.lcw.library.imagepicker.manager.ConfigManager;
import com.lcw.library.imagepicker.manager.SelectionManager;
import com.lcw.library.imagepicker.utils.Utils;
import com.lcw.library.imagepicker.view.SquareImageView;
import com.lcw.library.imagepicker.view.SquareRelativeLayout;

import java.util.List;

/**
 * 列表适配器
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:18
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.BaseHolder> {

    private Context mContext;
    private List<MediaFile> mMediaFileList;
    private boolean isShowCamera;


    public ImagePickerAdapter(Context context, List<MediaFile> mediaFiles) {
        this.mContext = context;
        this.mMediaFileList = mediaFiles;
        this.isShowCamera = ConfigManager.getInstance().isShowCamera();
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
    public MediaFile getMediaFile(int position) {
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
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ItemType.ITEM_TYPE_CAMERA) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_camera, null);
            return new BaseHolder(view);
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
    public void onBindViewHolder(@NonNull BaseHolder holder, final int position) {
        int itemType = getItemViewType(position);
        MediaFile mediaFile = getMediaFile(position);
        switch (itemType) {
            //图片、视频Item
            case ItemType.ITEM_TYPE_IMAGE:
            case ItemType.ITEM_TYPE_VIDEO:
                MediaHolder mediaHolder = (MediaHolder) holder;
                bindMedia(mediaHolder, mediaFile);
                break;
            //相机Item
            default:
                break;
        }
        //设置点击事件监听
        if (mOnItemClickListener != null) {
            holder.mSquareRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onMediaClick(view, position);
                }
            });

            if (holder instanceof MediaHolder) {
                ((MediaHolder) holder).mImageCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onMediaCheck(view, position);
                    }
                });
            }
        }
    }


    /**
     * 绑定数据（图片、视频）
     *
     * @param mediaHolder
     * @param mediaFile
     */
    private void bindMedia(MediaHolder mediaHolder, MediaFile mediaFile) {

        String imagePath = mediaFile.getPath();
        //选择状态（仅是UI表现，真正数据交给SelectionManager管理）
        if (SelectionManager.getInstance().isImageSelect(imagePath)) {
            mediaHolder.mImageView.setColorFilter(Color.parseColor("#77000000"));
            mediaHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_checked));
        } else {
            mediaHolder.mImageView.setColorFilter(null);
            mediaHolder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_check));
        }

        try {
            ConfigManager.getInstance().getImageLoader().loadImage(mediaHolder.mImageView, imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mediaHolder instanceof ImageHolder) {
            //如果是gif图，显示gif标识
            String suffix = imagePath.substring(imagePath.lastIndexOf(".") + 1);
            if (suffix.toUpperCase().equals("GIF")) {
                ((ImageHolder) mediaHolder).mImageGif.setVisibility(View.VISIBLE);
            } else {
                ((ImageHolder) mediaHolder).mImageGif.setVisibility(View.GONE);
            }
        }

        if (mediaHolder instanceof VideoHolder) {
            //如果是视频，需要显示视频时长
            String duration = Utils.getVideoDuration(mediaFile.getDuration());
            ((VideoHolder) mediaHolder).mVideoDuration.setText(duration);
        }

    }

    /**
     * 图片Item
     */
    class ImageHolder extends MediaHolder {

        public ImageView mImageGif;

        public ImageHolder(View itemView) {
            super(itemView);
            mImageGif = itemView.findViewById(R.id.iv_item_gif);
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
    class MediaHolder extends BaseHolder {

        public SquareImageView mImageView;
        public ImageView mImageCheck;

        public MediaHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_item_image);
            mImageCheck = itemView.findViewById(R.id.iv_item_check);
        }
    }

    /**
     * 基础Item
     */
    class BaseHolder extends RecyclerView.ViewHolder {

        public SquareRelativeLayout mSquareRelativeLayout;

        public BaseHolder(View itemView) {
            super(itemView);
            mSquareRelativeLayout = itemView.findViewById(R.id.srl_item);
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
        void onMediaClick(View view, int position);

        void onMediaCheck(View view, int position);
    }
}
