# 高仿微信图片选择器

#### 注意开发细节，尽可能的做到加载速度最快，目前支持图片单选，多选，多文件夹切换，自定义图片加载器等功能。

#### 效果图：
![效果图1](https://upload-images.jianshu.io/upload_images/2189443-52833f62e70ca1f6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/640)
![效果图2](https://upload-images.jianshu.io/upload_images/2189443-655e1c356e0f955e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/640)
![效果图3](https://upload-images.jianshu.io/upload_images/2189443-cb4dad72c803017f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/640)
![效果图4](https://upload-images.jianshu.io/upload_images/2189443-1da4a1e21fab2e98.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/640)



### 使用方式:
1、在项目下的build.gradle文件中引入（注意gradle的版本）：
```
//gradle版本在3.0以下引入此行
compile 'com.lcw.library:imagepicker:1.1.2'

//gradle版本在3.0以上引入此行
implementation 'com.lcw.library:imagepicker:1.1.2'
```

2、然后需要在AndroidManifest.xml里声明组件：
```
<application>
....
   <!--图片选择器的主Activity-->
        <activity
            android:name="com.lcw.library.imagepicker.activity.ImagePickerActivity"
            android:screenOrientation="portrait" />
  <!--图片大图预览-->
         <activity
            android:name="com.lcw.library.imagepicker.activity.ImagePreActivity"
            android:screenOrientation="portrait" />
...
</application>

```


3、调用方式非常简单，只需要简单一行代码：
```
 ImagePicker.getInstance()
                        .setTitle("标题")//设置标题
                        .showCamera(true)//设置是否显示拍照按钮
                        .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
                        .setImageLoader(new GlideLoader())//设置自定义图片加载器
                        .start(mContext, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
```

4、获取选择图片返回的数据：
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
        }
    }
```

5、关于自定义图片加载器，不具体指定图片加载框架，让开发者更加灵活的定制，只需要去实现ImageLoader接口即可：
```
public class GlideLoader implements ImageLoader {

    @Override
    public void loadImage(ImageView imageView, String imagePah) {
        //小图加载，这里以Glide图片加载框架为例
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.icon_image_default)
                .error(R.mipmap.icon_image_error);
        Glide.with(imageView.getContext()).load(imagePah).apply(options).into(imageView);
    }

}
```

6、关于权限，6.0以后危险权限需要动态申请，不了解的同学可以看下我之前写过的一篇文章[《适配Android6.0动态权限管理》](https://www.jianshu.com/p/a37f4827079a)，由于国内各大厂商的ROM存在差异化，需要经常处理一些兼容上的问题，也有比较成熟的开源库，为了简洁，本Library就不提供此功能，请开发者自行处理，所需权限：
```
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
```

7、最后需要注意的是在Android7.0后私有目录被限制访问，这里做了兼容处理，在开启拍照按钮的时候需要注意：

（1）首先需要在AndroidManifest.xml里声明组件：
```
<application>
....
 <!-- Android 7.0 文件共享配置 -->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.lcw.library.imagepicker.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/image_picker" />
        </provider>
....
</application>
```
（2）然后在res文件夹下建立一个xml文件夹，放置xml文件（image_picker.xml）即可，xml内容如下：
```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path
        name="imagePicker"
        path="" />
</paths>
```

8、其他：
根据业务的需求，有时候我们在选择一部分图片后，再次跳转图片选择器的时候，想要去保存已经勾选的图片状态，这边也提供了对应的方法，只需要把onActivityResult返回的图片路径List集合，重新设置进来即可，代码如下：
```
    ImagePicker.getInstance()
                        .setTitle("标题")
                        .showCamera(true)
                        .setMaxCount(9)
                        .setImagePaths(mImagePaths)//设置list
                        .setImageLoader(new GlideLoader())
                        .start(MainActivity.this, REQUEST_SELECT_IMAGES_CODE);
 ```



版本会持续迭代，未完待续。。。（欢迎Star，欢迎Fork）

 

