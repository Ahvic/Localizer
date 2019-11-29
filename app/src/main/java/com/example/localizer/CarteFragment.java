package com.example.localizer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.fragment.app.FragmentPagerAdapter;

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
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PickMapItemsCallback;
import com.here.sdk.mapviewlite.PickMapItemsResult;
import com.here.sdk.mapviewlite.PixelFormat;
import com.here.sdk.mapviewlite.SceneError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class CarteFragment extends Fragment {

    private MapViewLite mapView;
    LocationManager locationManager;
    private Location currentBestLocation = null;
    private ArrayList<MapCircle> myC;
    private MapImage mapImage;
    private MapMarker mapMarker = new MapMarker(new GeoCoordinates(5.0,5.0));
    Double myLat = 5.0;
    Double myLong = 5.0;
    ListeNotes copieListeNote;

    private List<MapMarker> listeMarqueur;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

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

    public CarteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myC = new ArrayList<MapCircle>();
        listeMarqueur = new ArrayList<MapMarker>();

        View v = inflater.inflate(R.layout.fragment_carte, container, false);

        mapView = v.findViewById(R.id.carteAffiche);
        mapView.onCreate(savedInstanceState);
        checkPermissions();

        //TODO: remettre le vrai cercle
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
                    locationManager.requestLocationUpdates(provider, 5 * 60 * 1000, 10, locationListenerGPS);
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

        if (mapView != null) {
            mapView.onResume();
            for (MapCircle m : myC) {
                mapView.getMapScene().addMapCircle(m);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mapView != null)
            mapView.onDestroy();
    }

    public void majMarqueur(ListeNotes ln)
    {
        for(int i = 0; i < ln.size(); i++)
        {
            Note n = ln.get(i);
            GeoCoordinates geo = new GeoCoordinates(n.getCoordN(), n.getCoordO());

            MapMarker mapMarker = new MapMarker(geo);

            //Ajout des données pour retrouver la note associé quand on clic dessus
            Metadata meta = new Metadata();
            meta.setString("nomNote", n.getTitre());
            mapMarker.setMetadata(meta);

            MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
            mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5F, 1));

            // TODO; remettre un vrai marqueur
            MapImage mapImage = MapImageFactory.fromResource(getResources(), R.drawable.icons8location);
            mapMarker.addImage(mapImage, mapMarkerImageStyle);

            mapView.getMapScene().addMapMarker(mapMarker);
            listeMarqueur.add(mapMarker);
        }

        copieListeNote = ln;
    }

    private MapCircle createMapCircle(GeoCoordinates geo,float radiusInMeters,long l) {
        GeoCircle geoCircle = new GeoCircle(geo, radiusInMeters);
        MapCircleStyle mapCircleStyle = new MapCircleStyle();
        mapCircleStyle.setFillColor(l, PixelFormat.RGBA_8888);
        MapCircle mapCircle = new MapCircle(geoCircle, mapCircleStyle);
        myC.add(mapCircle);
        return mapCircle;
    }

    private void setLongPressGestureHandler(MapViewLite mapView) {
        //Si on appuye longtemps
        mapView.getGestures().setLongPressListener(new LongPressListener() {
            @Override
            public void onLongPress(@NonNull GestureState gestureState, @NonNull Point2D touchPoint) {
                GeoCoordinates geoCoordinates = mapView.getCamera().viewToGeoCoordinates(touchPoint);

                if (gestureState == GestureState.END) {
                     CreationNote(geoCoordinates);
                }
            }
        });

        //Si on appuye à peine
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(Point2D touchPoint) {
                pickMapMarker(touchPoint);
            }
        });
    }

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 10;
        mapView.pickMapItems(touchPoint, radiusInPixel, new PickMapItemsCallback() {
            @Override
            public void onMapItemsPicked(@Nullable PickMapItemsResult pickMapItemsResult) {
                if (pickMapItemsResult == null) {
                    return;
                }

                MapMarker topmostMapMarker = pickMapItemsResult.getTopmostMarker();
                if (topmostMapMarker == null) {
                    return;
                }

                Note noteAffiche = null;

                Metadata metadata = topmostMapMarker.getMetadata();
                if (metadata != null) {
                    String string = metadata.getString("nomNote");
                    if (string != null) {
                        String nomNote = string;

                        if (copieListeNote != null) {
                            Log.e("sdkbsdkhvsdivgsdg", "ICICICICICICICI");

                            for (int i = 0; i < copieListeNote.size(); i++) {
                                if (copieListeNote.get(i).getTitre().equals(nomNote))
                                    noteAffiche = copieListeNote.get(i);
                            }
                        }

                        Log.e("ClicNte",  ""+ copieListeNote.size());
                    }
                }

                Log.e("ClicNte",  String.valueOf(noteAffiche == null));

                if(noteAffiche != null){

                    Intent i = new Intent(getContext(), DetailsActivity.class);
                    i.putExtra(DetailsActivity.EXTRA_Image, noteAffiche.getImage());
                    i.putExtra(DetailsActivity.EXTRA_Titre, noteAffiche.getTitre());
                    i.putExtra(DetailsActivity.EXTRA_CoordN, noteAffiche.getCoordN());
                    i.putExtra(DetailsActivity.EXTRA_CoordO, noteAffiche.getCoordO());
                    i.putExtra(DetailsActivity.EXTRA_Contenu, noteAffiche.getContenu());

                    startActivityForResult(i, 0);

                    return;
                }
            }
        });
    }

    public void CreationNote(GeoCoordinates geo){
        Intent i = new Intent(getActivity(), CreationActivity.class);
        i.putExtra(CreationActivity.EXTRA_CoordN, geo.latitude);
        i.putExtra(CreationActivity.EXTRA_CoordO, geo.longitude);
        startActivityForResult(i, 0);

        //TODO: NE RIEN METTRE SI ON A ANNULE LA NOTE
    }

}