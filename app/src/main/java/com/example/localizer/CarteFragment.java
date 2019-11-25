package com.example.localizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.LoadSceneCallback;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.SceneError;

public class CarteFragment extends Fragment {

    private MapViewLite mapView;
    private PermissionsRequestor permissionsRequestor;

    public CarteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_carte, container, false);

        mapView = v.findViewById(R.id.carteAffiche);
        mapView.onCreate(savedInstanceState);
        handleAndroidPermissions();

        return v;
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(getActivity());
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e("main: ", "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable SceneError sceneError) {
                if (sceneError == null) {
                    mapView.getCamera().setTarget(new GeoCoordinates(52.530932, 13.384915));
                    mapView.getCamera().setZoomLevel(14);
                } else {
                    Log.d("main: ", "onLoadScene failed: " + sceneError.toString());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mapView != null)
            mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mapView != null)
            mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mapView != null)
            mapView.onDestroy();
    }

}
