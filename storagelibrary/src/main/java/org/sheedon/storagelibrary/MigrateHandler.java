package org.sheedon.storagelibrary;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 迁移工具类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/13 9:45 PM
 */
public class MigrateHandler {

    private MigrateListener listener;

    private volatile boolean isExternalSDCard;
    private volatile boolean migrating = false;

    private MigratePlugin migratePlugin;

    private Observer<PluginTypeModel<Integer>> callback;
    private Disposable disposable;

    private File[] files;

    public MigrateHandler(boolean isExternalSDCard, File[] files) {
        this.isExternalSDCard = isExternalSDCard;
        this.files = files;

        create();
    }

    // 创建反馈观察者
    private void create() {
        callback = new Observer<PluginTypeModel<Integer>>() {
            int count = 1;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(PluginTypeModel<Integer> model) {
                if (model == null)
                    return;

                if (listener == null)
                    return;

                switch (model.getType()) {
                    case PluginTypeModel.TYPE_CALC_NUM:
                        count = model.getData() == null || model.getData() == 0 ? Integer.MAX_VALUE : model.getData();
                        break;
                    case PluginTypeModel.TYPE_MIGRATE:
                        int index = model.getData();
                        listener.onProgress(index * 100 / count);
                        break;
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (listener != null)
                    listener.onComplete(false);
            }

            @Override
            public void onComplete() {
                if (listener != null) {
                    listener.onComplete(true);
                }
            }

        };
    }

    /**
     * 迁移动作
     * 状态：
     * -> 未迁移
     * -> 存储是否一致
     * -> 一致
     * 不做处理
     * -> 不一致
     * 迁移
     * <p>
     * <p>
     * -> 正在迁移
     * -> 当前存储是否一致
     * -> 不一致
     * 不做处理
     * -> 一致
     * 暂停迁移，并且恢复文件
     */
    public void migrate(boolean isExternalSDCard, MigrateListener listener) {
        this.listener = listener;
        // 迁移中
        if (migrating) {
            if (isExternalSDCard == this.isExternalSDCard)
                return;

            // 停止迁移，恢复文件
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            migrating = false;
            migrate(isExternalSDCard, listener);
            return;
        }

        if (this.isExternalSDCard == isExternalSDCard)
            return;

        if (files == null || files.length <= 0) {
            if (listener != null) {
                listener.onComplete(false);
            }
            return;
        }

        migrating = true;

        if (migratePlugin != null) {
            migratePlugin.destroy();
            migratePlugin = null;
        }

        migratePlugin = new MigratePlugin(isExternalSDCard, files[0], files[1]);

        Observable.concat(migratePlugin.calcFileNum(),
                migratePlugin.executeMigrate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }


    public void close() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        migrating = false;

        if (migratePlugin != null) {
            migratePlugin.destroy();
        }
        migratePlugin = null;
    }


}
