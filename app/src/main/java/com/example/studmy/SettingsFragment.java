package com.example.studmy;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private View view;
    Button bt_out;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        bt_out = view.findViewById(R.id.button_out);
        bt_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // выход из учетной записи
                AuthUI.getInstance().signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(),
                                        "Вы вышли из аккаунта.",
                                        Toast.LENGTH_LONG)
                                        .show();

                                // закрываем окно и заново открываем окно входа/регистрации
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });

        return view;
    }
}
