package org.sheedon.storagelibrary;

/**
 * 迁移监听器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/13 9:37 PM
 */
public interface MigrateListener {

    void onProgress(int progress);

    void onComplete(boolean isSuccess);
}
