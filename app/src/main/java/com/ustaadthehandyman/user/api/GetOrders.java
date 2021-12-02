package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.UserId;
import com.ustaadthehandyman.user.api.models.response.Orders;
import com.ustaadthehandyman.user.api.models.response.OrdersList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by Jogi Prasad Pakki on 26-Jun-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public interface GetOrders {

    @POST("/api/user/orders")
    Call<List<Orders>> getOrder(@Header("authentication") String token, @Body UserId user);
}
