package com.anas.pizzeria.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anas.pizzeria.R;
import com.anas.pizzeria.ui.auth.LoginActivity;
import com.anas.pizzeria.ui.history.HistoryFragment;
import com.anas.pizzeria.ui.menu.MenuFragment;
import com.anas.pizzeria.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAnalytics = FirebaseAnalytics.getInstance(this);

        // Proveri dali korisnikot e najaven
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            goToLogin();
            return;
        }

        // Ucitaj poceten fragment
        if (savedInstanceState == null) {
            loadFragment(new MenuFragment());
        }

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_menu) {
                fragment = new MenuFragment();
                logScreen("MenuFragment");
            } else if (id == R.id.nav_history) {
                fragment = new HistoryFragment();
                logScreen("HistoryFragment");
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
                logScreen("ProfileFragment");
            }
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void logScreen(String screenName) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        mAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
