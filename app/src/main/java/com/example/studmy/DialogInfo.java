package com.example.studmy;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogInfo extends DialogFragment implements View.OnClickListener {

    private Button btn_route, btn_share;
    private Bundle bundle;
    private String addressText, nameText, discountText;

    public DialogInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_okno, null);

        bundle = getArguments();
        addressText = bundle.getString("address");
        nameText = bundle.getString("name");
        discountText = bundle.getString("discount");

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
