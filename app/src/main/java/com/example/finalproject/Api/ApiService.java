// ApiService.java
package com.example.finalproject.Api;

import com.example.finalproject.models.Waste;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("/api/waste")
    Call<ResponseBody> createWaste(@Body Waste waste);

    @Multipart
    @POST("/api/upload")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part photo
    );

}
