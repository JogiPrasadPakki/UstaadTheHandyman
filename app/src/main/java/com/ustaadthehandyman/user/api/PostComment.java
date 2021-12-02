package com.ustaadthehandyman.user.api;

import com.ustaadthehandyman.user.api.models.body.CommentBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Jogi Prasad Pakki on 03-Jul-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public interface PostComment {

    @POST("/api/order/comment")
    Call<Void> postComment(@Header("authentication")String token, @Body CommentBody comment);
}
