package com.example.finalproject.Api;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
    public interface WasteReportApi {
        @POST("/wasteReports")
        Call<Void> submitWasteReport(@Body JsonObject wasteReport);
    }


