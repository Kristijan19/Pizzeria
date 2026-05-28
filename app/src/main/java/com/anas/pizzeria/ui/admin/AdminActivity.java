package com.anas.pizzeria.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.pizzeria.R;
import com.anas.pizzeria.data.MenuRepository;
import com.anas.pizzeria.util.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private AdminOrderAdapter orderAdapter;
    private AdminPricesAdapter pricesAdapter;
    private MenuRepository menuRepository;

    private View ordersView;
    private View pricesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String savedLang = LocaleHelper.getSavedLanguage(this);
        LocaleHelper.applyLocale(this, savedLang);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        menuRepository = new MenuRepository();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ordersView = findViewById(R.id.adminOrdersRecyclerView);
        pricesContainer = findViewById(R.id.adminPricesContainer);

        RecyclerView ordersRecycler = findViewById(R.id.adminOrdersRecyclerView);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new AdminOrderAdapter();
        ordersRecycler.setAdapter(orderAdapter);

        RecyclerView pricesRecycler = findViewById(R.id.adminPricesRecyclerView);
        pricesRecycler.setLayoutManager(new LinearLayoutManager(this));
        pricesAdapter = new AdminPricesAdapter();
        pricesRecycler.setAdapter(pricesAdapter);

        MaterialButton btnSavePrices = findViewById(R.id.btnSavePrices);
        btnSavePrices.setOnClickListener(v -> savePrices());

        TabLayout tabLayout = findViewById(R.id.adminTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.admin_tab_orders));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.admin_tab_prices));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean ordersTab = tab.getPosition() == 0;
                ordersView.setVisibility(ordersTab ? View.VISIBLE : View.GONE);
                pricesContainer.setVisibility(ordersTab ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        verifyAdminAndLoad();
    }

    private void verifyAdminAndLoad() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, getString(R.string.admin_access_denied), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("admins")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    boolean isAdmin = document.exists() && Boolean.TRUE.equals(document.getBoolean("isAdmin"));
                    if (isAdmin) {
                        loadOrders();
                        loadPricesForAdmin();
                    } else {
                        Toast.makeText(this, getString(R.string.admin_access_denied), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.admin_access_denied), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void loadOrders() {
        db.collection("orders")
                .orderBy("dateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, R.string.admin_orders_load_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshots != null) {
                        orderAdapter.setOrders(snapshots.getDocuments());
                    }
                });
    }

    private void loadPricesForAdmin() {
        menuRepository.loadPrices(new MenuRepository.PricesCallback() {
            @Override
            public void onSuccess(java.util.Map<String, Double> prices) {
                pricesAdapter.setPrices(prices);
            }

            @Override
            public void onError(Exception e) {
                pricesAdapter.setPrices(com.anas.pizzeria.model.PizzaMenuCatalog.getDefaultPrices());
            }
        });
    }

    private void savePrices() {
        menuRepository.savePrices(pricesAdapter.collectPrices(), new MenuRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminActivity.this, R.string.admin_prices_saved, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminActivity.this, R.string.admin_prices_save_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
