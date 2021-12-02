package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.UserId;
import com.ustaadthehandyman.user.api.models.response.UserInfo;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Jogi Prasad Pakki on 25-Jun-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public interface GetUser {

    @POST("/api/user/get")
    Observable<Response<List<UserInfo>>> CurrentUser(@Header("authentication") String token, @Body UserId uid);
}
