package org.sheedon.storagelibrary;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.List;

/**
 * 存储客户端
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/13 2:21 PM
 */
public class Storage {

    private transient static Storage instance;

    private Context context;
    private List<String> sourcePaths;
    private volatile boolean isExternalSDCard = false;

    private MigrateHandler cacheHandler;

    public static Storage with(Context context) {
        if (instance == null) {
            synchronized (Storage.class) {
                if (instance == null) {
                    instance = new Builder(context).build();
                }
            }
        }
        return instance;
    }

    private Storage(Builder builder) {
        this.context = builder.context;
        this.sourcePaths = builder.sourcePaths;
    }

    void setUp() {

        isExternalSDCard = StorageInfo.isExternalSDCard(context);
        if (isExternalSDCard) {
            boolean hasExternalSDCard = FileUtils.isHasExternalSDCard();
            isExternalSDCard = hasExternalSDCard && isExternalSDCard;
        }

        if (sourcePaths == null || sourcePaths.isEmpty())
            return;

        String parentPath = getParentPath();
        if (parentPath == null)
            return;

        for (String sourcePath : sourcePaths) {
            if (sourcePath == null)
                continue;

            File file = new File(parentPath, sourcePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

    }

    /**
     * 获取外置系统包目录地址
     */
    private String getParentPath() {
        File[] externalFilesDir = context.getExternalFilesDirs("");
        if (externalFilesDir == null || externalFilesDir.length == 0) {
            File file = context.getExternalCacheDir();
            return file != null ? file.getParent() : "";
        }

        if (isExternalSDCard && externalFilesDir.length >= 2) {
            return externalFilesDir[1].getParent();
        } else {
            return externalFilesDir[0].getParent();
        }
    }

    /**
     * 获取存储缓存目录
     * 根据配置和设备支持情况，选择内部存储还是外置存储
     */
    public File getExternalCacheDir() {
        File[] externalCacheDirs = context.getExternalCacheDirs();
        if (externalCacheDirs == null || externalCacheDirs.length == 0)
            return context.getExternalCacheDir();


        if (externalCacheDirs.length == 2 && isExternalSDCard) {
            return externalCacheDirs[1];
        }

        return externalCacheDirs[0];
    }

    /**
     * 根据类型选择外部文件目录
     * 当前层级
     * 图片 / 视频 / 音频 / log文件 / apk文件 / 热更新插件
     *
     * @param type 类型
     */
    public File getExternalFilesDir(int type) {
        switch (type) {
            case StorageConstant.TYPE_NONE:
                return getExternalDefaultFilesDir("");
            case StorageConstant.TYPE_IMAGE:
                return getExternalDefaultFilesDir(Environment.DIRECTORY_PICTURES);
            case StorageConstant.TYPE_VIDEO:
                return getExternalDefaultFilesDir(Environment.DIRECTORY_MOVIES);
            case StorageConstant.TYPE_AUDIO:
                return getExternalDefaultFilesDir(Environment.DIRECTORY_MUSIC);
            case StorageConstant.TYPE_LOG:
                return getExternalDefaultFilesDir(Environment.DIRECTORY_ALARMS);
            case StorageConstant.TYPE_APK:
                return new File(getExternalDefaultFilesDir(Environment.DIRECTORY_DOWNLOADS), "Apk");
            case StorageConstant.TYPE_PLUGIN:
                return new File(getExternalDefaultFilesDir(Environment.DIRECTORY_DOWNLOADS), "Plugin");
        }

        return getExternalDefaultFilesDir("");
    }

    /**
     * 获取默认文件目录
     *
     * @param environmentType 文件类型
     */
    private File getExternalDefaultFilesDir(String environmentType) {
        File[] filesDirs = context.getExternalFilesDirs(environmentType);
        if (filesDirs == null || filesDirs.length == 0)
            return context.getExternalFilesDir(environmentType);

        if (filesDirs.length == 2 && isExternalSDCard) {
            return filesDirs[1];
        }

        return filesDirs[0];

    }

    public File getExternalSourceDir(String childPath) {
        String parent = getParentPath();
        if (childPath == null || childPath.isEmpty()) {
            return new File(parent);
        }

        return new File(parent, childPath);
    }


    /**
     * 迁移文件
     */
    public void migrateDir(boolean isExternalSDCard, MigrateListener listener) {
        File[] externalCacheDirs = context.getExternalCacheDirs();
        File[] externalFilesDirs = context.getExternalFilesDirs("");

        if (externalCacheDirs == null || externalCacheDirs.length <= 1
                || externalFilesDirs == null || externalFilesDirs.length <= 1) {

            if (listener != null) {
                listener.onComplete(false);
            }

            return;
        }

        File[] files = new File[]{externalCacheDirs[0].getParentFile(), externalCacheDirs[1].getParentFile()};

        if (cacheHandler != null) {
            cacheHandler.close();
        }
        cacheHandler = new MigrateHandler(this.isExternalSDCard, files);
        cacheHandler.migrate(isExternalSDCard, listener);
    }

    public static class Builder {

        private final Context context;
        private List<String> sourcePaths;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder sourcePaths(List<String> sourcePaths) {
            if (sourcePaths == null || sourcePaths.isEmpty())
                return this;

            this.sourcePaths = sourcePaths;
            return this;
        }

        public Storage build() {
            return new Storage(this);
        }
    }

}
