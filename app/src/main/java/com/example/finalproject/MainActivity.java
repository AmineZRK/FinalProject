package com.example.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Api.WasteReportApi;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etWasteType, etWeightEstimation, etPhotoUrl;
    private Button btnSubmit, btnGetCurrentLocation;
    private ImageView imageView;
    private double latitude, longitude;

    private WasteReportApi wasteReportApi;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        etWasteType = findViewById(R.id.etWasteType);
        etWeightEstimation = findViewById(R.id.etWeightEstimation);
        etPhotoUrl = findViewById(R.id.etPhotoUrl);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageView = findViewById(R.id.imageView);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")  // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create WasteReportApi instance
        wasteReportApi = retrofit.create(WasteReportApi.class);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitWasteReport();
            }
        });
    }

    private void submitWasteReport() {
        String wasteType = etWasteType.getText().toString();
        String weightEstimation = etWeightEstimation.getText().toString();
        String photoUrl = etPhotoUrl.getText().toString();

        // Validate input fields
        if (wasteType.isEmpty() || weightEstimation.isEmpty() || photoUrl.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a JSON object for the waste report
        JsonObject wasteReportJson = new JsonObject();
        wasteReportJson.addProperty("latitude", latitude);
        wasteReportJson.addProperty("longitude", longitude);
        wasteReportJson.addProperty("user", "JohnDoe");
        wasteReportJson.addProperty("wasteType", wasteType);
        wasteReportJson.addProperty("weightEstimation", weightEstimation);
        wasteReportJson.addProperty("photoUrl", photoUrl);

        // Call Retrofit to send the waste report
        Call<Void> call = wasteReportApi.submitWasteReport(wasteReportJson);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Waste report submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error submitting waste report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SubmitWasteReport", "Error", t);
                Toast.makeText(MainActivity.this, "Error submitting waste report", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
