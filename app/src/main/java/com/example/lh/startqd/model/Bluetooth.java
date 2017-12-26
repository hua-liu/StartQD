package com.example.lh.startqd.model;

/**
 * 蓝牙信息bean
 * Created by lh on 2017/12/24.
 */

public class Bluetooth {
    private String name;
    private String address;
    private short strong;
    private boolean check;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public short getStrong() {
        return strong;
    }

    public void setStrong(short strong) {
        this.strong = strong;
    }
}
