package com.anas.pizzeria.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.anas.pizzeria.data.local.PizzeriaDatabase;
import com.anas.pizzeria.data.local.dao.OrderDao;
import com.anas.pizzeria.data.local.entity.OrderEntity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {

    private final OrderDao orderDao;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;

    public OrderRepository(Application application) {
        PizzeriaDatabase db = PizzeriaDatabase.getInstance(application);
        orderDao = db.orderDao();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }

    // ============ ROOM (Lokalna baza) ============

    public void insertOrder(OrderEntity order, OnOrderInsertedCallback callback) {
        executor.execute(() -> {
            long id = orderDao.insertOrder(order);
            order.setOrderId(id);
            // Po lokalno snimanje, snimaj i vo Firestore
            saveToFirestore(order);
            if (callback != null) callback.onInserted(id);
        });
    }

    public LiveData<List<OrderEntity>> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public LiveData<List<OrderEntity>> getOrdersByUser(String userId) {
        return orderDao.getOrdersByUser(userId);
    }

    // ============ FIRESTORE (Cloud) ============

    private void saveToFirestore(OrderEntity order) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("customerName", order.getCustomerName());
        data.put("customerAddress", order.getCustomerAddress());
        data.put("totalCost", order.getTotalCost());
        data.put("pizzaItems", order.getPizzaItems());
        data.put("dateTime", order.getDateTime());
        data.put("firebaseUserId", order.getFirebaseUserId());

        firestore.collection("orders")
                .add(data)
                .addOnSuccessListener(ref -> {
                    // Uspesno snimeno vo Firestore
                })
                .addOnFailureListener(e -> {
                    // Greska - podatocite ostanuvaat lokalno vo Room
                });
    }

    public interface OnOrderInsertedCallback {
        void onInserted(long orderId);
    }
}
