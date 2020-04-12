package com.example.studmy;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
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
    DatabaseReference ref1; // ключ
    DatabaseReference ref2; // ключ

    ChildEventListener mChildEventListener;

    ArrayList<Integer> like_num = new ArrayList<>(); //список для номеров избранных
    ArrayList<String> name_like = new ArrayList<>(); //список для имен
    ArrayList<String> address_like = new ArrayList<>(); //список для адресов
    ArrayList<Like> like_class = new ArrayList<>(); //список для инфы о понравивщихся местах

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

        SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        if (preferences.getBoolean("pref", true)) {
            // При первом запуске (или если юзер удалял все данные приложения) показываем тост
            for (int i=0; i<3; i++){
                Toast toast = Toast.makeText(getActivity(),
                        "Чтобы открыть место на карте нажмите на него. \n" +
                                "Чтобы удалить из избранного свайпните влево.", Toast.LENGTH_LONG);
                toast.show();
            }

            preferences.edit().putBoolean("pref", false).commit();
        }

        //присваиваем переменной наш RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewLike);
        //LayoutManager отвечает за позиционирование view-компонентов в RecyclerView,
        // а также за определение того, когда следует переиспользовать view-компоненты,
        // которые больше не видны пользователю.
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ref1 = db.getReference("studak/kino");
        mChildEventListener = ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //заносим БД в HashMap и получаем значения
                HashMap<String, String> dataString = (HashMap<String, String>) dataSnapshot.getValue();
                String name = dataString.get("name");
                String address = dataString.get("address");

                name_like.add(name);
                address_like.add(address);
                like_class.clear();
                for (int i=0; i<name_like.size(); i++){
                    for (int j=0; j<like_num.size(); j++){
                        if (i==like_num.get(j)){
                            like_class.add(new Like(name_like.get(i), address_like.get(i)));
                        }
                    }
                }

                //создаем объект адаптера и передаем ему список данных
                final LikeAdapter likeAdapter = new LikeAdapter(like_class, getActivity());

                //передаем в recyclerView наш объект адаптера с данными
                recyclerView.setAdapter(likeAdapter);

                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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
