package com.example.studmy;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogInfo extends DialogFragment {


    public DialogInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_okno, null);

        Bundle bundle = getArguments();
        String addressText = bundle.getString("address");
        String nameText = bundle.getString("name");
        String discountText = bundle.getString("discount");

        TextView nameView = (TextView)view.findViewById(R.id.info_name);
        nameView.setText(nameText); // Добавляем текст в надпись с идентификатором addressText

        TextView discountView = (TextView)view.findViewById(R.id.info_discount);
        discountView.setText(discountText);

        TextView addressView = (TextView)view.findViewById(R.id.info_address);
        addressView.setText(addressText);

        return view;
    }

}
