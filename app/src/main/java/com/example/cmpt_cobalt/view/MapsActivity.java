package com.example.cmpt_cobalt.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.Inspection;
import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;
    private Marker[] mMarker = new Marker[RestaurantManager.getInstance().getManagerSize()];
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RestaurantManager manager = RestaurantManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        getDeviceLocation();
        mMap.setMyLocationEnabled(true);

        // Restaurant pegs
        populateRestaurants();

        //Set Custom InfoWindow Adapter
        CustomInfoAdapter adapter = new CustomInfoAdapter(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        registerClickCallback();
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
                MapsActivity.this.startActivity(intent);
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
}

//Implementation to custom adapter (Did not work but is a good idea to implement)

/*
private class InfoAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        private Context mContext;

        public InfoAdapter(Context context) {
            mContext = context;
            mWindow = LayoutInflater.from(context).inflate(R.layout.restaurant_item, null);
        }

        private void rendowWindowText(Marker marker, View view) {
            LatLng latLng0 = marker.getPosition();
            double lat = latLng0.latitude;
            double lng = latLng0.longitude;
            Restaurant restaurant = manager.findRestaurantByLatLng(lat, lng);
            System.out.println(restaurant.toString());
            // Fill the view
            TextView restaurantNameText = findViewById(R.id.item_restaurantName);
            restaurantNameText.setText(restaurant.getName());

            ImageView logo = findViewById(R.id.item_restaurantLogo);
            logo.setImageResource(restaurant.getIcon());


            Inspection mostRecentInspection = restaurant.getInspection(0);
            if (mostRecentInspection != null) {
                TextView numNonCriticalText = findViewById(R.id.item_numNonCritical);
                numNonCriticalText.setText(Integer.toString(mostRecentInspection.getNumNonCritical()));

                TextView numCriticalText = findViewById(R.id.item_numCritical);
                numCriticalText.setText(Integer.toString(mostRecentInspection.getNumCritical()));

                TextView lastInspectionText = findViewById(R.id.item_lastInspection);
                lastInspectionText.setText(mostRecentInspection.getFormattedDate());

                ImageView hazard = findViewById(R.id.item_hazardImage);
                hazard.setImageResource(mostRecentInspection.getHazardIcon());
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            rendowWindowText(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            rendowWindowText(marker, mWindow);
            return mWindow;
        }
    }
    */

/*
@Override
    public View getInfoWindow(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);

    }

    private View prepareInfoView(Marker marker){
        Restaurant restaurant = manager.findRestaurantByLatLng(
                                            marker.getPosition().latitude,
                                            marker.getPosition().longitude);
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(MapsActivity.this);
        //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_map);
        infoImageView.setImageDrawable(drawable);
        infoView.addView(infoImageView);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoLat = new TextView(MapsActivity.this);
        subInfoLat.setText("Lat: " + marker.getPosition().latitude);
        TextView subInfoLnt = new TextView(MapsActivity.this);
        subInfoLnt.setText("Lnt: " + marker.getPosition().longitude);
        subInfoView.addView(subInfoLat);
        subInfoView.addView(subInfoLnt);
        infoView.addView(subInfoView);

        return infoView;
    }
 */