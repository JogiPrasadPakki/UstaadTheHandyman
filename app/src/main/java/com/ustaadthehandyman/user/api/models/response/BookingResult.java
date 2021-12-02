
package com.ustaadthehandyman.user.api.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingResult {

@SerializedName("bookingid")
@Expose
private Integer bookingid;

public Integer getBookingid() {
return bookingid;
}

public void setBookingid(Integer bookingid) {
this.bookingid = bookingid;
}

}