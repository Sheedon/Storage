package org.sheedon.storagelibrary;

import java.io.File;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;

/**
 * 迁移插件
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/17 7:52 PM
 */
public class MigratePlugin {

    private String fromDir;
    private String toDir;

    private ObservableEmitter<PluginTypeModel<Integer>> emitter;
    private int index = 0;

    public MigratePlugin(boolean isExternalSDCard, File insideDir, File outsideDir) {
        if (isExternalSDCard) {
            fromDir = insideDir.getAbsolutePath();
            toDir = outsideDir.getAbsolutePath();
        } else {
            toDir = insideDir.getAbsolutePath();
            fromDir = outsideDir.getAbsolutePath();
        }

    }

    public ObservableSource<PluginTypeModel<Integer>> calcFileNum() {
        return Observable.create(new ObservableOnSubscribe<PluginTypeModel<Integer>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PluginTypeModel<Integer>> emitter) throws Throwable {

                File file = new File(fromDir);

                int num = FileUtils.calcFilesNum(file);
                PluginTypeModel<Integer> typeModel = PluginTypeModel.build(PluginTypeModel.TYPE_CALC_NUM, num);
                emitter.onNext(typeModel);
                emitter.onComplete();
            }
        });
    }


    public ObservableSource<PluginTypeModel<Integer>> executeMigrate() {
        return Observable.create(new ObservableOnSubscribe<PluginTypeModel<Integer>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PluginTypeModel<Integer>> emitter) throws Throwable {
                MigratePlugin.this.emitter = emitter;
                index = 0;
                File file = new File(fromDir);
                move(file);

                if (emitter != null && !emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    /**
     * 读取下一级文件
     */
    private void readNextLevelFile(File file) {
        if (file == null)
            return;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return;

            for (File file1 : files) {
                if (file1 == null) {
                    continue;
                }

                if (file1.isDirectory()) {
                    readNextLevelFile(file1);
                    continue;
                }

                if (file1.isFile()) {
                    move(file1);
                }
            }
        }
    }

    /**
     * 移动文件
     */
    private void move(File fromFile) {
        if (fromFile == null) {
            return;
        }

        if (fromFile.isDirectory()) {
            readNextLevelFile(fromFile);
            return;
        }

        if (fromFile.isFile()) {
            File outFile = new File(fromFile.getAbsolutePath().replace(fromDir, toDir));
            File parentFile = outFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            boolean isSuccess = FileUtils.copyFile(fromFile, outFile);
            if (isSuccess) {
                fromFile.delete();
                if (emitter != null && !emitter.isDisposed()) {
                    PluginTypeModel<Integer> typeModel = PluginTypeModel.build(PluginTypeModel.TYPE_MIGRATE, index++);
                    emitter.onNext(typeModel);
                }
            }
        }
    }

    public void destroy() {
        if (emitter != null && !emitter.isDisposed()) {
            emitter.onComplete();
        }
        emitter = null;
        fromDir = null;
        toDir = null;
    }
}
