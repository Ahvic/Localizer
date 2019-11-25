package com.example.localizer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.here.sdk.core.GeoCircle;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Point2D;
import com.here.sdk.gestures.GestureState;
import com.here.sdk.gestures.LongPressListener;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.LoadSceneCallback;
import com.here.sdk.mapviewlite.MapCircle;
import com.here.sdk.mapviewlite.MapCircleStyle;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PixelFormat;
import com.here.sdk.mapviewlite.SceneError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class CarteFragment extends Fragment {

    private MapViewLite mapView;
    private PermissionsRequestor permissionsRequestor;
    LocationManager locationManager;
    LocationProvider provider;
    LocationListener locationListener;
    private Location currentBestLocation = null;
    private ArrayList<MapCircle> myC;
    private MapImage mapImage;
    private MapMarker mapMarker = new MapMarker(new GeoCoordinates(5.0,5.0));
    Double myLat = 5.0;
    Double myLong = 5.0;
    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public CarteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myC = new ArrayList<MapCircle>();
        View v = inflater.inflate(R.layout.fragment_carte, container, false);
        mapView = v.findViewById(R.id.carteAffiche);
        mapView.onCreate(savedInstanceState);
        checkPermissions();
        mapImage = MapImageFactory.fromResource(mapView.getResources(), R.drawable.loupe);
        mapMarker.addImage(mapImage, new MapMarkerImageStyle());
        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarker.setVisible(true);
        setLongPressGestureHandler(mapView);
        return v;

    }

    private void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(getContext(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void loadMapScene(Location loc) {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable SceneError sceneError) {
                if (sceneError == null) {
                    mapView.getCamera().setTarget(new GeoCoordinates(loc.getLatitude(),loc.getLongitude()));
                    myLat = loc.getLatitude();
                    myLong =loc.getLongitude();
                    mapView.getCamera().setZoomLevel(19);
                } else {
                    Log.d("main: ", "onLoadScene failed: " + sceneError.toString());
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        getActivity().finish();
                        return;
                    }
                }
                locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                String provider = locationManager.getBestProvider(criteria, true);

                if(provider != null) {
                    locationManager.requestLocationUpdates(provider, 2 * 60 * 1000, 10, locationListenerGPS);
                    while(currentBestLocation == null) {
                        currentBestLocation = locationManager.getLastKnownLocation(provider);
                        loadMapScene(currentBestLocation);
                    }
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mapView != null)
            mapView.onResume();
            for(MapCircle m: myC){
                mapView.getMapScene().addMapCircle(m);
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mapView != null)
            mapView.onDestroy();
    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
            mapView.getCamera().setTarget(new GeoCoordinates(latitude,longitude));
            mapView.getCamera().setZoomLevel(19);
            mapMarker.setCoordinates(new GeoCoordinates(latitude,longitude));
            myLat = latitude;
            myLong =longitude;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private MapCircle createMapCircle(GeoCoordinates geo,float radiusInMeters,long l) {
        GeoCircle geoCircle = new GeoCircle(geo, radiusInMeters);
        MapCircleStyle mapCircleStyle = new MapCircleStyle();
        mapCircleStyle.setFillColor(l, PixelFormat.RGBA_8888);
        MapCircle mapCircle = new MapCircle(geoCircle, mapCircleStyle);
        myC.add(mapCircle);
        return mapCircle;
    }

    private void setLongPressGestureHandler(MapViewLite mapView) {
        mapView.getGestures().setLongPressListener(new LongPressListener() {
            @Override
            public void onLongPress(@NonNull GestureState gestureState, @NonNull Point2D touchPoint) {
                GeoCoordinates geoCoordinates = mapView.getCamera().viewToGeoCoordinates(touchPoint);
                if (gestureState == GestureState.BEGIN) {
                    Log.d(TAG, "LongPress detected at: " + geoCoordinates);
                }

                if (gestureState == GestureState.UPDATE) {
                    Log.d(TAG, "LongPress update at: " + geoCoordinates);
                }

                if (gestureState == GestureState.END) {
                    Log.d(TAG, "LongPress finger lifted at: " + geoCoordinates);
                    testCreation(new GeoCoordinates(myLat,myLong));
                }
            }
        });
    }


    public void testCreation(GeoCoordinates geo){
        Intent i = new Intent(getActivity(), CreationActivity.class);
        i.putExtra(CreationActivity.EXTRA_CoordN, geo.latitude);
        i.putExtra(CreationActivity.EXTRA_CoordO, geo.longitude);
        startActivityForResult(i, 0);
        MapCircle mapCircle = createMapCircle(geo,30, 0x9999);
        mapView.getMapScene().addMapCircle(mapCircle);
    }

}