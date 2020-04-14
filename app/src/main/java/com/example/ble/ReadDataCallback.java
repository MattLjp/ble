package com.example.ble;

import androidx.annotation.UiThread;

/**
 * @author matt.Ljp
 * @time 2020/4/14 9:59 PM
 * @description
 */
public interface ReadDataCallback {


    /**
     * 写入失败
     */
    @UiThread
    void onFailed();

    @UiThread
    void onReadSuccess(byte[] bytes);
}
