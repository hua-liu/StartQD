package com.example.lh.startqd;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lh on 2017/12/24.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";
    private BluetoothAdapter bluetoothAdapter;
    private Set<String> bluetooths = null;
    private MyBroadcastReceiverBackground myBroadcastReceiver = null;
    private boolean open = true;
    @Nullable
    @Override
    public void onCreate() {
        super.onCreate();
       bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        myBroadcastReceiver =  new MyBroadcastReceiverBackground();
        this.registerReceiver(myBroadcastReceiver, intentFilter);
       Log.i(TAG ,"----- onCreate() ---");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("后台执行--------------------");
        open = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> set = readBluetooth(getApplicationContext());
                if(set==null||set.size()<1){
                    return;
                }
                while(open){
                    Log.i(TAG,"后台正在执行:"+open);
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    bluetoothAdapter.startDiscovery();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG ,"----- onBind() ---");
        return null;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG ,"----- onRebind() ---");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG ,"----- onUnbind() ---");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG ,"----- onDestroy() ---");
        open=false;
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }
    public Set<String> readBluetooth(Context applicationContext) {
        SharedPreferences sp = applicationContext.getSharedPreferences("myfile", applicationContext.MODE_PRIVATE);
        return sp.getStringSet("checkBluetoothAddress", new HashSet<String>());
    }
}
