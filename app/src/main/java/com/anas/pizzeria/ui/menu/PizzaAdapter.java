package com.anas.pizzeria.ui.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.pizzeria.R;
import com.anas.pizzeria.model.Pizza;

import java.util.List;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder> {

    private final List<Pizza> pizzaList;
    private final PizzaClickListener listener;

    public interface PizzaClickListener {
        void onPlusClick(int position);
        void onMinusClick(int position);
    }

    public PizzaAdapter(List<Pizza> pizzaList, PizzaClickListener listener) {
        this.pizzaList = pizzaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PizzaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pizza, parent, false);
        return new PizzaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PizzaViewHolder holder, int position) {
        Pizza pizza = pizzaList.get(position);
        holder.bind(pizza, position, listener);
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    static class PizzaViewHolder extends RecyclerView.ViewHolder {
        ImageView pizzaImage;
        TextView tvName, tvPrice, tvQuantity;
        ImageView btnPlus, btnMinus;

        PizzaViewHolder(@NonNull View itemView) {
            super(itemView);
            pizzaImage   = itemView.findViewById(R.id.pizzaImageView);
            tvName       = itemView.findViewById(R.id.nameTextView);
            tvPrice      = itemView.findViewById(R.id.priceTextView);
            tvQuantity   = itemView.findViewById(R.id.quantityTextView);
            btnPlus      = itemView.findViewById(R.id.plusImageView);
            btnMinus     = itemView.findViewById(R.id.minusImageView);
        }

        void bind(Pizza pizza, int position, PizzaClickListener listener) {
            pizzaImage.setImageResource(pizza.getImageResId());
            tvName.setText(pizza.getName());
            tvPrice.setText(pizza.getFormattedPrice());
            tvQuantity.setText(String.valueOf(pizza.getQuantity()));

            btnPlus.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onPlusClick(pos);
            });
            btnMinus.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onMinusClick(pos);
            });
        }
    }
}
