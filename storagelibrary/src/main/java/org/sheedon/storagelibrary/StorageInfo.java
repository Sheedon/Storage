package org.sheedon.storagelibrary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 存储信息
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/13 2:58 PM
 */
class StorageInfo {

    private static final String KEY_POSITION = "KEY_POSITION";


    // 是否为外置
    private static boolean externalSDCard;

    /**
     * 存储数据到XML文件，持久化
     */
    private static void save(Context context) {
        // 获取数据持久化的SP
        SharedPreferences sp = context.getSharedPreferences(StorageInfo.class.getName(),
                Context.MODE_PRIVATE);
        // 存储数据
        sp.edit()
                .putBoolean(KEY_POSITION, externalSDCard)
                .apply();
    }

    /**
     * 进行数据加载
     */
    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(StorageInfo.class.getName(),
                Context.MODE_PRIVATE);
        externalSDCard = sp.getBoolean(KEY_POSITION, false);
    }

    /**
     * 设置是否为外置存储卡
     *
     * @param context          上下文
     * @param isExternalSDCard 是否为外置存储卡
     */
    public static void setExternalSDCard(Context context, boolean isExternalSDCard) {
        StorageInfo.externalSDCard = isExternalSDCard;
        StorageInfo.save(context);
    }

    /**
     * 是否为外置存储卡
     *
     * @param context 上下文
     */
    public static boolean isExternalSDCard(Context context) {
        if (!externalSDCard) {
            load(context);
        }
        return externalSDCard;
    }

}
