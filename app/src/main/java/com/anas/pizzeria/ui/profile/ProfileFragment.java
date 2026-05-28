package com.anas.pizzeria.ui.profile;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
 
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
 
import com.anas.pizzeria.R;
import com.anas.pizzeria.ui.admin.AdminActivity;
import com.anas.pizzeria.ui.auth.LoginActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
 
public class ProfileFragment extends Fragment {
 
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
 
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
 
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
 
        TextView tvEmail  = view.findViewById(R.id.tvEmail);
        TextView tvUserId = view.findViewById(R.id.tvUserId);
        TextView tvType   = view.findViewById(R.id.tvLoginType);
        Button btnAdmin   = view.findViewById(R.id.btnAdmin);
 
        if (user != null) {
            String email = user.getEmail();
            tvEmail.setText(email != null ? email : getString(R.string.anonymous_user));
            tvUserId.setText(user.getUid());
 
            if (user.isAnonymous()) {
                tvType.setText(getString(R.string.login_type_anonymous));
            } else {
                boolean isFacebook = false;
                boolean isGoogle = false;
                for (com.google.firebase.auth.UserInfo info : user.getProviderData()) {
                    if (info.getProviderId().equals("facebook.com")) isFacebook = true;
                    if (info.getProviderId().equals("google.com")) isGoogle = true;
                }
                if (isFacebook) {
                    tvType.setText(R.string.login_type_facebook);
                    tvEmail.setText(getString(R.string.anonymous_user));
                } else if (isGoogle) {
                    tvType.setText(R.string.login_type_google);
                } else {
                    tvType.setText(R.string.login_type_email_password);
                }
            }
 
            // Proveri dali e Admin vo Firestore
            if (btnAdmin != null) {
                FirebaseFirestore.getInstance()
                        .collection("admins")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(document -> {
                            if (document.exists() && Boolean.TRUE.equals(document.getBoolean("isAdmin"))) {
                                btnAdmin.setVisibility(View.VISIBLE);
                            } else {
                                btnAdmin.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(e -> btnAdmin.setVisibility(View.GONE));
            }
        }
 
        // Admin kopce
        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), AdminActivity.class);
                startActivity(intent);
            });
        }
 
        // Logout kopce
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken(null);
            FirebaseAuth.getInstance().signOut();
 
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        });
    }
}
