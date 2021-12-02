package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.LastSeen;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;

/**
 * Created by Jogi Prasad Pakki on 27-Jun-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public interface LastActive {
    @PATCH("/api/user/lastlogin")
    Call<Void> LastSeen(@Header("authentication") String token, @Body LastSeen lastSeen);
}
