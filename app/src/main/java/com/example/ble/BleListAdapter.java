package com.example.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author matt.Ljp
 * @time 2020/4/13 2:02 PM
 * @description
 */
public class BleListAdapter extends RecyclerView.Adapter<BleListAdapter.bleListHolder> {
    private Context mContext;
    private List<BluetoothDevice> deviceInfo;


    public BleListAdapter(Context context, List<BluetoothDevice> deviceInfo) {
        mContext = context;
        this.deviceInfo = deviceInfo;
    }

    public List<BluetoothDevice> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(List<BluetoothDevice> deviceInfo) {
        this.deviceInfo = deviceInfo;
        notifyDataSetChanged();
    }

    public void addDevieceInfo(List<BluetoothDevice> deviceInfo) {
        this.deviceInfo.addAll(deviceInfo);
        notifyDataSetChanged();
    }

    public void addDevice(BluetoothDevice device) {
        if (!deviceInfo.contains(device)) {
            deviceInfo.add(device);
        }
        notifyDataSetChanged();
    }

    public void clearDeviceInfo() {
        this.deviceInfo = null;
        notifyDataSetChanged();
    }


    /**
     * 手动添加点击事件
     */
    public interface OnClickListener {
        void onClick(String adress);
    }

    private OnClickListener mOnClickListener = null;

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public bleListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取视图
        View view = inflater.inflate(R.layout.ble_list, parent, false);
        return new bleListHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull bleListHolder holder, final int position) {
        final BluetoothDevice result = deviceInfo.get(position);
        String name = result.getName();
        String mac = result.getAddress();

        holder.bindText(name, mac);
        if (mOnClickListener != null) {
            //为ItemView设置监听器
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onClick(deviceInfo.get(position).getAddress());
                }
            });
        }

    }


    @Override
    public int getItemCount() {                             //获取项目数量
        return deviceInfo == null ? 0 : deviceInfo.size();
    }


    static class bleListHolder extends RecyclerView.ViewHolder {
        private TextView bleName;
        private TextView bleMac;
        ConstraintLayout constraintLayout;


        bleListHolder(View itemView) {
            super(itemView);
            bleName = itemView.findViewById(R.id.ble_name);
            bleMac = itemView.findViewById(R.id.ble_mac);
            constraintLayout = itemView.findViewById(R.id.cl_ble_list_item);
        }

        private void bindText(String name, String mac) {

            bleName.setText(name);
            bleMac.setText(mac);
        }
    }
}
