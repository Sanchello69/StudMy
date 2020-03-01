package com.example.studmy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private GoogleMap mMap;
    private LatLngBounds MOSKAU = new LatLngBounds(new LatLng
            (55.574487, 37.379872), new LatLng(55.901914, 37.818179)); //границы Москвы
    private SupportMapFragment mapFragment;
    private Marker marker;
    ChildEventListener mChildEventListener;

    //создаем экземпляр БД и сохраняем ссылку на ветку нашей БД
    DatabaseReference mProfileRef = FirebaseDatabase.getInstance().getReference("studak/kino");

    ArrayList<String> name_info = new ArrayList<>(); //список для имен
    ArrayList<String> address_info = new ArrayList<>(); //список для адресов
    ArrayList<String> discount_info = new ArrayList<>(); //список для описания скидок
    ArrayList<Double> latitude_info = new ArrayList<>();//список для широты
    ArrayList<Double> longitude_info = new ArrayList<>(); // список для долготы
    ArrayList<Marker> marker_info = new ArrayList<>(); // список для маркеров s


    public HomeFragment() {
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

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(MOSKAU, 5); //положение карты при запуске
                    mMap.moveCamera(cameraUpdate); //мгновенное перемещение камеры, без анимации
                    mMap.setLatLngBoundsForCameraTarget(MOSKAU); //ограничиваем область видимости карты

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

                name_info.add(name);
                discount_info.add(discount);
                address_info.add(address);
                latitude_info.add(latitude);
                longitude_info.add(longitude);

                LatLng location = new LatLng(latitude, longitude);
                marker = map.addMarker(new MarkerOptions().position(location));
                marker_info.add(marker);
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //getActivity() берет активность, в которой запущен наш фрагмент
                        Intent intent = new Intent(getActivity(), InfoActivity.class);
                        //Log.d(TAG, "ddd" + marker_info.indexOf(marker));
                        intent.putExtra("name", name_info.get(marker_info.indexOf(marker))); // добавляем в интент
                        intent.putExtra("address", address_info.get(marker_info.indexOf(marker)));
                        intent.putExtra("discount", discount_info.get(marker_info.indexOf(marker)));
                        startActivity(intent);
                        return true; //Если вернется false, то в дополнение к пользовательскому поведению произойдет поведение по умолчанию.
                        // Поведение по умолчанию для события щелчка маркера - показать его информационное окно и переместить камеру так, чтобы маркер находился в центре карты.
                    }
                });
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
