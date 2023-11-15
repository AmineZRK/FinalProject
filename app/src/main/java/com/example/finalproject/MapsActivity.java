package com.example.finalproject;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize OSMDroid's configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //create the map
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(18.5);

        // ======================  for loop here
        GeoPoint startPoint = new GeoPoint(48.87564052301125, 2.4108228857520624);
        mapController.setCenter(startPoint);

        Marker startMarker = new Marker(mapView);

        startMarker.setPosition(startPoint);
        startMarker.setTitle("Plastic");
        startMarker.setSnippet("2kg");
        mapView.getOverlays().add(startMarker);
        //=====================================================
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
