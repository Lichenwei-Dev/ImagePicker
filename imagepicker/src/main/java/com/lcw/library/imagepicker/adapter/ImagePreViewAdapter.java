package com.lcw.library.imagepicker.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.data.MediaFile;
import com.lcw.library.imagepicker.manager.ConfigManager;
import com.lcw.library.imagepicker.provider.ImagePickerProvider;

import java.io.File;
import java.util.List;

/**
 * 大图浏览适配器（并不是比较好的方案，后期会用RecyclerView来实现）
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 上午12:57
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreViewAdapter extends PagerAdapter {

    private Context mContext;
    private List<MediaFile> mMediaFileList;

    public ImagePreViewAdapter(Context context, List<MediaFile> mediaFileList) {
        this.mContext = context;
        this.mMediaFileList = mediaFileList;
    }

    @Override
    public int getCount() {
        return mMediaFileList == null ? 0 : mMediaFileList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final MediaFile mediaFile = mMediaFileList.get(position);
        long duration = mediaFile.getDuration();
        final String path = mediaFile.getPath();
        final View view;
        final ImageView imageView;
        if (duration > 0) {
            //视频
            view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager_video, null);
            ImageView playView = view.findViewById(R.id.iv_item_play);
            playView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //实现播放视频的跳转逻辑(调用原生视频播放器)
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(mContext, ImagePickerProvider.getFileProviderName(mContext), new File(path));
                    intent.setDataAndType(uri, "video/*");
                    //给所有符合跳转条件的应用授权
                    List<ResolveInfo> resInfoList = mContext.getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        mContext.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    mContext.startActivity(intent);
                }
            });
        } else {
            //图片
            view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager_image, null);
        }
        imageView = view.findViewById(R.id.iv_item_image);
        try {
            ConfigManager.getInstance().getImageLoader().loadPreImage(imageView, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
