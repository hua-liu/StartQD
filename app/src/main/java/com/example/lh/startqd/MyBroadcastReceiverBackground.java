package com.example.lh.startqd;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lh.startqd.model.Bluetooth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lh on 2017/12/24.
 */
public class MyBroadcastReceiverBackground extends BroadcastReceiver {
    private Context applicationContext = null;
    private static final String TAG="MyBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        applicationContext = context.getApplicationContext();
        Set<String> bluetooths = readBluetooth();
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
            if (bluetooth.isCheck()) {
                    startQD();
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        }
    }
    public Set<String> readBluetooth() {
        SharedPreferences sp = applicationContext.getSharedPreferences("myfile", applicationContext.MODE_PRIVATE);
        return sp.getStringSet("checkBluetoothAddress", new HashSet<String>());
    }

    private void startQD() {
        //获取正在运行程序
       /* ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : appProcesses){
            String[] pkgList = info.pkgList;
            for (String packageName : pkgList) {
                //判断是不是在前台运行
                if("com.qding.community".equals(packageName)&&info.importance== ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    Log.i(TAG,"千丁已在运行");
                    return;
                }
            }
        }*/
        Log.i(TAG,"正在启动千丁");
        final Intent intent = new Intent();
        intent.setClassName("com.qding.community", "com.qding.community.global.opendoor.ShortcutOpenDoorActivity");
        applicationContext.startActivity(intent);
    }
    public int readStartNumber() {
        SharedPreferences sp = applicationContext.getSharedPreferences("myfile", applicationContext.MODE_PRIVATE);
        return sp.getInt("startNumber",-50);
    }
}
