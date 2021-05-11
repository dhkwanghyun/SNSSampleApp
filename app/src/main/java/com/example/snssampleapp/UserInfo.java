package com.example.snssampleapp;

import android.widget.EditText;

public class UserInfo {
    private String name;
    private String phone;
    private String birthDay;
    private String address;
    private String photoUrl;

    public UserInfo(String name, String phone, String birthDay, String address){
        this.name = name;
        this.phone = phone;
        this.birthDay = birthDay;
        this.address = address;
    }

    public UserInfo(String name, String phone, String birthDay, String address, String photoUrl){
        this.name = name;
        this.phone = phone;
        this.birthDay = birthDay;
        this.address = address;
        this.photoUrl = photoUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() { return photoUrl; }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
