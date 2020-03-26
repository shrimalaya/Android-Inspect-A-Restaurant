package com.example.cmpt_cobalt.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.Inspection;
import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 20f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String EXTRA_MESSAGE = "Extra";

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Marker[] mMarker = new Marker[RestaurantManager.getInstance().getManagerSize()];
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RestaurantManager manager = RestaurantManager.getInstance();


    public static Intent makeLaunchIntent(Context c, String message) {
        Intent i1 = new Intent(c, MapsActivity.class);
        i1.putExtra(EXTRA_MESSAGE, message);
        return i1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setDefaultIntent();

        getLocationPermission();
        onButtonClick();
    }

    private void setDefaultIntent() {
        Intent i = new Intent();
        i.putExtra("result", 1);
        setResult(Activity.RESULT_OK, i);
    }

    private void onButtonClick() {
        ImageView goToList = findViewById(R.id.ic_list);

        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                intent.putExtra("result", 0);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        ImageView gpsLocation = findViewById(R.id.ic_location);
        gpsLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocationPermissionsGranted) {
                    getDeviceLocation();
                }
            }
        });
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    // Retrieved from: https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
            initMap();
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }


    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    moveCamera(new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), DEFAULT_ZOOM);
                }
            }
        });
    }

    /**
     * Move the camera according to Latitude and longitude
     * DEFAULT_ZOOM = 15
     */
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Self gps location
        if(mLocationPermissionsGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        // Restaurant pegs
        populateRestaurants();

        //Set Custom InfoWindow Adapter
        CustomInfoAdapter adapter = new CustomInfoAdapter(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        registerClickCallback();

        // Receive intent from Restaurant Activity
        Intent i_receive = getIntent();
        String resID = i_receive.getStringExtra(EXTRA_MESSAGE);
        // If valid intent, show information at marker
        if (resID != null) {
            HandleReceivingCoordinates(resID);
        }
    }

    private void HandleReceivingCoordinates(String resID) {
        Restaurant goToRes = null;
        boolean found = false;
        int i = 0;
        for (Restaurant temp : manager.getRestaurants()) {
            if (resID.equals(temp.getTracking())) {
                goToRes = temp;
                found = true;
                break;
            }
            i++;
        }

        if(found) {
            moveCamera(new LatLng(goToRes.getLatAddress(),
                    goToRes.getLongAddress()), DEFAULT_ZOOM);
            mMarker[i].showInfoWindow();
            moveCamera(new LatLng(goToRes.getLatAddress(),
                    goToRes.getLongAddress()), DEFAULT_ZOOM);
        }
    }

    private void registerClickCallback() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Find the restaurant to work with.
                LatLng latLng0 = marker.getPosition();
                double lat = latLng0.latitude;
                double lng = latLng0.longitude;
                Restaurant restaurant = manager.findRestaurantByLatLng(lat, lng);

                String message = restaurant.toString();
                Intent intent = RestaurantActivity.makeLaunchIntent(MapsActivity.this, "RestaurantActivity");
                intent.putExtra("Extra", message);
                MapsActivity.this.startActivityForResult(intent, 451);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                moveCamera(marker.getPosition(), DEFAULT_ZOOM);
                marker.showInfoWindow();
                return true;
            }
        });
    }

    private void populateRestaurants() {
        RestaurantManager manager = RestaurantManager.getInstance();
        MarkerOptions options;
        int i=0;
        for (Restaurant restaurant: manager) {
            options = new MarkerOptions().
                    position(new LatLng(restaurant.getLatAddress(),
                            restaurant.getLongAddress())).
                    title(restaurant.getName());

            mMarker[i++] = mMap.addMarker(options);

            Inspection mostRecentInspection = restaurant.getInspection(0);
            if (mostRecentInspection != null) {
                if (mostRecentInspection.getHazardRating().equals("\"Low\"")) {
                    mMarker[i - 1].setIcon(bitmapDescriptorFromVector(this, R.drawable.peg_green));
                } else if (mostRecentInspection.getHazardRating().equals("\"Moderate\"")) {
                    mMarker[i - 1].setIcon(bitmapDescriptorFromVector(this, R.drawable.peg_yellow));
                } else {
                    mMarker[i - 1].setIcon(bitmapDescriptorFromVector(this, R.drawable.peg_red));
                }
            }
            else
            {
                mMarker[i - 1].setIcon(bitmapDescriptorFromVector(this, R.drawable.peg_no_inspection));
            }
            moveCamera(new LatLng(restaurant.getLatAddress(),
                    restaurant.getLongAddress()), DEFAULT_ZOOM);
        }
    }

    private class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {

        private Activity context;

        public CustomInfoAdapter(Activity context){
            this.context = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View itemView = context.getLayoutInflater().inflate(R.layout.map_infowindow_layout, null);

            // Find the restaurant to work with.
            LatLng latLng0 = marker.getPosition();
            double lat = latLng0.latitude;
            double lng = latLng0.longitude;
            Restaurant restaurant = manager.findRestaurantByLatLng(lat, lng);

            // Fill the view
            ImageView logo = itemView.findViewById(R.id.info_item_restaurantLogo);
            logo.setImageResource(restaurant.getIcon());

            TextView restaurantNameText = itemView.findViewById(R.id.info_item_restaurantName);
            restaurantNameText.setText(restaurant.getName());


            Inspection mostRecentInspection = restaurant.getInspection(0);
            if (mostRecentInspection != null) {
                TextView numNonCriticalText = itemView.findViewById(R.id.info_item_numNonCritical);
                numNonCriticalText.setText(Integer.toString(mostRecentInspection.getNumNonCritical()));

                TextView numCriticalText = itemView.findViewById(R.id.info_item_numCritical);
                numCriticalText.setText(Integer.toString(mostRecentInspection.getNumCritical()));

                TextView lastInspectionText = itemView.findViewById(R.id.info_item_lastInspection);
                lastInspectionText.setText(mostRecentInspection.getFormattedDate());

                ImageView hazard = itemView.findViewById(R.id.info_item_hazardImage);
                hazard.setImageResource(mostRecentInspection.getHazardIcon());

            }

            return itemView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 451:
                String resID = data.getStringExtra("resID");
                int answer = data.getIntExtra("result", 0);

                if (answer == 1) {
                    HandleReceivingCoordinates(resID);
                }
                break;
        }
    }
}