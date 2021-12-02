package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.BodyPlaceOrder;
import com.ustaadthehandyman.user.api.models.response.BookingSuccess;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Jogi Prasad Pakki on 06-May-18.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2019 All reserved by Jogi Prasad Pakki.
 */
public interface PlaceOrder {
    @POST("/api/user/placeorder")
    Call<BookingSuccess> makeOrder(@Header("authentication")String token, @Body BodyPlaceOrder body);
}
