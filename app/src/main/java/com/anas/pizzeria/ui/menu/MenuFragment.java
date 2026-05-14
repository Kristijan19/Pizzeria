package com.anas.pizzeria.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.pizzeria.R;
import com.anas.pizzeria.model.Pizza;
import com.anas.pizzeria.ui.cart.CartActivity;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private List<Pizza> pizzaList;
    private PizzaAdapter pizzaAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pizzaList = createPizzaList();

        RecyclerView recyclerView = view.findViewById(R.id.pizzaRecyclerView);

        // Tablet: Grid so 2 koloni, Telefon: Linearen
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        if (isTablet) {
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        pizzaAdapter = new PizzaAdapter(pizzaList, new PizzaAdapter.PizzaClickListener() {
            @Override
            public void onPlusClick(int position) {
                Pizza pizza = pizzaList.get(position);
                if (pizza.getQuantity() < 10) {
                    pizza.setQuantity(pizza.getQuantity() + 1);
                    pizzaAdapter.notifyItemChanged(position);
                } else {
                    Toast.makeText(requireContext(),
                            getString(R.string.max_quantity_reached), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMinusClick(int position) {
                Pizza pizza = pizzaList.get(position);
                if (pizza.getQuantity() > 0) {
                    pizza.setQuantity(pizza.getQuantity() - 1);
                    pizzaAdapter.notifyItemChanged(position);
                }
            }
        });

        recyclerView.setAdapter(pizzaAdapter);

        Button btnCart = view.findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> openCart());
    }

    private void openCart() {
        ArrayList<Pizza> selected = new ArrayList<>();
        for (Pizza p : pizzaList) {
            if (p.getQuantity() > 0) selected.add(p);
        }
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(),
                    getString(R.string.cart_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        double total = 0;
        for (Pizza p : selected) total += p.getPrice() * p.getQuantity();

        Intent intent = new Intent(requireContext(), CartActivity.class);
        intent.putParcelableArrayListExtra("selectedPizzas", selected);
        intent.putExtra("total", total);
        startActivity(intent);
    }

    private List<Pizza> createPizzaList() {
        List<Pizza> list = new ArrayList<>();
        list.add(new Pizza("Margherita",       700,  R.drawable.pizza_item));
        list.add(new Pizza("Pepperoni",        1100, R.drawable.pizza_item));
        list.add(new Pizza("Hawaiian",         1200, R.drawable.pizza_item));
        list.add(new Pizza("BBQ Chicken",      1300, R.drawable.pizza_item));
        list.add(new Pizza("Supreme",          1400, R.drawable.pizza_item));
        list.add(new Pizza("Veggie Delight",   1200, R.drawable.pizza_item));
        list.add(new Pizza("Meat Lovers",      1100, R.drawable.pizza_item));
        list.add(new Pizza("Four Cheese",      1000, R.drawable.pizza_item));
        list.add(new Pizza("Buffalo Chicken",  1300, R.drawable.pizza_item));
        list.add(new Pizza("Mushroom",         1200, R.drawable.pizza_item));
        list.add(new Pizza("Mediterranean",    1100, R.drawable.pizza_item));
        list.add(new Pizza("Chicken Fajita",    900, R.drawable.pizza_item));
        list.add(new Pizza("Olive & Tomato",    700, R.drawable.pizza_item));
        list.add(new Pizza("Tandoori",         1300, R.drawable.pizza_item));
        list.add(new Pizza("Extravaganza",     1800, R.drawable.pizza_item));
        list.add(new Pizza("Hot & Spicy",       800, R.drawable.pizza_item));
        return list;
    }
}
