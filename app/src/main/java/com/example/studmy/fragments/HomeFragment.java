package com.example.studmy.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.studmy.R;
import com.example.studmy.models.MyItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;

import static com.google.firebase.auth.FirebaseAuth.getInstance;


public class HomeFragment extends Fragment {

    private GoogleMap mMap;
    private LatLngBounds MOSKAU = new LatLngBounds(new LatLng
            (55.574487, 37.359872), new LatLng(55.901914, 37.868179)); //границы Москвы
    private LatLng MOSKAU1 = new LatLng(55.753215, 37.622504);
    private SupportMapFragment mapFragment;
    private ClusterManager<MyItem> mClusterManager;
    private Context mContext;

    public ChildEventListener mChildEventListener;

    private Bundle bundle = new Bundle();

    //создаем экземпляр БД и сохраняем ссылку на ветку нашей БД
    private DatabaseReference mProfileRef = FirebaseDatabase.getInstance().getReference("studak/kino");

    private ArrayList<String> name_info = new ArrayList<>(); //список для имен
    private ArrayList<String> address_info = new ArrayList<>(); //список для адресов
    private ArrayList<String> discount_info = new ArrayList<>(); //список для описания скидок
    private ArrayList<String> phone_info = new ArrayList<>(); //список для телефона
    private ArrayList<Double> latitude_info = new ArrayList<>();//список для широты
    private ArrayList<Double> longitude_info = new ArrayList<>(); // список для долготы
    private ArrayList<MyItem> marker_info = new ArrayList<>(); // список для маркеров

    private ArrayList<Integer> like_info = new ArrayList<>(); //список для избранных

    private FirebaseUser user;
    private String userID;// id пользователя

    private FirebaseDatabase db = FirebaseDatabase.getInstance(); //создаем экземпляр БД
    private DatabaseReference ref1; // ключ

    private FrameLayout layout;
    private BottomSheetBehavior bottomSheetBehavior;

    //public HomeFragment() {
   // }

    public HomeFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        user = getInstance().getCurrentUser();
        userID = user.getUid();// id пользователя

        layout = (FrameLayout) getActivity().findViewById(R.id.info_fr);
        bottomSheetBehavior = BottomSheetBehavior.from(layout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); //расширенный
        bottomSheetBehavior.setHideable(false); //не скрывать элемент при свайпе вниз

        ref1 = db.getReference("user/" + userID + "/like");

        mChildEventListener = ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                like_info.add(dataSnapshot.getValue().hashCode());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // не воссоздаем фрагмент каждый раз, когда убеждаемся, что последнее местоположение/состояние карты сохраняются
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(MOSKAU1, 5); //положение карты при запуске
                    //mMap.moveCamera(cameraUpdate); //мгновенное перемещение камеры, без анимации
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MOSKAU1, 10));
                    mMap.setLatLngBoundsForCameraTarget(MOSKAU); //ограничиваем область видимости карты

                    mClusterManager = new ClusterManager<MyItem>(getActivity(), mMap);
                    mClusterManager.setRenderer(new CustomRenderer<MyItem>(getActivity(), mMap, mClusterManager));
                    mMap.setOnCameraIdleListener(mClusterManager);
                    mMap.setOnMarkerClickListener(mClusterManager);

                    //вызывается при движении камеры
                    googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                        @Override
                        public void onCameraMoveStarted(int reason) {
                            if (reason ==REASON_GESTURE) { //REASON_GESTURE- движение камеры в ответ на жесты пользователя

                                Fragment fragment = getFragmentManager().findFragmentById(R.id.info_fr);


                                if (fragment!=null){
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); //расширенный
                                    fragment.getView().animate().translationY(fragment.getView().getHeight()+10).setDuration(350); //анимация скрытия плавно вниз
                                    //fragment.getView().setVisibility(View.GONE); //скрываем

                                }
                            }
                        }
                    });

                    //mMap.setPadding(0,0,0,100);

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

                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
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

                        map.moveCamera(CameraUpdateFactory.newLatLng(item.getPosition()));

                        //передаем информацию в фрагмент
                        bundle.putString("name", name_info.get(marker_info.indexOf(item)));
                        bundle.putString("address", address_info.get(marker_info.indexOf(item)));
                        bundle.putString("discount", discount_info.get(marker_info.indexOf(item)));
                        bundle.putString("phone", phone_info.get(marker_info.indexOf(item)));
                        bundle.putInt("num", marker_info.indexOf(item));
                        bundle.putIntegerArrayList("like", like_info);

                        Fragment fragment = new InfoFragment();
                        fragment.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                        fragmentTransaction.replace(R.id.info_fr, fragment); //обновляем
                        fragmentTransaction.commit();

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
