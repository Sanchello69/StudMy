package com.example.studmy;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
public class DialogInfo extends DialogFragment implements View.OnClickListener {

    private Button btn_route, btn_share;
    private Bundle bundle;
    private String addressText, nameText, discountText, phoneText;
    private int num_like;
    private boolean likeBoolean;
    ImageButton imageButton;

    ArrayList<Integer> like_info = new ArrayList<>(); //список для избранных

    FirebaseUser user;
    String userID;// id пользователя

    FirebaseDatabase db = FirebaseDatabase.getInstance(); //создаем экземпляр БД
    DatabaseReference ref; // ключ
    DatabaseReference ref1; // ключ

    ChildEventListener mChildEventListener;

    public DialogInfo() {
        // Required empty public constructor

        user = getInstance().getCurrentUser();
        userID = user.getUid();// id пользователя

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_okno, null);

        bundle = getArguments();
        addressText = bundle.getString("address");
        nameText = bundle.getString("name");
        discountText = bundle.getString("discount");
        phoneText = bundle.getString("phone");
        num_like = bundle.getInt("num");

        ref = db.getReference("user/"+userID+"/like"+"/like"+num_like); //ключ

        TextView nameView = (TextView)view.findViewById(R.id.info_name);
        nameView.setText(nameText); // Добавляем текст в надпись с идентификатором addressText

        TextView discountView = (TextView)view.findViewById(R.id.info_discount);
        discountView.setText(discountText);

        TextView addressView = (TextView)view.findViewById(R.id.info_address);
        addressView.setText(addressText);

        if (phoneText!=null){
            TextView phoneView = (TextView)view.findViewById(R.id.info_phone);
            phoneView.setText(phoneText);
        }
        else{
            TextView phoneView = (TextView)view.findViewById(R.id.info_phone);
            phoneView.setText("Нет данных");
        }



        btn_route=view.findViewById(R.id.btn_route);
        btn_share=view.findViewById(R.id.btn_share);
        btn_route.setOnClickListener(this);
        btn_share.setOnClickListener(this);

        imageButton = view.findViewById(R.id.imageButtonLike);

        for (int i=0; i<like_info.size(); i++){
            if (num_like==like_info.get(i)){
                likeBoolean=true;
                break;
            }
            else{
                likeBoolean=false;
            }
        }

        if (likeBoolean)
            imageButton.setImageResource(R.drawable.ic_like1_on);
        else
            imageButton.setImageResource(R.drawable.ic_like1_off);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeBoolean = !likeBoolean;
                // меняем изображение на кнопке и заносим в БД
                if (likeBoolean){
                    ref.setValue(num_like); // заносим значение
                    imageButton.setImageResource(R.drawable.ic_like1_on);

                }
                else{
                    ref.removeValue(); //удаляем
                    imageButton.setImageResource(R.drawable.ic_like1_off);

                    for (int i=0; i<like_info.size(); i++){
                        if (num_like==like_info.get(i)){
                            like_info.remove(i);
                        }
                    }
                }
            }
        });

        //getDialog().getWindow().setGravity(Gravity.BOTTOM);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_route:
                //открываем наше место на карте
                String uri = "geo:0,0?q=" + addressText + ' ' + nameText;
                Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(mapIntent);
                break;
            case R.id.btn_share:
                //отправляем текстовое сообщение
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String textToSend="Ёу, бро, а в этом месте неплохая скидочка: " + nameText +
                        ", по адресу: " + addressText + "\n" +
                        "Скачивай приложение StudMy и следи за всеми скидками в кинотеатрах Москвы " +
                        "/ссылка на приложение/";
                intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                startActivity(Intent.createChooser(intent, "Поделиться"));
                break;
        }
    }
}