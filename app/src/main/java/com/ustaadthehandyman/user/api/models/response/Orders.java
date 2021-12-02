package com.ustaadthehandyman.user.api.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Orders {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("ServiceID")
    @Expose
    private String serviceID;
    @SerializedName("Status")
    @Expose
    private int status;

    @SerializedName("Description")
    @Expose
    private String Description;

    @SerializedName("BookingTime")
    @Expose
    private String BookingTime;

    public Integer getId() {
    return id;
    }

    public void setId(Integer id) {
    this.id = id;
    }

    public String getServiceID() {
    return serviceID;
    }

    public void setServiceID(String serviceID) {
    this.serviceID = serviceID;
    }

    public int getStatus() {
    return status;
    }

    public void setStatus(int status) {
    this.status = status;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getBookingTime() {
        return BookingTime;
    }

    public void setBookingTime(String bookingTime) {
        BookingTime = bookingTime;
    }
}