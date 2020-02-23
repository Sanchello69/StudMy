package com.example.studmy;


import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    ChildEventListener mChildEventListener;
    //создаем экземпляр БД и сохраняем ссылку на ветку нашей БД
    DatabaseReference mProfileRef = FirebaseDatabase.getInstance().getReference("studak/kino");

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // не воссоздаем фрагмент каждый раз, когда убеждаемся, что последнее местоположение/состояние карты сохраняются
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds( //положение карты при запуске
                            new LatLngBounds(new LatLng(55.574487, 37.379872), new LatLng(55.901914, 37.818179)),
                            5);
                    mMap.animateCamera(cameraUpdate);

                    addMarkersToMap(mMap);
                }
            });
        }

        // R.id.map это FrameLayout, а не Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        return rootView;
    }

    private void addMarkersToMap(final GoogleMap map) {

        mChildEventListener = mProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //заносим БД в HashMap и получаем значения
                HashMap<String, Double> dataDouble = (HashMap<String, Double>) dataSnapshot.getValue();
                double latitude = dataDouble.get("latitude");
                double longitude = dataDouble.get("longitude");
                HashMap<String, String> dataString = (HashMap<String, String>) dataSnapshot.getValue();
                String name = dataString.get("name");
                String address = dataString.get("address");
                String discount = dataString.get("discount");

                LatLng location = new LatLng(latitude, longitude);
                map.addMarker(new MarkerOptions().position(location).title(name));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
