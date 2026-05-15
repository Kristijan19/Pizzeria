package com.anas.pizzeria.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.anas.pizzeria.R;
import com.anas.pizzeria.ui.auth.LoginActivity;
import com.anas.pizzeria.ui.history.HistoryFragment;
import com.anas.pizzeria.ui.menu.MenuFragment;
import com.anas.pizzeria.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mAnalytics;
    private String currentLang = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAnalytics = FirebaseAnalytics.getInstance(this);

        // Postavi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    // ============ JAZICNO KOPCE VO TOOLBAR ============

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "MK / EN")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            // Zameni jazik
            if (currentLang.equals("en")) {
                setLocale("mk");
                currentLang = "mk";
            } else {
                setLocale("en");
                currentLang = "en";
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());

        // Log Analytics event
        Bundle params = new Bundle();
        params.putString("language", langCode);
        mAnalytics.logEvent("language_changed", params);

        // Restartaj Activity za da se primeni jazikot
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    // ============ HELPER METHODS ============

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