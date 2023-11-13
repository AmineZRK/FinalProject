// MainActivity.java
package com.example.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalproject.Api.ApiClient;
import com.example.finalproject.Api.ApiService;
import com.example.finalproject.models.Waste;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etWasteType, etWeightEstimation, etPhotoUrl;
    private Button btnGetCurrentLocation, btnTakePhoto, btnSubmit;
    private ImageView imageView;
    private Bitmap photoBitmap;
    private ApiService wasteReportService;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 2;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etWasteType = findViewById(R.id.etWasteType);
        etWeightEstimation = findViewById(R.id.etWeightEstimation);
        etPhotoUrl = findViewById(R.id.etPhotoUrl);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageView = findViewById(R.id.imageView);

        wasteReportService = ApiClient.getApiService();

        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWasteReport();
            }
        });
    }

    private void requestLocation() {
        // Check location permissions (request if not granted)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(MainActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Handle location provider status changes
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // Handle location provider enabled
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // Handle location provider disabled
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
            if (photoBitmap != null) {
                imageView.setImageBitmap(photoBitmap);
            }
        }
    }
    private class UploadImageTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            // Move your network code here
            String serverUrl = "http://10.0.2.2:3000/api/";

            try {
                // Create OkHttpClient with the interceptor
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        // Add other interceptors if needed
                        .build();

                // Build Retrofit instance with the OkHttpClient
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(serverUrl)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Create a service interface for your API
                ApiService apiService = retrofit.create(ApiService.class);

                // Convert Bitmap to a file
                File file = convertBitmapToFile(params[0]);
                System.out.println(file);
                // Determine the media type of the image
                String mediaType = getMediaType(file);
                // Create a request body with the file
                RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);

                // Create a MultipartBody.Part from the file
                MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

                // Call the API service method to upload the image
                Call<ResponseBody> call = apiService.upload(photoPart);
                Response<ResponseBody> response = call.execute();

                // Check the response
                if (response.isSuccessful()) {
                    System.out.println("okkkk");
                } else {
                    System.out.println("not   okkkk");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        private String getMediaType(File file) {
            // Determine the media type based on the file extension or content
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            switch (extension) {
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                // Add more cases if needed for other image formats
                default:
                    return "application/octet-stream"; // fallback to generic binary data
            }
        }
        @Override
        protected void onPostExecute(Void result) {
            // Handle the result if needed
        }

        public void executeTask(Bitmap... bitmaps) {
            // Execute the task using executeOnExecutor
            // THREAD_POOL_EXECUTOR allows parallel execution
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bitmaps);
        }
    }


    private File convertBitmapToFile(Bitmap bitmap) throws IOException {
        File filesDir = getCacheDir();
        File file = new File(filesDir, "photo.jpeg");
        file.createNewFile();

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        // Write the bytes in file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        return file;
    }
    /*private void submitWasteReport() {
        String wasteType = etWasteType.getText().toString();
        String weightEstimation = etWeightEstimation.getText().toString();
        String photoUrl = etPhotoUrl.getText().toString();

        Waste request = new Waste();
        request.setWasteType(wasteType);
        request.setWeightEstimation(weightEstimation);
        request.setPhotoUrl(photoUrl);
        request.setUser("Amine");
        // Include location data in the request
        request.setLatitude(latitude);
        request.setLongitude(longitude);

        // Check if the photoBitmap is not null
        if (photoBitmap != null) {
            // Convert Bitmap to File
            File photoFile = convertBitmapToFile(photoBitmap);

            // Create a request body with the file
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);

            // Create a multipart part from the file
            MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", photoFile.getName(), requestFile);
            // Create a list to hold the photo parts
            List<MultipartBody.Part> photosList = new ArrayList<>();
            photosList.add(photoPart);

            // Set the list of photo parts in the request
            request.setPhotos(photosList);
        }

        Call<ResponseBody> call = wasteReportService.createWaste(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("SubmitWasteReport", "Response code: " + response.code());
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Waste report submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to submit waste report: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("SubmitWasteReport", "Error: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/



    private void submitWasteReport() {
        // Check if the photoBitmap is not null
        if (photoBitmap != null) {
            new UploadImageTask().executeTask(photoBitmap);

    }
    }
}
