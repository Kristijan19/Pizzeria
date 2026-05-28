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
import com.anas.pizzeria.data.MenuRepository;
import com.anas.pizzeria.model.Pizza;
import com.anas.pizzeria.model.PizzaMenuCatalog;
import com.anas.pizzeria.ui.cart.CartActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {

    private List<Pizza> pizzaList;
    private PizzaAdapter pizzaAdapter;
    private MenuRepository menuRepository;
    private RecyclerView recyclerView;
    private boolean isTablet;
    private PizzaAdapter.PizzaClickListener clickListener;

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

        menuRepository = new MenuRepository();
        pizzaList = new ArrayList<>();
        isTablet = getResources().getBoolean(R.bool.is_tablet);

        recyclerView = view.findViewById(R.id.pizzaRecyclerView);
        if (isTablet) {
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        clickListener = new PizzaAdapter.PizzaClickListener() {
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
        };

        pizzaAdapter = new PizzaAdapter(pizzaList, clickListener);
        recyclerView.setAdapter(pizzaAdapter);

        Button btnCart = view.findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> openCart());

        loadMenuPrices();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMenuPrices();
    }

    private void loadMenuPrices() {
        menuRepository.loadPrices(new MenuRepository.PricesCallback() {
            @Override
            public void onSuccess(Map<String, Double> prices) {
                if (!isAdded()) return;
                applyPrices(prices);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                applyPrices(PizzaMenuCatalog.getDefaultPrices());
            }
        });
    }

    private void applyPrices(Map<String, Double> prices) {
        Map<String, Integer> quantities = new java.util.HashMap<>();
        for (Pizza p : pizzaList) {
            quantities.put(p.getName(), p.getQuantity());
        }

        pizzaList.clear();
        for (PizzaMenuCatalog.Entry entry : PizzaMenuCatalog.getEntries()) {
            double price = prices.containsKey(entry.id) ? prices.get(entry.id) : entry.defaultPrice;
            String name = getString(entry.nameResId);
            Pizza pizza = new Pizza(name, price, entry.imageResId);
            Integer qty = quantities.get(name);
            if (qty != null && qty > 0) {
                pizza.setQuantity(qty);
            }
            pizzaList.add(pizza);
        }

        if (pizzaAdapter != null) {
            pizzaAdapter.notifyDataSetChanged();
        }
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
}
