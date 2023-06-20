package com.example.bus21.ui.home;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bus21.BottomSheet;
import com.example.bus21.DatabaseHelper;
import com.example.bus21.R;
import com.example.bus21.databinding.FragmentHomeBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private HomeViewModel homeViewModel;
    private int ACCESS_LOCATION_REQUEST_CODE = 1001;
    private Marker mMRTQuezonAve, mNepaQmart, mBagongBarrio, mMRTSantolan, mMRTOrtigas, mMRTGuadalupe, mMRTNorthAve, mLRTBalintawak, mKalingin, mMonumento
            , mMunoz, mMainAvenue, mAyalaAvenue, mRoxas, mMOA,mTaft,mPITX;
    private static final String TAG = "HomeFragment";
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String CONTACT_KEY = "contact_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    public static final String MARKERTITLE_KEY = "markertitle_key";

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String scontact, stitle;

    public HomeFragment() {
        super();
    }

    GoogleMap map;
    private TextView mLatitude, mLongtitude;
    private GeofencingClient geofencingClient;
    private float GEOFENCERADIUS = 300;
    private GeoFenceHelper geofenceHelper;
    String nLocation, aLocation, Contacts1, Contacts2, Contacts3, mLat, mLongt;
    DatabaseHelper db;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceHelper = new GeoFenceHelper(getActivity());

        sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        scontact = sharedpreferences.getString(MARKERTITLE_KEY, null);

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = new DatabaseHelper(getContext());


        mLatitude = root.findViewById(R.id.mLatitude);
        mLongtitude = root.findViewById(R.id.mLongtitude);

        final SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);





        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == getActivity().getPackageManager().PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.setOnMapLongClickListener(this);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }

        }
        map = googleMap;
        map.setOnMarkerClickListener(this);

        LatLng MRTQuezonAve = new LatLng(14.659763, 121.0620926);
        LatLng NepaQmart = new LatLng(14.6281826, 121.0470684);
        LatLng MRTSantolan = new LatLng(14.60803, 121.05637235907872);
        LatLng MRTOrtigas = new LatLng(14.587897649999999, 121.05671625848481);
        LatLng MRTGuadalupe = new LatLng(14.5626731, 121.0437086);
        LatLng BagongBarrio = new LatLng(14.657313, 120.9972624);
        LatLng LRTBalintawak = new LatLng(14.6572026, 120.9904398);
        LatLng Kalingin = new LatLng(14.6468435, 121.0087396);
        LatLng MRTNorthAve = new LatLng(14.6537112, 121.0387264);
        LatLng Monumento = new LatLng(14.658170697191842, 120.98624255383476);
        LatLng Munoz = new LatLng(14.657935842800741, 121.02171752140161);
        LatLng MainAve = new LatLng(14.615432650113886, 121.05358044465844);
        LatLng AyalaAve = new LatLng(14.549440860733766, 121.02834844912903);
        LatLng Taft = new LatLng(14.538620543758341, 121.00188230771147);
        LatLng Roxas = new LatLng(14.538832586686908, 120.99213251317649);
        LatLng MOA = new LatLng(14.53608651975886, 120.98252748734893);
        LatLng PITX = new LatLng(14.51144809660538, 120.99174154411146);

        mMonumento = map.addMarker(new MarkerOptions()
                .position(Monumento)
                .title("Monumento"));
        mMunoz = map.addMarker(new MarkerOptions()
                .position(Munoz)
                .title("Munoz"));
        mMainAvenue = map.addMarker(new MarkerOptions()
                .position(MainAve)
                .title("Main Ave"));
        mAyalaAvenue = map.addMarker(new MarkerOptions()
                .position(AyalaAve)
                .title("Ayala Ave"));
        mTaft = map.addMarker(new MarkerOptions()
                .position(Taft)
                .title("Taft"));
        mRoxas = map.addMarker(new MarkerOptions()
                .position(Roxas)
                .title("Roxas"));
        mMOA = map.addMarker(new MarkerOptions()
                .position(MOA)
                .title("MOA"));
        mPITX = map.addMarker(new MarkerOptions()
                .position(PITX)
                .title("PITX"));
        mMRTQuezonAve = map.addMarker(new MarkerOptions()
                .position(MRTQuezonAve)
                .title("MRT Quezon Ave"));
        mNepaQmart = map.addMarker(new MarkerOptions()
                .position(NepaQmart)
                .title("Nepa Qmart"));
        mMRTSantolan = map.addMarker(new MarkerOptions()
                .position(MRTSantolan)
                .title("MRT Santolan"));
        mMRTOrtigas = map.addMarker(new MarkerOptions()
                .position(MRTOrtigas)
                .title("MRT Ortigas"));
        mMRTGuadalupe = map.addMarker(new MarkerOptions()
                .position(MRTGuadalupe)
                .title("MRT Guadalupe"));
        mBagongBarrio = map.addMarker(new MarkerOptions()
                .position(BagongBarrio)
                .title("Bagong Barrio"));
        mLRTBalintawak = map.addMarker(new MarkerOptions()
                .position(LRTBalintawak)
                .title("LRT Balintawak"));
        mKalingin = map.addMarker(new MarkerOptions()
                .position(Kalingin)
                .title("Kaingin Rd"));
        mMRTNorthAve = map.addMarker(new MarkerOptions()
                .position(MRTNorthAve)
                .title("MRT North Ave"));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MRTQuezonAve, 12));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {


            @Override
            public void onInfoWindowClick(Marker arg0) {


            }

        });


    }

    private void enableuserlocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == getActivity().getPackageManager().PERMISSION_GRANTED) {
                enableuserlocation();
            } else {

            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void message(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }



    @Override
    public void onMapLongClick(LatLng latLng) {
        map.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCERADIUS);
        addGeoFence(latLng, GEOFENCERADIUS);
    }

    private void addGeoFence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getgeofence(GEOFENCE_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.geofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Geofence Added...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = geofenceHelper.getErrorString(e);
                Log.d(TAG, "onFailure: " + errorMessage);
            }
        });
    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        map.addMarker(markerOptions);
    }
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        map.addCircle(circleOptions);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mBagongBarrio)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();

        } else if (marker.equals(mKalingin)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        } else if (marker.equals(mLRTBalintawak)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        } else if (marker.equals(mMRTGuadalupe)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        } else if (marker.equals(mMRTNorthAve)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        } else if (marker.equals(mMRTOrtigas)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        } else if (marker.equals(mMRTQuezonAve)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }else if (marker.equals(mMRTSantolan)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        } else if (marker.equals(mNepaQmart)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }else if (marker.equals(mMonumento)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }
        else if (marker.equals(mMunoz)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }
        else if (marker.equals(mMainAvenue)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }
        else if (marker.equals(mAyalaAvenue)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }
        else if (marker.equals(mTaft)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }
        else if (marker.equals(mRoxas)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }
        else if (marker.equals(mMOA)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }
        else if (marker.equals(mPITX)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
            String title = marker.getTitle().trim();

            sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            stitle = title;

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(MARKERTITLE_KEY, stitle);

            // to save our data with key and value.
            editor.apply();
        }

        return false;
    }
}