package com.ustaadthehandyman.user.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Jogi Prasad Pakki on 24-Jun-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public interface UpdateUser {



    @Multipart
    @POST("/api/user/update")
    Call<ResponseBody> UpdateUser(@HeaderMap Map<String,String> token, @Query("uid") String Uid,
                                  @Query("name") String fullName,
                                  @Part MultipartBody.Part image
    );
}
