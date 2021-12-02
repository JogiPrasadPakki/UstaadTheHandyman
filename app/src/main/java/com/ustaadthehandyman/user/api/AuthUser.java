package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.Auth;
import com.ustaadthehandyman.user.api.models.response.UserAuth;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Jogi Prasad Pakki on 06-May-18.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2019 All reserved by Jogi Prasad Pakki.
 */
public interface AuthUser {

    @POST("/api/user/login")
   Observable<Response<UserAuth>> createUser(@Body Auth auth);
}
