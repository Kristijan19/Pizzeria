package com.anas.pizzeria.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.pizzeria.R;
import com.anas.pizzeria.util.OrderEmailNotifier;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder> {

    private List<DocumentSnapshot> orders = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String STATUS_RECEIVED = "RECEIVED";
    private static final String STATUS_PREPARING = "PREPARING";
    private static final String STATUS_ON_THE_WAY = "ON_THE_WAY";
    private static final String STATUS_DELIVERED = "DELIVERED";

    public void setOrders(List<DocumentSnapshot> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new AdminOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int position) {
        DocumentSnapshot doc = orders.get(position);
        holder.tvName.setText("👤 " + safe(doc.getString("customerName")));
        holder.tvAddress.setText("📍 " + safe(doc.getString("customerAddress")));
        holder.tvTotal.setText("💰 MKD " + formatTotal(doc.getDouble("totalCost")));
        holder.tvDate.setText("📅 " + safe(doc.getString("dateTime")));

        String currentStatus = doc.getString("status");
        if (currentStatus == null || currentStatus.trim().isEmpty()) {
            currentStatus = STATUS_RECEIVED;
        }
        holder.tvStatus.setText("📦 " + getStatusLabel(holder.itemView, currentStatus));

        String nextStatus = getNextStatus(currentStatus);
        if (nextStatus == null) {
            holder.btnNextStatus.setEnabled(false);
            holder.btnNextStatus.setText(R.string.admin_status_final);
        } else {
            holder.btnNextStatus.setEnabled(true);
            holder.btnNextStatus.setText(holder.itemView.getContext().getString(
                    R.string.admin_mark_status_template,
                    getStatusLabel(holder.itemView, nextStatus)
            ));
        }

        holder.btnNextStatus.setOnClickListener(v -> {
            String statusNow = doc.getString("status");
            if (statusNow == null || statusNow.trim().isEmpty()) {
                statusNow = STATUS_RECEIVED;
            }
            String newStatus = getNextStatus(statusNow);
            if (newStatus == null) return;

            db.collection("orders")
                    .document(doc.getId())
                    .update("status", newStatus)
                    .addOnSuccessListener(unused -> {
                        String email = doc.getString("customerEmail");
                        String name = doc.getString("customerName");
                        OrderEmailNotifier.sendStatusUpdate(
                                holder.itemView.getContext(),
                                email,
                                name,
                                newStatus
                        );
                        Toast.makeText(
                                holder.itemView.getContext(),
                                holder.itemView.getContext().getString(R.string.admin_status_updated),
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(
                            holder.itemView.getContext(),
                            holder.itemView.getContext().getString(R.string.admin_status_update_failed),
                            Toast.LENGTH_SHORT
                    ).show());
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class AdminOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvTotal, tvDate, tvStatus;
        MaterialButton btnNextStatus;

        AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvAdminName);
            tvAddress = itemView.findViewById(R.id.tvAdminAddress);
            tvTotal   = itemView.findViewById(R.id.tvAdminTotal);
            tvDate    = itemView.findViewById(R.id.tvAdminDate);
            tvStatus  = itemView.findViewById(R.id.tvAdminStatus);
            btnNextStatus = itemView.findViewById(R.id.btnAdminNextStatus);
        }
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }

    private String formatTotal(Double total) {
        return String.format(Locale.getDefault(), "%.2f", total == null ? 0.0 : total);
    }

    private String getNextStatus(String current) {
        if (STATUS_RECEIVED.equals(current)) return STATUS_PREPARING;
        if (STATUS_PREPARING.equals(current)) return STATUS_ON_THE_WAY;
        if (STATUS_ON_THE_WAY.equals(current)) return STATUS_DELIVERED;
        return null;
    }

    private String getStatusLabel(View view, String status) {
        if (STATUS_RECEIVED.equals(status)) return view.getContext().getString(R.string.admin_status_received);
        if (STATUS_PREPARING.equals(status)) return view.getContext().getString(R.string.admin_status_preparing);
        if (STATUS_ON_THE_WAY.equals(status)) return view.getContext().getString(R.string.admin_status_on_the_way);
        if (STATUS_DELIVERED.equals(status)) return view.getContext().getString(R.string.admin_status_delivered);
        return status;
    }
}