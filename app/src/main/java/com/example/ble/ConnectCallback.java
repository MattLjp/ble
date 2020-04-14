package com.example.ble;

import android.bluetooth.BluetoothGatt;

/**
 * @author matt.Ljp
 * @time 2020/4/13 9:24 PM
 * @description
 */
public interface ConnectCallback {
    /**
     * 连接成功
     */
    void onConnSuccess();

    /**
     * 断开或连接失败
     *
     * @param message
     */
    void onConnFailed(String message);

}
