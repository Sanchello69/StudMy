package com.example.studmy.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.studmy.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;
import java.util.List;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MainActivity extends AppCompatActivity {

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.AnonymousBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //создаем намерение входа
        if (getInstance().getCurrentUser() == null) {
            // запускаем окно входа/регистрации
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.ic_logomain) // логотип
                            .setTheme(R.style.AppTheme) // наша тема
                            .build(),
                    1);
        } else {
            // пользователь уже зашел
            if(getInstance().getCurrentUser().getDisplayName()==null){
                Toast.makeText(this,
                        "Привет",
                        Toast.LENGTH_LONG)
                        .show();
            }
            else {
                Toast.makeText(this,
                        "Привет " + getInstance()
                                .getCurrentUser()
                                .getDisplayName(),
                        Toast.LENGTH_LONG)
                        .show();
            }
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }

        //запрос на использование геолокации
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
    }

    @Override
    protected void onRestart() {
        //finish();
        super.onRestart();
    }

    //Когда поток входа будет завершен, получаем результат в onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // успешно вошел в систему
                if(getInstance().getCurrentUser().getDisplayName()==null){
                    Toast.makeText(this,
                            "Привет",
                            Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    Toast.makeText(this,
                            "Привет " + getInstance()
                                    .getCurrentUser()
                                    .getDisplayName(),
                            Toast.LENGTH_LONG)
                            .show();
                }
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
            // закрываем активность
            finish();
        }
    }
}
