package com.example.finalproject.ui.dashboard;





import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.databinding.FragmentDashboardBinding;
import com.example.finalproject.Api.ApiClient;
import com.example.finalproject.Api.ApiService;
import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.UploadImageTask;
import com.example.finalproject.models.Waste;

public class DashboardFragment extends Fragment {

    private EditText etWeightEstimation;
    private Spinner spinnerWasteType;
    private Button btnGetCurrentLocation, btnTakePhoto, btnSubmit;
    private ImageView imageView;
    private Bitmap photoBitmap;
    private ApiService wasteReportService;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 2;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude, longitude;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        spinnerWasteType = root.findViewById(R.id.spinnerWasteType);
        etWeightEstimation = root.findViewById(R.id.etWeightEstimation);
        btnGetCurrentLocation = root.findViewById(R.id.btnGetCurrentLocation);
        btnTakePhoto = root.findViewById(R.id.btnTakePhoto);
        btnSubmit = root.findViewById(R.id.btnSubmit);
        imageView = root.findViewById(R.id.imageView);

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

        return root;
    }
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
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
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == -1 && data != null) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
            if (photoBitmap != null) {
                imageView.setImageBitmap(photoBitmap);
            }
            // Check location permissions here and call requestLocation()
            requestLocation();
        }
    }

    private void requestLocation() {

        // Check location permissions (request if not granted)
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);


            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
                    Toast.makeText(requireActivity(), "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
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

            // Try to get updates from both network and GPS providers
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            } else {
                // No provider available, inform the user
                Toast.makeText(requireContext(), "Location providers are not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void submitWasteReport() {
        // Check if the photoBitmap is not null
        if (photoBitmap != null) {
            // Get other information from the UI
            String selectedWasteType = spinnerWasteType.getSelectedItem().toString();
            String weightEstimation = etWeightEstimation.getText().toString();

            // Create a Waste object with the information
            Waste waste = new Waste();
            waste.setLatitude(latitude);
            waste.setLongitude(longitude);
            waste.setWasteType(selectedWasteType);
            waste.setWeightEstimation(weightEstimation);
            new UploadImageTask(requireContext()).executeTask(photoBitmap,  waste);

        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}