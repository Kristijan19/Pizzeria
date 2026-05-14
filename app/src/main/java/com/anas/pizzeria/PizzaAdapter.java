package com.anas.pizzeria;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder> {

    private final List<Pizza> pizzaList;
    private final PizzaClickListener pizzaClickListener;

    public interface PizzaClickListener {
        void onPlusClick(int position);
        void onMinusClick(int position);
    }

    public PizzaAdapter(List<Pizza> pizzaList, PizzaClickListener pizzaClickListener) {
        this.pizzaList = pizzaList;
        this.pizzaClickListener = pizzaClickListener;
    }

    @NonNull
    @Override
    public PizzaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pizza, parent, false);
        return new PizzaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PizzaViewHolder holder, int position) {
        Pizza pizza = pizzaList.get(position);

        holder.pizzaImageView.setImageResource(R.drawable.pizza_item);
        holder.nameTextView.setText(pizza.getName());
        holder.priceTextView.setText(pizza.getPrice());
        holder.quantityTextView.setText(String.valueOf(pizza.getQuantity()));

        holder.plusImageView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                pizzaClickListener.onPlusClick(adapterPosition);
            }
        });

        holder.minusImageView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                pizzaClickListener.onMinusClick(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public static class PizzaViewHolder extends RecyclerView.ViewHolder {
        ImageView pizzaImageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView quantityTextView;
        ImageView plusImageView;
        ImageView minusImageView;

        public PizzaViewHolder(@NonNull View itemView) {
            super(itemView);
            pizzaImageView = itemView.findViewById(R.id.pizzaImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            plusImageView = itemView.findViewById(R.id.plusImageView);
            minusImageView = itemView.findViewById(R.id.minusImageView);
        }
    }
}

