package com.anas.pizzeria.ui.admin;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.pizzeria.R;
import com.anas.pizzeria.model.PizzaMenuCatalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPricesAdapter extends RecyclerView.Adapter<AdminPricesAdapter.PriceViewHolder> {

    public static class PriceRow {
        public final PizzaMenuCatalog.Entry entry;
        public double price;

        public PriceRow(PizzaMenuCatalog.Entry entry, double price) {
            this.entry = entry;
            this.price = price;
        }
    }

    private final List<PriceRow> rows = new ArrayList<>();

    public void setPrices(Map<String, Double> prices) {
        rows.clear();
        for (PizzaMenuCatalog.Entry entry : PizzaMenuCatalog.getEntries()) {
            double price = prices.containsKey(entry.id) ? prices.get(entry.id) : entry.defaultPrice;
            rows.add(new PriceRow(entry, price));
        }
        notifyDataSetChanged();
    }

    public Map<String, Double> collectPrices() {
        Map<String, Double> map = new HashMap<>();
        for (PriceRow row : rows) {
            if (row.price <= 0) {
                row.price = row.entry.defaultPrice;
            }
            map.put(row.entry.id, row.price);
        }
        return map;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_price, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        PriceRow row = rows.get(position);
        holder.bind(row);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class PriceViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final EditText etPrice;
        private TextWatcher watcher;
        private PriceRow boundRow;

        PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminPizzaName);
            etPrice = itemView.findViewById(R.id.etAdminPizzaPrice);
        }

        void bind(PriceRow row) {
            boundRow = row;
            tvName.setText(itemView.getContext().getString(row.entry.nameResId));

            if (watcher != null) {
                etPrice.removeTextChangedListener(watcher);
            }
            etPrice.setText(String.valueOf((int) row.price));

            watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (boundRow == null) return;
                    try {
                        boundRow.price = Double.parseDouble(s.toString().trim());
                    } catch (NumberFormatException ignored) {
                        boundRow.price = 0;
                    }
                }
            };
            etPrice.addTextChangedListener(watcher);
        }
    }
}
