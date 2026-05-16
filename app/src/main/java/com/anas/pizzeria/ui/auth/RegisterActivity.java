
package com.anas.pizzeria.ui.auth;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
 
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
 
import com.anas.pizzeria.R;
import com.anas.pizzeria.ui.MainActivity;
import com.anas.pizzeria.util.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
 
public class RegisterActivity extends AppCompatActivity {
 
    private FirebaseAuth mAuth;
    private TextInputEditText etEmail, etPassword, etConfirmPassword;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Primeni zacuvaniot jazik
        String savedLang = LocaleHelper.getSavedLanguage(this);
        LocaleHelper.applyLocale(this, savedLang);
 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
 
        // Postavi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
 
        mAuth = FirebaseAuth.getInstance();
 
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
 
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> register());
 
        MaterialButton btnBackLogin = findViewById(R.id.btnBackLogin);
        btnBackLogin.setOnClickListener(v -> finish());
    }
 
    // ============ JAZICNO KOPCE VO TOOLBAR ============
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String currentLang = LocaleHelper.getSavedLanguage(this);
        String label = currentLang.equals("en") ? "MK" : "EN";
        menu.add(Menu.NONE, 1, Menu.NONE, label)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
 
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        String currentLang = LocaleHelper.getSavedLanguage(this);
        String label = currentLang.equals("en") ? "MK" : "EN";
        menu.add(Menu.NONE, 1, Menu.NONE, label)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            String currentLang = LocaleHelper.getSavedLanguage(this);
            String newLang = currentLang.equals("en") ? "mk" : "en";
            LocaleHelper.setLocale(this, newLang);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
 
    private void register() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirm = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
 
        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, getString(R.string.error_passwords_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, getString(R.string.error_password_short), Toast.LENGTH_SHORT).show();
            return;
        }
 
        View progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
 
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseAnalytics.getInstance(this).logEvent("sign_up", null);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}