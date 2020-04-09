package com.example.cmpt_cobalt.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.Inspection;
import com.example.cmpt_cobalt.model.PegItem;
import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 18f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String EXTRA_MESSAGE = "Extra";

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Marker mMarker;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RestaurantManager manager = RestaurantManager.getInstance();
    private ClusterManager<PegItem> mClusterManager;

    //Search filters
    private EditText searchField;
    private EditText violationCountField;
    private Button searchSumbitBtn;
    private Button clearBtn;
    private Button countBtn;
    private Spinner hazardSpinner;
    private Spinner comparatorSpinner;
    private CheckBox favouriteCheckBox;

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
        setupSearch();
        getLocationPermission();
        onButtonClick();
    }

    private void setupSearch() {
        setupFields();
        setupButtons();
        setupSpinners();
        setupCheckBox();
    }

    private void setupFields() {
        searchField = (EditText) findViewById(R.id.search_field);
        violationCountField = (EditText) findViewById(R.id.violation_count_field);
    }

    private void setupButtons() {
        searchSumbitBtn = (Button) findViewById(R.id.search_button);
        clearBtn = (Button) findViewById(R.id.clear_button);
        countBtn = (Button) findViewById(R.id.count_button);
        searchSumbitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSearch();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters();
            }
        });
        countBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateViolationCountRestriction();
            }
        });

    }

    private void setupSpinners() {
        hazardSpinner = (Spinner) findViewById(R.id.hazard_spinner);
        ArrayAdapter<CharSequence> hazardAdapter = ArrayAdapter.createFromResource(this,
                R.array.hazard_level_array, android.R.layout.simple_spinner_dropdown_item);
        hazardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hazardSpinner.setAdapter(hazardAdapter);
        hazardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.setHazardLevelFilter(position);
                updateMap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manager.setHazardLevelFilter(0);
            }
        });

        comparatorSpinner = (Spinner) findViewById(R.id.count_hazard_spinner);
        ArrayAdapter<CharSequence> comparatorAdapter = ArrayAdapter.createFromResource(this,
                R.array.comparator, android.R.layout.simple_spinner_dropdown_item);
        comparatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comparatorSpinner.setAdapter(comparatorAdapter);
        comparatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.setComparator(position);
                updateMap();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manager.setComparator(0);
            }
        });
    }

    private void setupCheckBox() {
        favouriteCheckBox = findViewById(R.id.checkbox_meat);
        favouriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteCheckBox.isChecked()) manager.setFavouriteOnly(true);
                else manager.setFavouriteOnly(false);
                updateMap();
            }
        });
    }

    private void submitSearch() {
        String searchTerm = searchField.getText().toString();
        manager.setSearchTerm(searchTerm);
        updateMap();
    }

    private void updateViolationCountRestriction() {
        try{
            int limit = Integer.parseInt(violationCountField.getText().toString());
            manager.setViolationLimit(limit);
            updateMap();
        }
        catch (Exception e) {}
    }

    private void clearFilters() {
        manager.setSearchTerm("");
        manager.setHazardLevelFilter(0);
        manager.setComparator(0);
        manager.setFavouriteOnly(false);
        updateMap();
    }

    private void updateMap() {
        mClusterManager.clearItems();
        mMap.clear();
        setUpClusterer();
    }

    private void setDefaultIntent() {
        Intent i = new Intent();
        i.putExtra("result", 1);
        setResult(Activity.RESULT_OK, i);
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

    // Learned from: https://www.youtube.com/watch?v=MWowf5SkiOE&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=6
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
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }
        });
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
                            currentLocation.getLongitude()), 15f);
                }
            }
        });
    }

    /**
     * Move the camera according to Latitude and longitude
     * DEFAULT_ZOOM = 15
     */
    private void moveCamera(LatLng latLng, float zoom) {
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(location);
    }

    // For peg icon
    // Learned from:https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
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
        mClusterManager = new ClusterManager<>(this, mMap);

        //Clustering
        setUpClusterer();

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
        } else {
            // Self gps location
            if(mLocationPermissionsGranted) {
                getDeviceLocation();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
    }

    private void setUpClusterer() {
        mMap.setOnCameraIdleListener(mClusterManager);
        getItems();
        mClusterManager.cluster();
        mClusterManager.setRenderer(new MarkerClusterRenderer(getApplicationContext(), mMap, mClusterManager));
    }

    private void getItems() {
        RestaurantManager manager = RestaurantManager.getInstance();
        List<Restaurant> restaurants = manager.getRestaurants();
        int i = 0;
        for (Restaurant restaurant : restaurants) {

            String temp = restaurant.getName();

            PegItem newItem = new PegItem(restaurant.getLatAddress(),
                                        restaurant.getLongAddress(),
                                        temp, getHazardIcon(restaurant));

            mClusterManager.addItem(newItem);
        }
    }

    private BitmapDescriptor getHazardIcon(Restaurant restaurant) {
        Inspection mostRecentInspection = restaurant.getInspection(0);
        BitmapDescriptor hazardIcon;
        boolean isFavourite = restaurant.getFavourite();
        if (mostRecentInspection != null) {
            String hazardLevel = mostRecentInspection.getHazardRating();

            if(isFavourite){
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_blue);
            }
            else {
                if (hazardLevel.equals("Low")) {
                    hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_green);
                } else if (hazardLevel.equals("Moderate")) {
                    hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_yellow);
                } else {
                    hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_red);
                }
            }
        }
        else
        {
            if(isFavourite){
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_blue);
            }
            else {
            hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_no_inspection); }

        }
        return hazardIcon;
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
            mClusterManager.clearItems();
            moveCamera(new LatLng(goToRes.getLatAddress(),
                    goToRes.getLongAddress()), DEFAULT_ZOOM);

            String temp = goToRes.getName();

            MarkerOptions options = new MarkerOptions().
                    position(new LatLng(goToRes.getLatAddress(),
                            goToRes.getLongAddress())).
                    title(temp);

            mMarker = mMap.addMarker(options);
            mMarker.setIcon(getHazardIcon(goToRes));
            mMarker.showInfoWindow();
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Clear everything
                mClusterManager.clearItems();

                // Clear the currently open marker
                mMap.clear();

                // Reinitialize clusterManager
                setUpClusterer();

                // Focus map on the position that was clicked on map
                moveCamera(latLng, 15f);
            }
        });

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<PegItem>() {
            @Override
            public boolean onClusterClick(Cluster<PegItem> cluster) {
                moveCamera(cluster.getPosition(), -10f);
                return true;
            }
        });

        ImageView favourites_icon = findViewById(R.id.ic_favourites);
        favourites_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFavourites();
            }
        });
    }

    private void populateFavourites() {
        mClusterManager.clearItems();
        mMap.clear();

        SharedPreferences mSharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        Set<String> favourites = new HashSet<String>(mSharedPreferences.getStringSet("Favourites", new HashSet<String>()));

        manager = RestaurantManager.getInstance();

        for(Restaurant temp: manager) {
            Gson gson = new Gson();
            String json = gson.toJson(temp);
            if (favourites.contains(json)) {
                String name = temp.getName();

                MarkerOptions options = new MarkerOptions().
                        position(new LatLng(temp.getLatAddress(),
                                temp.getLongAddress())).
                        title(name);

                mMarker = mMap.addMarker(options);
                mMarker.setIcon(getHazardIcon(temp));
                moveCamera(new LatLng(temp.getLatAddress(),
                        temp.getLongAddress()), 12f);
            }
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
            String temp = restaurant.getName();
            if(temp.length() > 25) {
                temp = temp.substring(0, 25) + "...";
            }
            restaurantNameText.setText(temp);


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

    private class MarkerClusterRenderer extends DefaultClusterRenderer<PegItem> {

        public MarkerClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<PegItem> clusterManager) {
            super(context, map, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(PegItem item, MarkerOptions markerOptions) {
            // use this to make your change to the marker option
            // for the marker before it gets render on the map
            markerOptions.icon(item.getHazard());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }
}