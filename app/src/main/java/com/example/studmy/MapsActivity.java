package com.example.studmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ChildEventListener mChildEventListener;
    //создаем экземпляр БД и сохраняем ссылку на ветку нашей БД
    DatabaseReference mProfileRef = FirebaseDatabase.getInstance().getReference("studak/kino");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:

                                break;
                            case R.id.like:

                                break;
                            case R.id.settings:

                                break;
                        }
                        return false;
                    }
                });

       /* FirebaseDatabase database = FirebaseDatabase.getInstance(); //создание экземпляра БД
        DatabaseReference reference = database.getReference("kino"); //ссылка на БД
        reference.addValueEventListener(new ValueEventListener() { //чтение БД
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                kino = dataSnapshot.getValue(Kino.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds( //положение карты при запуске
                new LatLngBounds(new LatLng(55.574487, 37.379872), new LatLng(55.901914, 37.818179)),
                5);
        mMap.animateCamera(cameraUpdate);

        addMarkersToMap(mMap);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    private void addMarkersToMap(final GoogleMap map){

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

                LatLng location = new LatLng(latitude,longitude);
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
