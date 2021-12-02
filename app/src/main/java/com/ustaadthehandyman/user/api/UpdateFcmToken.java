package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.UpdateFCM;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

/**
 * Created by Jogi Prasad Pakki on 04-Jul-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public interface UpdateFcmToken {

    @PATCH("/api/user/fcm/update")
    Observable<Response<Void>> update(@Header("authentication") String authToken, @Body UpdateFCM updateFCM);
}
