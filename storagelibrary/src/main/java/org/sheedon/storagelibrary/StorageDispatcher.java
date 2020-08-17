package org.sheedon.storagelibrary;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.List;

/**
 * 存储调度器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/13 2:21 PM
 */
public class StorageDispatcher {

    private static final String TAG = "Storage.StorageDispatcher";

    public static Storage setUp(Application application) {
        return setUp(application, null);
    }

    public static Storage setUp(Application application, List<String> sourcePaths) {
        Storage upgrade = new Storage.Builder(application).sourcePaths(sourcePaths).build();
        upgrade.setUp();
        return upgrade;
    }

    /**
     * ------------------------------------------------------
     * 内部存储方法，直接使用系统方法
     * ------------------------------------------------------
     */

    // 获取根目录
    public static File getSystemRootDirectory() {
        return Environment.getDataDirectory();
    }

    // 系统缓存目录
    public static File getSystemCacheDir(Context context) {
        return context.getCacheDir();
    }

    // 系统文件目录
    public static File getSystemFilesDir(Context context) {
        return context.getFilesDir();
    }

    // 系统文件子目录
    public static File getSystemFilesChildDir(Context context, String path) {
        if (path == null || path.isEmpty())
            return getSystemFilesDir(context);

        return context.getFileStreamPath(path.replace("/", ""));
    }

    /**
     * ------------------------------------------------------
     * 外部存储，借助 Storage 来调度
     * ------------------------------------------------------
     */

    // 外部存储缓存目录
    public static File getExternalCacheDir(Context context) {
        if (context == null)
            return null;

        return Storage.with(context).getExternalCacheDir();
    }

    // 外部存储文件目录
    public static File getExternalFilesDir(Context context) {
        return Storage.with(context).getExternalFilesDir(StorageConstant.TYPE_NONE);
    }

    // 外部存储图片目录
    public static File getExternalImageDir(Context context) {
        return Storage.with(context).getExternalFilesDir(StorageConstant.TYPE_IMAGE);
    }

    // 外部存储视频目录
    public static File getExternalVideoDir(Context context) {
        return Storage.with(context).getExternalFilesDir(StorageConstant.TYPE_VIDEO);
    }

    // 外部存储音频目录
    public static File getExternalAudioDir(Context context) {
        return Storage.with(context).getExternalFilesDir(StorageConstant.TYPE_AUDIO);
    }

    // 外部存储Log目录
    public static File getExternalLogDir(Context context) {
        return Storage.with(context).getExternalFilesDir(StorageConstant.TYPE_LOG);
    }

    // 外部存储Apk目录
    public static File getExternalApkDir(Context context) {
        return Storage.with(context).getExternalFilesDir(StorageConstant.TYPE_APK);
    }

    // 外部存储PlugIn目录
    public static File getExternalPlugInDir(Context context) {
        return Storage.with(context).getExternalFilesDir(StorageConstant.TYPE_PLUGIN);
    }

    // 外部存储自定义目录
    public static File getExternalSourceDir(Context context) {
        return getExternalSourceDir(context, "");
    }

    // 外部存储自定义目录子目录
    public static File getExternalSourceDir(Context context, String childPath) {
        return Storage.with(context).getExternalSourceDir(childPath);
    }

    // 动作

    public static void migrateDir(Context context, boolean isExternalSDCard, MigrateListener listener) {
        Storage.with(context).migrateDir(isExternalSDCard, listener);
    }


}
