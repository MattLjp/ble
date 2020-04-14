package com.example.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    private BleController mBleController;
    private RecyclerView mRecyclerView;
    private BleListAdapter mBleListAdapter;
    private List<BluetoothDevice> mDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mBleController = BleController.getBleController(this);

        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // 已经申请过权限，做想做的事
            scanBle();

        } else {
            // 没有申请过权限，现在去申请
            /**
             *@param host Context对象
             *@param rationale  权限弹窗上的提示语。
             *@param requestCode 请求权限的唯一标识码
             *@param perms 一系列权限
             */
            EasyPermissions.requestPermissions(
                    MainActivity.this,
                    "申请权限",
                    0,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
        mDevices = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rcv_ble_mian);
        mBleListAdapter = new BleListAdapter(this, mDevices);
        mRecyclerView.setAdapter(mBleListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mBleListAdapter.setOnClickListener(new BleListAdapter.OnClickListener() {
            @Override
            public void onClick(String adress) {
                conneBle(adress);
            }
        });


    }

    private void scanBle() {
        if (mBleController.checkBleDevice()) {
            if (!mBleController.isEnabledBt()) {
                //打开蓝牙
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            } else {
                mBleController.scanLeDevice(true, new BleScanCallback() {
                    @Override
                    public void onSuccess() {
                        ToastUtils.show("搜索结束");
                    }

                    @Override
                    public void onScanning(BluetoothDevice device, int rssi) {

                        LogUtils.d(device.getAddress());
                        mBleListAdapter.addDevice(device);
                    }
                });

            }

        }
    }


    private void conneBle(String adress) {
        mBleController.Connect(adress, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                ToastUtils.show("连接成功");
                Intent intent = new Intent(MainActivity.this, readAndwriteBle.class);
                startActivity(intent);
            }

            @Override
            public void onConnFailed(String message) {
                ToastUtils.show("连接失败");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (mBleController.isEnabledBt()) {
                scanBle();
            }
        }
    }


    /**
     * 重写onRequestPermissionsResult，用于接受请求结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限成功。
     * 可以弹窗显示结果，也可执行具体需要的逻辑操作
     *
     * @param requestCode
     * @param perms
     */

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e("Permissions", "用户授权成功");
        if (mBleController.checkBleDevice()) {
            if (!mBleController.isEnabledBt()) {
                //打开蓝牙
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            } else {
                scanBle();
            }

        }

    }

    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e("Permissions", "用户授权失败");
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setTitle("需要定位权限")
                    .setRationale("没有定位权限，此应用程序可能无法正常工作。请打开“应用程序设置”界面修改应用程序权限。")
                    .build()
                    .show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.e("Permissions", "用户看到我们的提示选择了继续申请权限");
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.e("Permissions", "用户看到我们的提示依然选择了残忍拒绝");
    }

    @AfterPermissionGranted(0)
    public void allGranted() {
        Log.e("Permissions", "请求的权限全部被允许时调用调用此方法");
    }


}

