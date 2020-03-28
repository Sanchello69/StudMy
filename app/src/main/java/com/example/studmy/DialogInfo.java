package com.example.studmy;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

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
public class DialogInfo extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button btn_route, btn_share;
    private Bundle bundle;
    private String addressText, nameText, discountText;
    private int num_like;
    private boolean likeBoolean;
    ToggleButton btn_like;
    ImageButton imageButton;

    FirebaseUser user = getInstance().getCurrentUser();
    String userID = user.getUid();// id пользователя

    FirebaseDatabase db = FirebaseDatabase.getInstance(); //создаем экземпляр БД
    DatabaseReference ref; // ключ

    public DialogInfo() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        btn_like.setChecked(likeBoolean);
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_okno, null);

        bundle = getArguments();
        addressText = bundle.getString("address");
        nameText = bundle.getString("name");
        discountText = bundle.getString("discount");
        num_like = bundle.getInt("num");
        likeBoolean = bundle.getBoolean("boolean_like");

        ref = db.getReference("user/"+userID+"/like"+num_like); //ключ

        TextView nameView = (TextView)view.findViewById(R.id.info_name);
        nameView.setText(nameText); // Добавляем текст в надпись с идентификатором addressText

        TextView discountView = (TextView)view.findViewById(R.id.info_discount);
        discountView.setText(discountText);

        TextView addressView = (TextView)view.findViewById(R.id.info_address);
        addressView.setText(addressText);

        btn_route=view.findViewById(R.id.btn_route);
        btn_share=view.findViewById(R.id.btn_share);
        btn_route.setOnClickListener(this);
        btn_share.setOnClickListener(this);

        btn_like = (ToggleButton) view.findViewById(R.id.button_like);
        btn_like.setOnCheckedChangeListener(this);

        imageButton = view.findViewById(R.id.imageButtonLike);

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked !=likeBoolean){
            if (isChecked && likeBoolean==false){
                ref.setValue(num_like); // значение
            }
            else if (likeBoolean==true){
                ref.removeValue(); //удаляем
            }
        }
    }
}