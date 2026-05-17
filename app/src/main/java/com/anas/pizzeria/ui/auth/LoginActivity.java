package com.anas.pizzeria.ui.auth;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
 
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
 
import com.anas.pizzeria.R;
import com.anas.pizzeria.ui.MainActivity;
import com.anas.pizzeria.util.LocaleHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
 
import java.util.Arrays;
 
public class LoginActivity extends AppCompatActivity {
 
    private static final int RC_GOOGLE_SIGN_IN = 9001;
 
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mAnalytics;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private TextInputEditText etEmail, etPassword;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Primeni zacuvaniot jazik PRVO pred se drugo
        String savedLang = LocaleHelper.getSavedLanguage(this);
        LocaleHelper.applyLocale(this, savedLang);
 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
 
        // Iscistvanje na Facebook sesija pri sekoe otvoranje
        LoginManager.getInstance().logOut();
 
        // Postavi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
 
        mAuth = FirebaseAuth.getInstance();
        mAnalytics = FirebaseAnalytics.getInstance(this);
 
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
 
        MaterialButton btnEmailLogin = findViewById(R.id.btnEmailLogin);
        btnEmailLogin.setOnClickListener(v -> loginWithEmail());
 
        MaterialButton btnGoRegister = findViewById(R.id.btnGoRegister);
        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
 
        MaterialButton btnAnonymous = findViewById(R.id.btnAnonymous);
        btnAnonymous.setOnClickListener(v -> loginAnonymously());
 
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
 
        SignInButton btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
 
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton btnFacebook = findViewById(R.id.btnFacebook);
        btnFacebook.setPermissions(Arrays.asList("public_profile"));
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                showToast(getString(R.string.login_cancelled));
            }
            @Override
            public void onError(@NonNull FacebookException error) {
                showToast(getString(R.string.login_failed) + ": " + error.getMessage());
            }
        });
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }
        return super.onOptionsItemSelected(item);
    }
 
    private void loginWithEmail() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        if (email.isEmpty() || password.isEmpty()) {
            showToast(getString(R.string.error_empty_fields));
            return;
        }
        showLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        logAnalyticsEvent("login_email");
                        goToMain();
                    } else {
                        showToast(getString(R.string.login_failed));
                    }
                });
    }
 
    private void loginAnonymously() {
        showLoading(true);
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        logAnalyticsEvent("login_anonymous");
                        goToMain();
                    } else {
                        showToast(getString(R.string.login_failed));
                    }
                });
    }
 
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }
 
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        showLoading(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        logAnalyticsEvent("login_google");
                        goToMain();
                    } else {
                        showToast(getString(R.string.login_failed));
                    }
                });
    }
 
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        showLoading(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        logAnalyticsEvent("login_facebook");
                        goToMain();
                    } else {
                        showToast(getString(R.string.login_failed));
                    }
                });
    }
 
    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
 
    private void logAnalyticsEvent(String method) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        mAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }
 
    private void showLoading(boolean show) {
        View progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
 
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                showToast(getString(R.string.login_failed) + ": " + e.getMessage());
            }
        }
    }
}