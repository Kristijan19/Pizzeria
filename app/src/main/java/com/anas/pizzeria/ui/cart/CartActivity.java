package com.anas.pizzeria.ui.cart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.anas.pizzeria.R;
import com.anas.pizzeria.model.Pizza;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private EditText etName, etAddress;
    private CartViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        etName    = findViewById(R.id.nameEditText);
        etAddress = findViewById(R.id.addressEditText);

        ArrayList<Pizza> selectedPizzas = getIntent().getParcelableArrayListExtra("selectedPizzas");
        double total = getIntent().getDoubleExtra("total", 0.0);

        // Prikazi gi narachanite pici
        TextView tvPizzas = findViewById(R.id.selectedPizzasTextView);
        TextView tvTotal  = findViewById(R.id.totalTextView);

        if (selectedPizzas != null) {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (Pizza p : selectedPizzas) {
                sb.append(i++).append(". ")
                  .append(p.getName())
                  .append(" x").append(p.getQuantity())
                  .append("  (").append(p.getFormattedPrice()).append(")\n");
            }
            tvPizzas.setText(sb.toString());
        }

        tvTotal.setText(getString(R.string.total_label, total));

        Button btnConfirm = findViewById(R.id.confirmOrderButton);
        btnConfirm.setOnClickListener(v -> confirmOrder(selectedPizzas, total));

        // Observe so ViewModel
        viewModel.getOrderSaved().observe(this, saved -> {
            if (saved != null && saved) {
                Toast.makeText(this, getString(R.string.order_confirmed), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void confirmOrder(ArrayList<Pizza> pizzas, double total) {
        String name    = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "anonymous";

        String dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).format(new Date());

        // Konvertirај go listata vo JSON string za Room
        String pizzaJson = pizzasToJson(pizzas);

        // Log Analytics event
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putDouble(FirebaseAnalytics.Param.VALUE, total);
        analyticsBundle.putString(FirebaseAnalytics.Param.CURRENCY, "MKD");
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.PURCHASE, analyticsBundle);

        // Zacuvaj preku ViewModel -> Repository -> Room + Firestore
        viewModel.saveOrder(name, address, total, pizzaJson, dateTime, userId);
    }

    private String pizzasToJson(ArrayList<Pizza> pizzas) {
        JSONArray arr = new JSONArray();
        try {
            if (pizzas != null) {
                for (Pizza p : pizzas) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", p.getName());
                    obj.put("price", p.getPrice());
                    obj.put("quantity", p.getQuantity());
                    arr.put(obj);
                }
            }
        } catch (Exception ignored) {}
        return arr.toString();
    }
}
