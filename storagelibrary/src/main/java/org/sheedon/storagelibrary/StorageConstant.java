package org.sheedon.storagelibrary;

/**
 * 存储基本常量信息
 * 采用Android系统的外置内存布局设计
 * 外置内存卡保存路径内容
 * /storage/emulated/0/Android/data/xx.xx.xx下面
 * +- cache
 *
 * +- files
 * 	  - Pictures
 * 	  - Alarms
 * 	  - Movies
 * 	  - Download
 * 	    -- apk
 * 	    -- plugin
 *
 * +- customSource
 * 	  - xx
 * 	  - xx
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/12 4:31 PM
 */
public interface StorageConstant {

    int TYPE_NONE = 0;// 当前层级
    int TYPE_IMAGE = 1;// 图片
    int TYPE_VIDEO = 2;// 视频
    int TYPE_AUDIO = 3;// 音频
    int TYPE_LOG = 4;// log文件
    int TYPE_APK = 5;// apk文件
    int TYPE_PLUGIN = 6;// 热更新插件

}
