package com.anas.pizzeria.ui.history;
 
import android.os.Bundle;
 
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
 
import com.anas.pizzeria.R;
import com.anas.pizzeria.util.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
 
public class HistoryActivity extends AppCompatActivity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Primeni zacuvaniot jazik
        String savedLang = LocaleHelper.getSavedLanguage(this);
        LocaleHelper.applyLocale(this, savedLang);
 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
 
        // Postavi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.order_history));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
 
        HistoryViewModel viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);
 
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "anonymous";
 
        viewModel.getOrdersByUser(userId).observe(this, orders -> {
            if (orders != null) adapter.setOrders(orders);
        });
    }
 
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}