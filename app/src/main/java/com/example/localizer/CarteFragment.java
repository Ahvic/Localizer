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
import androidx.fragment.app.FragmentManager;

import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCircle;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
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
    private LocationManager locationManager;
    private Location currentBestLocation = null;
    private MapCircle mapCircle;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Les permissions obligatoires
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private ListeNotes listeNotes;
    private LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
            mapView.getCamera().setTarget(new GeoCoordinates(location.getLatitude(), location.getLongitude()));
            mapView.getCamera().setZoomLevel(19);
            mapView.getMapScene().removeMapCircle(mapCircle);
            mapCircle = createMapCircle(new GeoCoordinates(latitude,longitude), 15, 0x0C9C);
            mapView.getMapScene().addMapCircle(mapCircle);
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
    private List<MapMarker> listeMarqeur = new ArrayList<>();

    public CarteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_carte, container, false);
        mapView = v.findViewById(R.id.carteAffiche);
        mapView.onCreate(savedInstanceState);
        checkPermissions();
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
    private void loadMapScene(Location loc) {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable SceneError sceneError) {
                if (sceneError == null) {
                    mapView.getCamera().setTarget(new GeoCoordinates(loc.getLatitude(), loc.getLongitude()));
                    mapView.getCamera().setZoomLevel(19);
                    mapCircle = createMapCircle(new GeoCoordinates(loc.getLatitude(),loc.getLongitude()),15, 0x0C9C);
                    MapScene mapScene = mapView.getMapScene();
                    mapScene.addMapCircle(mapCircle);

                } else {
                    Log.d("main: ", "onLoadScene failed: " + sceneError.toString());
                }
            }
        });
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

        if (mapView != null){
            mapView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mapView != null)
            mapView.onDestroy();
    }

    private MapCircle createMapCircle(GeoCoordinates geo,float radiusInMeters,long l) {
        GeoCircle geoCircle = new GeoCircle(geo, radiusInMeters);
        MapCircleStyle mapCircleStyle = new MapCircleStyle();
        mapCircleStyle.setFillColor(l, PixelFormat.RGBA_8888);
        MapCircle mapCircle = new MapCircle(geoCircle, mapCircleStyle);

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
                    CreationNote(geoCoordinates);

                }
            }
        });
    }

    private MapMarker addPOIMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(getContext().getResources(), R.drawable.poi);

        MapMarker mapMarker = new MapMarker(geoCoordinates);

        // The bottom, middle position should point to the location.
        // By default, the anchor point is set to 0.5, 0.5.
        MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
        mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5F, 1));

        mapMarker.addImage(mapImage, mapMarkerImageStyle);
        listeMarqeur.add(mapMarker);

        return mapMarker;
    }

    public void CreationNote(GeoCoordinates geo){

        Intent i = new Intent(getActivity(), CreationActivity.class);
        i.putExtra(CreationActivity.EXTRA_CoordN, geo.latitude);
        i.putExtra(CreationActivity.EXTRA_CoordO, geo.longitude);
        startActivityForResult(i, 0);

        MapMarker marqueur = addPOIMapMarker(geo);
        mapView.getMapScene().addMapMarker(marqueur);
    }

}