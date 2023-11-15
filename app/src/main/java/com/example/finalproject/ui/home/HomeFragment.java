package com.example.finalproject.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog; // Import AlertDialog
import com.example.finalproject.Api.ApiClient;
import com.example.finalproject.Api.ApiService;
import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentHomeBinding;
import com.example.finalproject.models.DataResponse;
import com.example.finalproject.models.Waste;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ApiService wasteReportService;
    private MapView mapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize OSMDroid's configuration
        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // create the map
        mapView = root.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(10.5);

        // Fetch data from the server and update the map
        fetchDataAndUpdateMap();

        // Set a marker click listener
        mapView.getOverlayManager().add(
                new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                // Show modal when a marker is clicked
                showMarkerDetailsModal(marker);
                return false;
            }
        });







        return root;
    }

    private void fetchDataAndUpdateMap() {
        wasteReportService = ApiClient.getApiService();
        Call<DataResponse> call = wasteReportService.getWasteData();
        call.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataResponse dataResponse = response.body();
                    List<Waste> dataList = dataResponse.getData();
                    updateMapWithMarkers(dataList);
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("Retrofit onFailure: " + t.getMessage());
            }
        });
    }

    private void updateMapWithMarkers(List<Waste> dataList) {
        for (Waste data : dataList) {
            GeoPoint dataPoint = new GeoPoint(data.getLatitude(), data.getLongitude());
            Marker dataMarker = new Marker(mapView);
            dataMarker.setPosition(dataPoint);
            dataMarker.setTitle(data.getWasteType());
            dataMarker.setSnippet(data.getWeightEstimation());

            // Set the marker click listener here
            dataMarker.setOnMarkerClickListener((marker, mapView) -> {
                // Show modal when a marker is clicked
                showMarkerDetailsModal(marker);
                return false;
            });


            mapView.getOverlays().add(dataMarker);
        }
    }

    // Method to show the marker details modal
    private void showMarkerDetailsModal(Marker marker) {
        // Create a custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.marker_details_dialog, null);

        // Set up the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle(marker.getTitle()) // Set the title from the marker
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
