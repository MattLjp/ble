package com.example.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author matt.Ljp
 * @time 2020/4/13 10:55 AM
 * @description
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleController {

    //客户端特征配置（一般不需要修改）
    private final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    // 通用接入规范 ServiceUUID
    private final static String GAP_SERVICE_UUID = "00001800-0000-1000-8000-00805f9b34fb";
    // 设备名称 characterUUID
    private final static String DEVICE_NAME_UUID = "00002a00-0000-1000-8000-00805f9b34fb";

    //TODO 以下uuid根据自己需要连接的硬件修改，可以查阅厂家说明书
    //UUID服务
    private final static String UUID_SERVICE = "ee800000-5eaf-f013-e8f9-acb1eedcca6d";
    //接收数据UUID
    private final static String UUID_NOTIFY = "ee800002-5eaf-f013-e8f9-acb1eedcca6d";
    //发送数据UUID
    private final static String UUID_WRITE = "ee800003-5eaf-f013-e8f9-acb1eedcca6d";


    private Context mContext;
    private static BleController mBleController;
    private BluetoothAdapter mBleAdapter;
    private BluetoothGattCallback mGattCallback;
    private ConnectCallback mConnectCallback;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private ScanCallback mScanCallback;
    private ReadDataCallback mReadDataCallback;
    private Handler mHandler = new Handler();
    //获取到所有服务的集合
    private Map<String, Map<String, BluetoothGattCharacteristic>> servicesMap = new HashMap<>();
    //当前是否开启蓝牙扫描标志
    private boolean mScanning;
    //蓝牙扫描时间
    private final static int SCAN_PERIOD = 10000;

    private BleController() {
    }

    private BleController(Context context) {
        mGattCallback = new BleGattCallback();
        //首先获取BluetoothManager
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != mBluetoothManager) {
            //获取BluetoothAdapter
            mBleAdapter = mBluetoothManager.getAdapter();
        } else {
            LogUtils.e("BluetoothManager init error!");
        }

        mContext = context;
    }


    public static BleController getBleController(Context context) {
        if (mBleController == null) {
            mBleController = new BleController(context);
        }
        return mBleController;
    }

    /**
     * 判断是否支持蓝牙
     */
    public boolean checkBleDevice() {
        if (mBluetoothManager != null) {
            return mBleAdapter != null;
        } else {
            return false;
        }
    }

    /**
     * 是否当前是否打开蓝牙
     *
     * @return
     */
    public boolean isEnabledBt() {
        if (mBleAdapter != null) {
            return mBleAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 开始关闭搜索蓝牙
     */
    public void scanLeDevice(final boolean enable, final BleScanCallback callback) {
        if (mScanCallback == null) {
            mScanCallback = new BleDevceScanCallback(callback);
        }
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            // 预先定义停止蓝牙扫描的时间（因为蓝牙扫描需要消耗较多的电量）
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBleAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
                    callback.onSuccess();
                }
            }, SCAN_PERIOD);
            mScanning = true;

            // 定义一个回调接口供扫描结束处理
            mBleAdapter.getBluetoothLeScanner().startScan(mScanCallback);
        } else {
            mScanning = false;
            mBleAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        }
    }


    /**
     * 连接设备
     *
     * @param address 设备mac地址
     */
    public void Connect(String address, ConnectCallback connectCallback) {

        if (mBleAdapter == null || address == null) {
            LogUtils.e("No device found at this address：" + address);
            return;
        }
        mConnectCallback = connectCallback;
        try {
            BluetoothDevice remoteDevice = mBleAdapter.getRemoteDevice(address);
            if (remoteDevice == null) {
                LogUtils.e("Device not found.  Unable to connect.");
                return;
            }
            remoteDevice.connectGatt(mContext, false, mGattCallback);
            LogUtils.e("connecting mac-address:$address");
        } catch (Exception e) {
            LogUtils.e("蓝牙地址错误，请重新绑定");
        }
    }

    /**
     * 读取设备名称
     */
    public void readBuffer() {
        if (mBluetoothGatt == null) {
            return;
        }
        BluetoothGattCharacteristic characteristic = getBluetoothGattCharacteristic(GAP_SERVICE_UUID, DEVICE_NAME_UUID);
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 写入数据
     */
    public void writeBuffer(byte[] sendValue) {
        if (mBluetoothGatt == null) {
            return;
        }
        //往蓝牙数据通道的写入数据
        BluetoothGattCharacteristic characteristic = getBluetoothGattCharacteristic(UUID_SERVICE, UUID_WRITE);
        characteristic.setValue(sendValue);
        boolean a = mBluetoothGatt.writeCharacteristic(characteristic);
        if (!a && mReadDataCallback != null) {
            mReadDataCallback.onFailed();
        }

    }

    /**
     * 断开连接
     */
    public void close() {
        if (null == mBleAdapter || null == mBluetoothGatt) {
            LogUtils.e("disconnection error maybe no init");
            return;
        }
        //断开连接
        mBluetoothGatt.disconnect();
        //释放gatt
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        servicesMap.clear();
    }


    private class BleGattCallback extends BluetoothGattCallback {

        //蓝牙连接状态回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
                gatt.discoverServices();
                connSuccess();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   //断开连接
                connFailed("断开连接");
            }
        }

        //发现服务回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //将所有的特征保存
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService bluetoothGattService : services) {
                    Map<String, BluetoothGattCharacteristic> charMap = new HashMap<>();
                    String serviceUuid = bluetoothGattService.getUuid().toString();
                    List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        charMap.put(characteristic.getUuid().toString(), characteristic);
                    }
                    servicesMap.put(serviceUuid, charMap);
                }
                //根据实际的服务UUID和特征UUID获取特征[BluetoothGattCharacteristic]
                BluetoothGattCharacteristic notificationCharacteristic = getBluetoothGattCharacteristic(UUID_SERVICE, UUID_NOTIFY);
                //向蓝牙设备注册监听
                enableNotification(true, notificationCharacteristic, gatt);
                mBluetoothGatt = gatt;
            }


        }

        //蓝牙设备发送通知回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            LogUtils.i("蓝牙回复信息：" + Arrays.toString(characteristic.getValue()));
            if (mReadDataCallback != null) {
                mReadDataCallback.onReadSuccess(characteristic.getValue());
            }
        }

        //读取蓝牙设备数据回调
        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtils.d("读到的数据：" + Arrays.toString(characteristic.getValue()));
                if (mReadDataCallback != null) {
                    mReadDataCallback.onReadSuccess(characteristic.getValue());
                }
            }
        }
    }

    /**
     * 根据服务UUID和特征UUID,获取一个特征[BluetoothGattCharacteristic]
     *
     * @param serviceUUID   服务UUID
     * @param characterUUID 特征UUID
     */
    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(String serviceUUID, String characterUUID) {

        //找服务
        Map<String, BluetoothGattCharacteristic> bluetoothGattCharacteristicMap = servicesMap.get(serviceUUID);
        if (null == bluetoothGattCharacteristicMap) {
            LogUtils.e("Not found the serviceUUID!");
            return null;
        }

        //找特征
        BluetoothGattCharacteristic gattCharacteristic = bluetoothGattCharacteristicMap.get(characterUUID);
        if (null == gattCharacteristic) {
            LogUtils.e("Not found the characterUUID!");
            return null;
        }
        return gattCharacteristic;
    }

    /**
     * 向蓝牙设备注册监听
     *
     * @param isEnable
     * @param characteristic
     * @param gatt
     */
    private void enableNotification(boolean isEnable, BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        gatt.setCharacteristicNotification(characteristic, isEnable);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    public void setReadDataCallback(ReadDataCallback readDataCallback) {
        mReadDataCallback = readDataCallback;
    }


    @UiThread
    private void connFailed(String message) {
        if (mConnectCallback != null) {
            mConnectCallback.onConnFailed(message);
        }
        LogUtils.e("Ble disconnect or connect failed!");

    }

    @UiThread
    private void connSuccess() {
        if (mConnectCallback != null) {
            mConnectCallback.onConnSuccess();
        }
        LogUtils.e("Ble connect success!");
    }


}
