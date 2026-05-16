
package com.anas.pizzeria.ui;
 
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
 
import androidx.appcompat.app.AppCompatActivity;
 
import com.anas.pizzeria.R;
import com.anas.pizzeria.ui.auth.LoginActivity;
import com.anas.pizzeria.util.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
 
public class SplashActivity extends AppCompatActivity {
 
    private static final int SPLASH_DELAY = 2500;
    private static final String TAG = "FCM_TOKEN";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Primeni zacuvaniot jazik pri sekoe startuvanje
        String savedLang = LocaleHelper.getSavedLanguage(this);
        LocaleHelper.applyLocale(this, savedLang);
 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        // FCM Token logging za Firebase Messaging testiranje
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                    } else {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                    }
                });
 
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent;
            if (currentUser != null) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}