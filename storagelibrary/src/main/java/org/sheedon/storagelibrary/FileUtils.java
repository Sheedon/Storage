package org.sheedon.storagelibrary;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 文件处理
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/8/13 3:09 PM
 */
public class FileUtils {

    /**
     * 检测外置SD卡是否有效
     */
    public static boolean isHasExternalSDCard() {
        String storageState = Environment.getExternalStorageState();
        switch (storageState) {
            case Environment.MEDIA_MOUNTED:
            case Environment.MEDIA_MOUNTED_READ_ONLY:
            case Environment.MEDIA_SHARED:
                return true;
        }

        return false;
    }

    /**
     * 复制单个文件
     *
     * @param oldFile File 原文件 如：data/user/0/com.test/files/abc.txt
     * @param newFile File 复制后文件 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     * <code>false</code> otherwise
     */
    public static boolean copyFile(File oldFile, File newFile) {
        try {

            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }


            FileInputStream fileInputStream = new FileInputStream(oldFile);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 计算文件个数
     *
     * @param file 文件
     * @return 文件个数
     */
    public static int calcFilesNum(File file) {
        if (file == null)
            return 0;

        if (file.isFile())
            return 1;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return 0;
            int num = 0;
            for (File file1 : files) {
                num += calcFilesNum(file1);
            }
            return num;
        }

        return 0;
    }


}
