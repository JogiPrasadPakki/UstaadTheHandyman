package com.ustaadthehandyman.user.models;

/**
 * Created by Jogi Prasad Pakki on 16-May-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public class DBAddress {

    private int id;
    private String name,mobile,house,strret,address;
    private double latitude,longitude;
    public DBAddress() {

    }

    public DBAddress(int id, String name, String mobile, String house, String street, String address, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.house = house;
        this.strret = street;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DBAddress(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getStrret() {
        return strret;
    }

    public void setStrret(String strret) {
        this.strret = strret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
