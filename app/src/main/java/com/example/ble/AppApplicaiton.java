package com.example.ble;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import pub.devrel.easypermissions.BuildConfig;

/**
 * @author kevin.wangzhiqiang
 * @Date 2019/9/12 16:01
 * @Description
 */
public class AppApplicaiton extends Application {
    private static Context mContext;
    private static AppApplicaiton sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        sInstance = this;
    }

    public static Context getContext() {
        return mContext;
    }

    public static AppApplicaiton getInstance() {
        return sInstance;
    }
}

