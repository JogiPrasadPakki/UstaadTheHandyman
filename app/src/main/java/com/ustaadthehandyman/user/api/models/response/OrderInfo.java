package com.ustaadthehandyman.user.api.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderInfo {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("ServiceID")
    @Expose
    private Integer serviceID;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("BookingTime")
    @Expose
    private Integer bookingTime;
    @SerializedName("Timestamp")
    @Expose
    private Integer timestamp;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("Mobile")
    @Expose
    private String mobile;
    @SerializedName("Price")
    @Expose
    private Integer price;
    @SerializedName("ClearedTime")
    @Expose
    private Integer clearedTime;
    @SerializedName("UserName")
    @Expose
    private String UserName;
    
    public Integer getId() {
    return id;
    }
    
    public void setId(Integer id) {
    this.id = id;
    }
    
    public Integer getServiceID() {
    return serviceID;
    }
    
    public void setServiceID(Integer serviceID) {
    this.serviceID = serviceID;
    }
    
    public String getDescription() {
    return description;
    }
    
    public void setDescription(String description) {
    this.description = description;
    }
    
    public Integer getStatus() {
    return status;
    }
    
    public void setStatus(Integer status) {
    this.status = status;
    }
    
    public Integer getBookingTime() {
    return bookingTime;
    }
    
    public void setBookingTime(Integer bookingTime) {
    this.bookingTime = bookingTime;
    }
    
    public Integer getTimestamp() {
    return timestamp;
    }
    
    public void setTimestamp(Integer timestamp) {
    this.timestamp = timestamp;
    }
    
    public String getAddress() {
    return address;
    }
    
    public void setAddress(String address) {
    this.address = address;
    }
    
    public String getMobile() {
    return mobile;
    }
    
    public void setMobile(String mobile) {
    this.mobile = mobile;
    }
    
    public Integer getPrice() {
    return price;
    }
    
    public void setPrice(Integer price) {
    this.price = price;
    }
    
    public Integer getClearedTime() {
    return clearedTime;
    }
    
    public void setClearedTime(Integer clearedTime) {
    this.clearedTime = clearedTime;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}