# 高仿微信图片选择器

#### 注意开发细节，尽可能的做到加载速度最快，目前支持图片，视频单选，多选，多文件夹切换，大图预览，自定义图片加载器等功能。

#### 效果图：
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot1.png)
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot2.png)
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot3.png)
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot4.png)
 
 
 
版本更新历史：
#### Version1.0.0：
1、可预览各文件夹下的图片
2、可配置是否支持相机拍照
3、可配置选择图片模式（单选/多选）
4、可配置选择图片数量
5、可配置图片加载框架

#### Version1.1.0：
1、新增大图预览功能（初步实现，考虑性能后期会改为Fragment承载，单一Activity架构）
2、更改选择图片文件夹弹窗高度
3、更改ImageLoader接口，开放小图加载、大图加载、清除缓存方法

#### Version2.0.0：
高仿微信图片选择器界面，提供基础图片选择器功能
1、重构了大量的代码，更加注重代码间的解耦，相比1.0版本简化了配置项（下沉FileProvider），让开发者可以更专注于业务。
2、添加媒体库扫描对GIF、视频的支持，并开放接口让开发者自行实现视频播放逻辑。
3、添加媒体库加载策略，开发者可自行配置加载图片或者视频，灵活运用于不同业务实现。
4、完善大图预览功能，完善部分UI界面的显示。

 
 

相比1.0.+版本，2.0版本进行了代码的大块重构，注重模块间的代码解耦，简化了配置，将兼容Android7.0所需要的FileProvider下沉到库中完成，不再需要开发者配置，并对GIF图，视频，大图预览等功能有了支持。

### 使用方式:

1、如何在项目中引入该图片加载库：
```
//gradle版本在3.0以下引入此行
compile 'com.lcw.library:imagepicker:2.0.2'

//gradle版本在3.0以上引入此行
implementation 'com.lcw.library:imagepicker:2.0.2'
```

2、需要申请的权限：
```
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
```

3、需要在AndroidManifest.xml里声明组件：
```
<application>
....
   <!--图片列表Activity-->
        <activity
            android:name="com.lcw.library.imagepicker.activity.ImagePickerActivity"
            android:screenOrientation="portrait" />
  <!--大图预览Activity-->
         <activity
            android:name="com.lcw.library.imagepicker.activity.ImagePreActivity"
            android:screenOrientation="portrait" />
...
</application>

```

4、一行代码调用：
```
                ImagePicker.getInstance()
                        .setTitle("标题")//设置标题
                        .showCamera(true)//设置是否显示拍照按钮
                        .showImage(true)//设置是否展示图片
                        .showVideo(true)//设置是否展示视频
                        .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
                        .setImagePaths(mImageList)//保存上一次选择图片的状态，如果不需要可以忽略
                        .setImageLoader(new GlideLoader())//设置自定义图片加载器
                        .start(MainActivity.this, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
```

5、如何获取选中的图片集合：
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
        }
    }
```

6、如何自定义图片加载器（让开发者更加灵活的定制，只需要去实现ImageLoader接口即可）：
```
public class GlideLoader implements ImageLoader {
    //to do something 可以参考Demo用法
}
```

7、由于大图预览手势处理是依赖PhotoView三方库的，请在你的项目级别里的build.gradle加入：
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```


版本会持续迭代，欢迎大家给建议。。。（欢迎Star，欢迎Fork）

 

