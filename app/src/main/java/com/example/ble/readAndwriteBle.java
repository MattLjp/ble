package com.example.ble;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author matt.Ljp
 * @time 2020/4/14 9:40 PM
 * @description
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class readAndwriteBle extends AppCompatActivity {

    private EditText etSend;
    private Button btnSend;
    private TextView tvMsg;
    private Button btnMsg;
    private BleController mBleController;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_and_write_ble);
        etSend = findViewById(R.id.ed_msg);
        btnSend = findViewById(R.id.btn_senddiy);
        tvMsg = findViewById(R.id.tv_msg);
        btnMsg = findViewById(R.id.bt_get_ble_name);
        mBleController = BleController.getBleController(this);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = etSend.getText().toString();
                if (!"".equals(s)) {
                    mBleController.writeBuffer(s.getBytes());
                }

            }
        });
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBleController.readBuffer();
            }
        });

        mBleController.setReadDataCallback(new ReadDataCallback() {
            @Override
            public void onFailed() {
                ToastUtils.show("发送数据失败");
            }

            @Override
            public void onReadSuccess(byte[] bytes) {
                ToastUtils.show(new String(bytes));
                tvMsg.setText(new String(bytes));
            }
        });

    }
}
