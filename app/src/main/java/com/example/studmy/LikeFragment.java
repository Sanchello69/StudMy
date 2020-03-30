package com.example.studmy;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.google.firebase.auth.FirebaseAuth.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class LikeFragment extends Fragment {

    RecyclerView recyclerView;

    FirebaseUser user;
    String userID;// id пользователя

    FirebaseDatabase db = FirebaseDatabase.getInstance(); //создаем экземпляр БД
    DatabaseReference ref; // ключ

    ChildEventListener mChildEventListener;

    ArrayList<Integer> like_num = new ArrayList<>();

    public LikeFragment() {
        // Required empty public constructor

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



        return view ;
    }

}
