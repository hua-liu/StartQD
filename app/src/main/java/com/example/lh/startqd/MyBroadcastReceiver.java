package com.example.lh.startqd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lh.startqd.model.Bluetooth;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lh on 2017/12/24.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver(){}
    private MainActivity mainActivity;
    public MyBroadcastReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Context applicationContext = context.getApplicationContext();
        Set<String> bluetooths = readBluetooth(applicationContext);
        String action = intent.getAction();
        //查找设备
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //获取蓝牙设备
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //获取信号强度
            short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
            final Bluetooth bluetooth = new Bluetooth();
            bluetooth.setName(bluetoothDevice.getName()==null? "未知设备" : bluetoothDevice.getName());
            bluetooth.setAddress(bluetoothDevice.getAddress());
            bluetooth.setStrong(rssi);
            //判断是否为监控设备
            bluetooth.setCheck(bluetooths != null && bluetooths.contains(bluetooth.getAddress()));
            mainActivity.appendLayout(bluetooth);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        }
    }
    public Set<String> readBluetooth(Context applicationContext) {
        SharedPreferences sp = applicationContext.getSharedPreferences("myfile", applicationContext.MODE_PRIVATE);
        return sp.getStringSet("checkBluetoothAddress", new HashSet<String>());
    }
}
