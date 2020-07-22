package com.example.studmy.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studmy.models.Like;
import com.example.studmy.LikeAdapter;
import com.example.studmy.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static com.google.firebase.auth.FirebaseAuth.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class LikeFragment extends Fragment {

    private RecyclerView recyclerView;

    private FirebaseUser user;
    private String userID;// id пользователя

    private FirebaseDatabase db = FirebaseDatabase.getInstance(); //создаем экземпляр БД
    private DatabaseReference ref; // ключ
    private DatabaseReference ref1; // ключ
    private DatabaseReference ref2; // ключ

    ChildEventListener mChildEventListener;

    private ArrayList<Integer> like_num = new ArrayList<>(); //список для номеров избранных
    private ArrayList<String> name_like = new ArrayList<>(); //список для имен
    private ArrayList<String> address_like = new ArrayList<>(); //список для адресов
    private ArrayList<Like> like_class = new ArrayList<>(); //список для инфы о понравивщихся местах
    private ArrayList<Double> latitude_info = new ArrayList<>();//список для широты
    private ArrayList<Double> longitude_info = new ArrayList<>(); // список для долготы

    public LikeFragment() {

        user = getInstance().getCurrentUser();
        userID = user.getUid();// id пользователя

        ref = db.getReference("user/" + userID + "/like");

        mChildEventListener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                like_num.add(dataSnapshot.getValue().hashCode());
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like, container, false);

        //присваиваем переменной наш RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewLike);
        //LayoutManager отвечает за позиционирование view-компонентов в RecyclerView,
        // а также за определение того, когда следует переиспользовать view-компоненты,
        // которые больше не видны пользователю.
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //ref1 = db.getReference("studak/kino");
        ref1 = db.getReference("parser/kinoafisha");
        mChildEventListener = ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //заносим БД в HashMap и получаем значения
                HashMap<String, String> dataString = (HashMap<String, String>) dataSnapshot.getValue();
                String name = dataString.get("name");
                String address = dataString.get("address");
                HashMap<String, Double> dataDouble = (HashMap<String, Double>) dataSnapshot.getValue();
                double latitude = dataDouble.get("latitude");
                double longitude = dataDouble.get("longitude");

                latitude_info.add(latitude);
                longitude_info.add(longitude);
                name_like.add(name);
                address_like.add(address);
                like_class.clear();
                for (int i=0; i<name_like.size(); i++){
                    for (int j=0; j<like_num.size(); j++){
                        if (i==like_num.get(j)){
                            like_class.add(new Like(name_like.get(i), address_like.get(i), latitude_info.get(i), longitude_info.get(i)));
                        }
                    }
                }

                //создаем объект адаптера и передаем ему список данных
                final LikeAdapter likeAdapter = new LikeAdapter(like_class, getActivity());

                //передаем в recyclerView наш объект адаптера с данными
                recyclerView.setAdapter(likeAdapter);

                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition(); //получаем позицию элемента
                        ref2 = db.getReference("user/"+userID+"/like"+"/like"+name_like.indexOf(like_class.get(position).getName_like())); //ключ
                        ref2.removeValue(); //удаляем
                        likeAdapter.notifyItemRemoved(position); //удаляем элемент из списка
                        like_class.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged(); //обновляем
                    }
                };
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView); //прикрепляем к RecyclerView
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

        return view ;
    }
}
