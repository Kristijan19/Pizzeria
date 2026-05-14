package com.anas.pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText addressEditText;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Window window = CartActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(CartActivity.this, R.color.colorPrimaryYellow));
        window.setNavigationBarColor(ContextCompat.getColor(CartActivity.this, R.color.colorPrimaryYellow));
        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        TextView selectedPizzasTextView = findViewById(R.id.selectedPizzasTextView);
        TextView totalTextView = findViewById(R.id.totalTextView);
        Button confirmOrderButton = findViewById(R.id.confirmOrderButton);

        // Retrieve selected pizzas and total from the intent
        Intent intent = getIntent();
        ArrayList<Pizza> selectedPizzas = intent.getParcelableArrayListExtra("selectedPizzas");
        double total = intent.getDoubleExtra("total", 0.0);

        dbHelper = new DBHelper(this);

        StringBuilder pizzasText = new StringBuilder();
        long count = 0;
        for (Pizza pizza : selectedPizzas) {
            pizzasText.append(++count).append(". ").append(pizza.getName()).append(" (").append(pizza.getQuantity()).append(")\n");
        }

        selectedPizzasTextView.setText(pizzasText.toString());
        totalTextView.setText(String.format("Total: PKR %.2f", total) + "\nFree Delivery\nTax Inclusive Price (17% GST)");

        confirmOrderButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String address = addressEditText.getText().toString();

            if (name.isEmpty() || address.isEmpty()) {
                Toast.makeText(CartActivity.this, "Please enter your name and address", Toast.LENGTH_SHORT).show();
            } else {
                long orderId = dbHelper.insertOrder(name, address, total);
                if (orderId != -1) {
                    Toast.makeText(CartActivity.this, "Order confirmed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to insert order", Toast.LENGTH_SHORT).show();
                }

                for (Pizza pizza : selectedPizzas) {
                    pizza.setQuantity(0);
                }

                Intent intent1 = new Intent(CartActivity.this, MainActivity.class);
                intent1.putParcelableArrayListExtra("selectedPizzas", selectedPizzas);
                setResult(RESULT_OK, intent1);

                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
