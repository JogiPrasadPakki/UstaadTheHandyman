package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.Feedback;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

/**
 * Created by Jogi Prasad Pakki on 15-May-18.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018 All reserved by Jogi Prasad Pakki.
 */
public interface SendFeedback {

    @PATCH("/api/user/feedback")
    Call<Void> sendFeedback(@Header("authentication") String token, @Body Feedback feedback);
}
