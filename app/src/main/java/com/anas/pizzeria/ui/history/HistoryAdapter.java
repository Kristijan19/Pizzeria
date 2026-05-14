package com.anas.pizzeria.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.pizzeria.R;
import com.anas.pizzeria.data.local.entity.OrderEntity;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<OrderEntity> orders = new ArrayList<>();

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvName, tvAddress, tvTotal, tvDate, tvPizzas;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId  = itemView.findViewById(R.id.tvOrderId);
            tvName     = itemView.findViewById(R.id.tvCustomerName);
            tvAddress  = itemView.findViewById(R.id.tvCustomerAddress);
            tvTotal    = itemView.findViewById(R.id.tvTotal);
            tvDate     = itemView.findViewById(R.id.tvDate);
            tvPizzas   = itemView.findViewById(R.id.tvPizzaItems);
        }

        void bind(OrderEntity order) {
            tvOrderId.setText(itemView.getContext().getString(
                    R.string.order_number, order.getOrderId()));
            tvName.setText(order.getCustomerName());
            tvAddress.setText(order.getCustomerAddress());
            tvTotal.setText(itemView.getContext().getString(
                    R.string.total_label, order.getTotalCost()));
            tvDate.setText(order.getDateTime());
            tvPizzas.setText(order.getPizzaItems());
        }
    }
}
