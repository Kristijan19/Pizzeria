package com.anas.pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CART_ACTIVITY = 1;
    private List<Pizza> pizzaList;
    private PizzaAdapter pizzaAdapter;

    private int pizzaCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryYellow));
        window.setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryYellow));
        RecyclerView pizzaRecyclerView = findViewById(R.id.pizzaRecyclerView);
        pizzaList = createPizzaList();
        pizzaAdapter = new PizzaAdapter(pizzaList, new PizzaAdapter.PizzaClickListener() {
            @Override
            public void onPlusClick(int position) {
                addPizzaToCart(position);
            }

            @Override
            public void onMinusClick(int position) {
                removePizzaFromCart(position);
            }
        });

        pizzaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pizzaRecyclerView.setAdapter(pizzaAdapter);

        Button cartButton = findViewById(R.id.cartButton);
        Button historyButton = findViewById(R.id.historyButton);

        cartButton.setOnClickListener(v -> {
            if (pizzaCount > 0) {
                ArrayList<Pizza> selectedPizzas = new ArrayList<>();
                for (Pizza pizza : pizzaList) {
                    if (pizza.getQuantity() > 0) {
                        selectedPizzas.add(pizza);
                    }
                }
                if (selectedPizzas.size() > 0) {
                    double total = calculateTotal(selectedPizzas);
                    openCartActivity(selectedPizzas, total);
                } else {
                    Toast.makeText(MainActivity.this, "No pizzas added to the cart", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "No pizzas added to the cart", Toast.LENGTH_SHORT).show();
            }
        });

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private List<Pizza> createPizzaList() {
        List<Pizza> pizzas = new ArrayList<>();
        pizzas.add(new Pizza("Margherita"       , "PKR 700"));
        pizzas.add(new Pizza("Pepperoni"        , "PKR 1100"));
        pizzas.add(new Pizza("Hawaiian"         , "PKR 1200"));
        pizzas.add(new Pizza("BBQ Chicken"      , "PKR 1300"));
        pizzas.add(new Pizza("Supreme"          , "PKR 1400"));
        pizzas.add(new Pizza("Veggie Delight"   , "PKR 1200"));
        pizzas.add(new Pizza("Meat Lovers"      , "PKR 1100"));
        pizzas.add(new Pizza("Four Cheese"      , "PKR 1000"));
        pizzas.add(new Pizza("Buffalo Chicken"  , "PKR 1300"));
        pizzas.add(new Pizza("Mushroom"         , "PKR 1200"));
        pizzas.add(new Pizza("Mediterranean"    , "PKR 1100"));
        pizzas.add(new Pizza("Chicken Fajita"   , "PKR 900"));
        pizzas.add(new Pizza("Olive & Tomato"   , "PKR 700"));
        pizzas.add(new Pizza("Tandoori"         , "PKR 1300"));
        pizzas.add(new Pizza("Extravaganza"     , "PKR 1800"));
        pizzas.add(new Pizza("Hot & Spicy"      , "PKR 800"));
        return pizzas;
    }

    private void addPizzaToCart(int position) {
        Pizza pizza = pizzaList.get(position);
        int quantity = pizza.getQuantity();
        if (quantity < 10) {
            pizzaCount++;
            quantity++;
            pizza.setQuantity(quantity);
            pizzaAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Maximum pizza limit reached", Toast.LENGTH_SHORT).show();
        }
    }

    private void removePizzaFromCart(int position) {
        Pizza pizza = pizzaList.get(position);
        int quantity = pizza.getQuantity();
        if (quantity > 0) {
            pizzaCount--;
            quantity--;
            pizza.setQuantity(quantity);
            pizzaAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Why?", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateTotal(List<Pizza> pizzas) {
        double total = 0;
        for (Pizza pizza : pizzas) {
            int quantity = pizza.getQuantity();
            double price = Double.parseDouble(pizza.getPrice().replaceAll("[^\\d.]+", ""));
            total += (quantity * price);
        }
        return total;
    }


    private void openCartActivity(List<Pizza> selectedPizzas, double total) {
        Intent intent = new Intent(MainActivity.this, CartActivity.class);
        intent.putExtra("selectedPizzas", new ArrayList<>(selectedPizzas));
        intent.putExtra("total", total);
        startActivityForResult(intent, REQUEST_CART_ACTIVITY);
    }

    //Sets the Quantities of pizzas back to 0 once we confirm the order
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CART_ACTIVITY && resultCode == RESULT_OK) {
            ArrayList<Pizza> updatedPizzas = data.getParcelableArrayListExtra("selectedPizzas");
            if (updatedPizzas != null) {
                for (Pizza pizza : pizzaList) {
                    for (Pizza updatedPizza : updatedPizzas) {
                        if (Objects.equals(pizza.getName(), updatedPizza.getName())) {
                            pizza.setQuantity(updatedPizza.getQuantity());
                        }
                    }
                }
                pizzaAdapter.notifyDataSetChanged();
            }
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        }

    }
}