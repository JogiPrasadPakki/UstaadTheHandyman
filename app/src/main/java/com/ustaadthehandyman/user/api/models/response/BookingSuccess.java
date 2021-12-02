package com.ustaadthehandyman.user.api.models.response;
/**
 * Created by Jogi Prasad Pakki on 29-Jun-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingSuccess {

    @SerializedName("BookingId")
    @Expose
    private Integer bookingId;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
}