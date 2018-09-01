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
import com.lcw.library.imagepicker.manager.SelectionManager;
import com.lcw.library.imagepicker.view.SquareImageView;

import java.util.List;

/**
 * 图片列表适配器
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:18
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ViewHolder> {

    private Context mContext;
    private int mSelectionMode;
    private List<ImageFile> mImageFileList;


    public ImagePickerAdapter(Context context, List<ImageFile> imageFiles, int selectionMode) {
        this.mContext = context;
        this.mSelectionMode = selectionMode;
        this.mImageFileList = imageFiles;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_image, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        ImageFile imageFile = mImageFileList.get(position);
        Integer imageId = imageFile.getImageId();
        String imagePath = imageFile.getImagePath();

        //选择状态（仅是UI表现，真正数据交给SelectionManager管理）
        if (mSelectionMode == ImagePickerActivity.SELECT_MODE_MULTI) {
            //多选状态
            holder.mImageCheck.setVisibility(View.VISIBLE);
            if (SelectionManager.getInstance().isImageSelect(imageId)) {
                holder.mImageView.setColorFilter(Color.parseColor("#77000000"));
                holder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_checked));
            } else {
                holder.mImageView.setColorFilter(null);
                holder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_check));
            }
        } else {
            //单选状态
            holder.mImageCheck.setVisibility(View.GONE);
            holder.mImageView.setColorFilter(null);
            holder.mImageCheck.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_image_check));
        }

        //加载图片
        ImagePicker.getInstance().getImageLoader().loadImage(holder.mImageView, imagePath);

        //设置点击事件监听
        if (mOnItemClickListener != null) {
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onImageClick(view, position);
                }
            });

            holder.mImageCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onImageCheck(view, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mImageFileList == null ? 0 : mImageFileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private SquareImageView mImageView;
        private ImageView mImageCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_item_image);
            mImageCheck = itemView.findViewById(R.id.iv_item_check);
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
