package com.example.lh.startqd;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lh.startqd.model.Bluetooth;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothAdapter bluetoothAdapter;
    private Set<String> bluetooths = null;
    private LinearLayout listNew = null;
    private LinearLayout listCheck = null;
    private static final int REQUEST_FINE_LOCATION = 100;
    private Button newBluetoothButton = null;
    private Button checkBluetoothButton = null;
    private static final String TAG = "MainActivity";
    private MyBroadcastReceiver myBroadcastReceiver = null;
    private boolean open = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listNew = findViewById(R.id.list_view_new);
        listCheck = findViewById(R.id.list_view_check);
        checkMyPermission();
        init();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        myBroadcastReceiver =  new MyBroadcastReceiver(this);
        this.registerReceiver(myBroadcastReceiver, intentFilter);
        findViewById(R.id.searchBluetooth).setOnClickListener(this);
        findViewById(R.id.startQd).setOnClickListener(this);
        newBluetoothButton = findViewById(R.id.new_bluetooth);
        newBluetoothButton.setOnClickListener(this);
        checkBluetoothButton = findViewById(R.id.check_bluetooth);
        checkBluetoothButton.setOnClickListener(this);
        startScanBluetooth(5000);
       // startService(new Intent(this,MyService.class));
    }

    private void init() {
        Set<String> set = readBluetooth();
        if(set!=null&&set.size()>0){
            for (final String ar : set){
                //获取显示list样式
                final LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item, null);
                //获取list样式里名称视图
                TextView name = linearLayoutItem.findViewById(R.id.name);
                //获取list样式里地址视图
                TextView address = linearLayoutItem.findViewById(R.id.address);
                //获取list样式里信号强度
                TextView showStrong = linearLayoutItem.findViewById(R.id.show_strong);
                Button button = linearLayoutItem.findViewById(R.id.addButton);
                button.setText("移出监控");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteBluetooth(ar);
                        listCheck.removeView(linearLayoutItem);
                    }
                });
                //设置list样式里名称
                name.setText("不在范围内");
                address.setText(ar);
                showStrong.setText("0");
                listCheck.addView(linearLayoutItem);
            }
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG,"Resume-----------------");
        stopService(new Intent(this,MyService.class));
        open = true;
        startScanBluetooth(5000);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG,"pause-----------------");
        startService(new Intent(this,MyService.class));
        super.onPause();
    }
    @Override
    protected void onStop() {
        Log.i(TAG,"stop------------------");
        super.onStop();
    }
    private void checkMyPermission() {
        int checkCallPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkCallPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(MainActivity.this, "请求开启定位权限", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    /**
     * 扫描蓝牙设备
     */
    public void startScanBluetooth(final long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(open){
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    bluetoothAdapter.startDiscovery();
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    private void searchBluetooth() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        int checkCallPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkCallPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "请求开启定位权限", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
        }
        Toast.makeText(this, "开始搜索蓝牙设备", Toast.LENGTH_SHORT).show();
        bluetooths = readBluetooth();
        Log.i(TAG, bluetooths.toString());
        bluetoothAdapter.startDiscovery();
    }

    private void startQD() {
        final Intent intent = new Intent();
        intent.setClassName("com.qding.community", "com.qding.community.global.opendoor.ShortcutOpenDoorActivity");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (R.id.searchBluetooth == v.getId()) {
            searchBluetooth();
        } else if (R.id.startQd == v.getId()) {
            startQD();
        } else if (R.id.new_bluetooth == v.getId()) {
            listNew.setVisibility(View.VISIBLE);
            listCheck.setVisibility(View.GONE);
            newBluetoothButton.setTextColor(Color.parseColor("#FFFFFF"));
            newBluetoothButton.setBackgroundColor(Color.parseColor("#676A6C"));
            checkBluetoothButton.setTextColor(Color.parseColor("#000000"));
            checkBluetoothButton.setBackgroundColor(Color.parseColor("#D6D7D7"));
        } else if (R.id.check_bluetooth == v.getId()) {
            listNew.setVisibility(View.GONE);
            listCheck.setVisibility(View.VISIBLE);
            checkBluetoothButton.setTextColor(Color.parseColor("#FFFFFF"));
            checkBluetoothButton.setBackgroundColor(Color.parseColor("#676A6C"));
            newBluetoothButton.setTextColor(Color.parseColor("#000000"));
            newBluetoothButton.setBackgroundColor(Color.parseColor("#D6D7D7"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "你禁止了我访问位置", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void deleteBluetooth(String address) {
        Set<String> set = readBluetooth();
        if(set==null||set.size()<1){
            return;
        }
        Set<String> newSet = new HashSet<>(set);
        int size = set.size();
        newSet.remove(address);
        if(newSet.size()!=size){
            SharedPreferences sp = getSharedPreferences("myfile", MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putStringSet("checkBluetoothAddress", newSet);
            edit.commit();
        }
    }

    private void saveBluetooth(String bluetooth) {
        Set<String> set = readBluetooth();
        if (set == null) {
            set = new HashSet<>();
        }
        set.add(bluetooth);
        SharedPreferences sp = getSharedPreferences("myfile", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putStringSet("checkBluetoothAddress", set);
        edit.commit();
    }

    public Set<String> readBluetooth() {
        SharedPreferences sp = getSharedPreferences("myfile", MODE_PRIVATE);
        return sp.getStringSet("checkBluetoothAddress", new HashSet<String>());
    }
    public void appendLayout(final Bluetooth bluetooth){
        final LinearLayout linearLayout = bluetooth.isCheck()?listCheck:listNew;
       int count = linearLayout.getChildCount();
       TextView textView = null;
       for(int i=0;i<count;i++){
           View childAt = linearLayout.getChildAt(i);
           textView = childAt.findViewById(R.id.address);
           if(bluetooth.getAddress().equals(textView.getText())){
               textView = childAt.findViewById(R.id.name);
               textView.setText(bluetooth.getName());
               textView = childAt.findViewById(R.id.show_strong);
               textView.setText(bluetooth.getStrong()+"");
               return;
           }
       }
        //获取显示list样式
        final LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item, null);
        //获取list样式里名称视图
        TextView name = linearLayoutItem.findViewById(R.id.name);
        //获取list样式里地址视图
        TextView address = linearLayoutItem.findViewById(R.id.address);
        //获取list样式里信号强度
        TextView showStrong = linearLayoutItem.findViewById(R.id.show_strong);
        //设置list样式里名称
        name.setText(bluetooth.getName());
        address.setText(bluetooth.getAddress());
        showStrong.setText(bluetooth.getStrong()+"");
        Log.i(TAG,"检测到蓝牙:"+bluetooth.getName());
        if (bluetooth.isCheck()) {
                   /*
                    Toast.makeText(MainActivity.this,"信号强度:"+rssi,Toast.LENGTH_SHORT).show();
                    searchBluetooth();*/
            Button button = linearLayoutItem.findViewById(R.id.addButton);
            button.setText("移出监控");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteBluetooth(bluetooth.getAddress());
                    linearLayout.removeView(linearLayoutItem);
                }
            });
        } else {
            linearLayoutItem.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveBluetooth(bluetooth.getAddress());
                    linearLayout.removeView(linearLayoutItem);
                }
            });
        }
        linearLayout.addView(linearLayoutItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        open = false;
        this.unregisterReceiver(myBroadcastReceiver);
    }
}