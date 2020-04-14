package com.example.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * @author matt.Ljp
 * @time 2020/4/13 10:50 PM
 * @description
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleDevceScanCallback extends ScanCallback {
    private BleScanCallback mBleScanCallback;

    private BleDevceScanCallback() {
    }

    public BleDevceScanCallback(BleScanCallback bleScanCallback) {
        mBleScanCallback = bleScanCallback;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        //对扫描的结果预处理，如判断ble广播包内容，过滤设备
        BluetoothDevice bluetoothDevice = result.getDevice();
        int rssi = result.getRssi();
        //广播包数据
        ScanRecord scanRecord = result.getScanRecord();

        mBleScanCallback.onScanning(bluetoothDevice, rssi);

    }
}
