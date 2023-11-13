package com.example.finalproject;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.Settings;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadImageTask extends AsyncTask<Bitmap, Void, Void> {
    private Context context;

    public UploadImageTask(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Bitmap... params) {
        String serverUrl = "http://10.0.2.2:3000/api/waste";

        try {

            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set connection properties
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data");

            // Convert Bitmap to a file
            File file = convertBitmapToFile(params[0]);

            // Open the file
            FileInputStream fileInputStream = new FileInputStream(file);

            // Open a data output stream to write the file contents
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }

            // Close streams
            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();
            System.out.println(file);
            // Get the response from the server (optional)
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
        File filesDir = context.getCacheDir();
        File file = new File(filesDir, "image.jpg");
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
}
