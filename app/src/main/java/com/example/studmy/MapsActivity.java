package com.example.studmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MapsActivity extends AppCompatActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {
    // способы входа
   /* List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.AnonymousBuilder().build());*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

       /* if (getInstance().getCurrentUser() == null) {
            // запускаем окно входа/регистрации
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                          //  .setLogo(R.drawable.my_great_logo)      // логотип
                            .setTheme(R.style.AppTheme) // наша тема
                            .build(),
                    1);
        } else {
            // пользователь уже зашел
            Toast.makeText(this,
                    "Привет " + getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
        }*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, new HomeFragment());
        fragmentTransaction.commit();

        //получаем меню и прикрепляем слушателя
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

       /* //запрос на использование геолокации
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.home:
                fragment = new HomeFragment();
                break;

            case R.id.like:
                fragment = new LikeFragment();
                break;

            case R.id.settings:
                fragment = new SettingsFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //переключение фрагмента
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit();
            return true;
        }
        return false;
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // успешно вошел в систему

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }*/

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // успешно вошел в систему
                Toast.makeText(this,
                        "Привет " + getInstance()
                                .getCurrentUser()
                                .getDisplayName(),
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                // закрываем приложение
                finish();
            }
        }
    }*/
}