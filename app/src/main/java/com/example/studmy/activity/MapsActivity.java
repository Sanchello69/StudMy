package com.example.studmy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.studmy.fragments.HomeFragment;
import com.example.studmy.R;
import com.example.studmy.fragments.LikeFragment;
import com.example.studmy.fragments.SettingsFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsActivity extends AppCompatActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {

    private FrameLayout layout;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5463324623023106/9528147951");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, new HomeFragment(getApplicationContext()));
        fragmentTransaction.commit();

        layout = (FrameLayout) findViewById(R.id.info_fr);

        //получаем меню и прикрепляем слушателя
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.home:
                //fragment = new HomeFragment();
                fragment = new HomeFragment(getApplicationContext());
                break;

            case R.id.like:
                fragment = new LikeFragment();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    //Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                break;

            case R.id.settings:
                fragment = new SettingsFragment();
                break;
        }

        layout.removeAllViews(); // удалить все View из FrameLayout
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
}