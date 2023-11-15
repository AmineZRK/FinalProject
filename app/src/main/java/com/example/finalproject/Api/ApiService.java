// ApiService.java
package com.example.finalproject.Api;

import com.example.finalproject.models.DataResponse;
import com.example.finalproject.models.Waste;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @GET("/api/waste")
    Call<DataResponse> getWasteData();
    @POST("/api/waste")
    Call<ResponseBody> createWaste(@Body Waste waste);

    @Multipart
    @POST("/api/upload")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part photo,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("wasteType") RequestBody wasteType,
            @Part("weightEstimation") RequestBody weightEstimation,
            @Part("user") RequestBody User
    );


}
