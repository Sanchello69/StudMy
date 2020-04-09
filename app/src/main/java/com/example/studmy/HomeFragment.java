package com.example.studmy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private GoogleMap mMap;
    private LatLngBounds MOSKAU = new LatLngBounds(new LatLng
            (55.574487, 37.379872), new LatLng(55.901914, 37.818179)); //границы Москвы
    private SupportMapFragment mapFragment;
    //private Marker marker;
    private ClusterManager<MyItem> mClusterManager;

    DialogFragment dl_info;

    ChildEventListener mChildEventListener;

    Bundle bundle = new Bundle();

    //создаем экземпляр БД и сохраняем ссылку на ветку нашей БД
    DatabaseReference mProfileRef = FirebaseDatabase.getInstance().getReference("studak/kino");

    ArrayList<String> name_info = new ArrayList<>(); //список для имен
    ArrayList<String> address_info = new ArrayList<>(); //список для адресов
    ArrayList<String> discount_info = new ArrayList<>(); //список для описания скидок
    ArrayList<String> phone_info = new ArrayList<>(); //список для телефона
    ArrayList<Double> latitude_info = new ArrayList<>();//список для широты
    ArrayList<Double> longitude_info = new ArrayList<>(); // список для долготы
    ArrayList<MyItem> marker_info = new ArrayList<>(); // список для маркеров

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        dl_info = new DialogInfo();

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

                    mClusterManager = new ClusterManager<MyItem>(getActivity(), mMap);
                    mClusterManager.setRenderer(new CustomRenderer<MyItem>(getActivity(), mMap, mClusterManager));
                    mMap.setOnCameraIdleListener(mClusterManager);
                    mMap.setOnMarkerClickListener(mClusterManager);

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
                String phone = dataString.get("phone");

                name_info.add(name);
                discount_info.add(discount);
                address_info.add(address);
                phone_info.add(phone);
                latitude_info.add(latitude);
                longitude_info.add(longitude);

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) { //если получено разрешение
                    mMap.setMyLocationEnabled(true); //показать свое местоположение
                }

                MyItem offsetItem = new MyItem(latitude, longitude);
                marker_info.add(offsetItem);
                mClusterManager.addItem(offsetItem);
                mClusterManager.setAnimation(false);
                mClusterManager.cluster ();
                map.setOnMarkerClickListener(mClusterManager);

                mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
                    @Override
                    public boolean onClusterClick(Cluster<MyItem> cluster) {
                        map.moveCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
                        //map.animateCamera(CameraUpdateFactory.zoomTo(11));
                        return true;
                    }
                });

                mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        //передаем информацию в диалоговое окно
                        bundle.putString("name", name_info.get(marker_info.indexOf(item)));
                        bundle.putString("address", address_info.get(marker_info.indexOf(item)));
                        bundle.putString("discount", discount_info.get(marker_info.indexOf(item)));
                        bundle.putString("phone", phone_info.get(marker_info.indexOf(item)));
                        bundle.putInt("num", marker_info.indexOf(item));

                        dl_info.setArguments(bundle);
                        dl_info.show(getFragmentManager(), "dl_info");

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
