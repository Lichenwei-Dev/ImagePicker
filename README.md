# 高仿微信图片选择器

#### 注意开发细节，尽可能的做到加载速度最快，目前支持图片，视频单选，多选，多文件夹切换，大图预览，自定义图片加载器等功能。

#### 效果图：
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot1.png)
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot2.png)
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot3.png)
 ![](https://github.com/Lichenwei-Dev/ImagePicker/blob/master/screenshot/Screenshot4.png)
 
 
 
版本更新历史：
### Version1.0.0：
1、可预览各文件夹下的图片  
2、可配置是否支持相机拍照  
3、可配置选择图片模式（单选/多选）  
4、可配置选择图片数量  
5、可配置图片加载框架

### Version1.1.0：
1、新增大图预览功能（初步实现，考虑性能后期会改为Fragment承载，单一Activity架构）  
2、更改选择图片文件夹弹窗高度  
3、更改ImageLoader接口，开放小图加载、大图加载、清除缓存方法   

### Version2.0.0：
1、重构了大量的代码，更加注重代码间的解耦，相比1.0.+版本简化了配置项，让开发者可以更专注于业务。  
2、添加媒体库扫描对GIF、视频的支持，并开放接口让开发者自行实现视频播放逻辑。  
3、添加媒体库加载策略，开发者可自行配置加载图片或者视频，灵活运用于不同业务实现。  
4、完善大图预览功能，完善部分UI界面的显示。


# 使用方式:

1、如何在项目中引入该图片加载库：
```
                //gradle版本在3.0以上引入此行
                implementation 'com.lcw.library:imagepicker:2.2.7'
```
2、如何自定义图片加载器（不定死框架，让框架更加灵活，需要去实现ImageLoader接口即可，如果需要显示视频，优先推荐Glide加载框架，可以参考Demo实现）：
```
            public class GlideLoader implements ImageLoader {
                //to do something 可以参考Demo用法
                
            }
```
3、一行代码调用：
```
                ImagePicker.getInstance()
                        .setTitle("标题")//设置标题
                        .showCamera(true)//设置是否显示拍照按钮
                        .showImage(true)//设置是否展示图片
                        .showVideo(true)//设置是否展示视频
                        .filterGif(false)//设置是否过滤gif图片
                        .setSingleType(true)//设置图片视频不能同时选择
                        .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
                        .setImagePaths(mImageList)//保存上一次选择图片的状态，如果不需要可以忽略
                        .setImageLoader(new GlideLoader())//设置自定义图片加载器
                        .start(MainActivity.this, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
```

4、如何获取选中的图片集合：
```
                @Override
                protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                    if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
                        List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                    }
                }
```

版本会持续迭代，欢迎大家给建议。。。（欢迎Star，欢迎Fork）

 

