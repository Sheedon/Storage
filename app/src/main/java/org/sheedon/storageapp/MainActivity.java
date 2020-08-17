package org.sheedon.storageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.View;

import org.sheedon.storagelibrary.MigrateListener;
import org.sheedon.storagelibrary.StorageDispatcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StorageDispatcher.setUp(getApplication());
    }

    public void onReboot(View view) {

        StorageDispatcher.migrateDir(this, true, new MigrateListener() {
            @Override
            public void onProgress(int progress) {
                System.out.println(progress);
            }

            @Override
            public void onComplete(boolean isSuccess) {
                System.out.println("结果:" + isSuccess);
            }
        });
    }
}