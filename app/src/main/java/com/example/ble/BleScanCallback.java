package com.example.ble;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.UiThread;

/**
 * @author matt.Ljp
 * @time 2020/4/13 10:46 PM
 * @description 扫描回调
 */
public interface BleScanCallback {
    /**
     * 扫描完成回调
     */
    @UiThread
    void onSuccess();

    /**
     * 扫描过程中,每扫描到一个设备回调一次
     *
     * @param device     扫描到的设备
     * @param rssi       设备的信息强度
     */
    @UiThread
    void onScanning(BluetoothDevice device, int rssi);
}
