package com.app.sampleapp.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

//    @Headers("Content-Type: application/json; charset=utf-8")

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadVideo(@Part MultipartBody.Part filePart);

}
