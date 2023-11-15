package com.example.finalproject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.Settings;

import com.example.finalproject.Api.ApiService;
import com.example.finalproject.models.Waste;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadImageTask extends AsyncTask<Object, Void, Void> {

    private Context applicationContext;

    public UploadImageTask(Context context) {
        // Use application context to avoid memory leaks
        this.applicationContext = context.getApplicationContext();
    }
    @Override
    protected Void doInBackground(Object... params) {
        // Move your network code here
        String serverUrl = "https://6f0c-178-16-174-77.ngrok-free.app/api/";

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
            // Extract parameters from the params array
            Bitmap photoBitmap = (Bitmap) params[0];
            Waste waste = (Waste) params[1];
            // Convert Bitmap to a file
            File file = convertBitmapToFile(photoBitmap);
            System.out.println(file);
            // Determine the media type of the image
            String mediaType = getMediaType(file);
            // Create a request body with the file
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);

            // Create a MultipartBody.Part from the file
            MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
            // Convert other information to RequestBody
            RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(waste.getLatitude()));
            RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(waste.getLongitude()));
            RequestBody wasteType = RequestBody.create(MediaType.parse("text/plain"), waste.getWasteType());
            RequestBody weightEstimation = RequestBody.create(MediaType.parse("text/plain"), waste.getWeightEstimation());
            RequestBody User = RequestBody.create(MediaType.parse("text/plain"), "MyUser");
            // Call the API service method to upload the image
            Call<ResponseBody> call = apiService.upload(photoPart, latitude, longitude, wasteType, weightEstimation, User);
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
    private File convertBitmapToFile(Bitmap bitmap) throws IOException {
        File filesDir = applicationContext.getCacheDir();
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

    public void executeTask(Object... bitmaps) {
        // Execute the task using executeOnExecutor
        // THREAD_POOL_EXECUTOR allows parallel execution
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bitmaps);
    }
}
