package com.anas.pizzeria.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.anas.pizzeria.data.local.PizzeriaDatabase;
import com.anas.pizzeria.data.local.dao.OrderDao;
import com.anas.pizzeria.data.local.entity.OrderEntity;
import com.anas.pizzeria.util.OrderEmailNotifier;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {

    private final Application application;
    private final OrderDao orderDao;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;

    public OrderRepository(Application application) {
        this.application = application;
        PizzeriaDatabase db = PizzeriaDatabase.getInstance(application);
        orderDao = db.orderDao();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }

    // ============ ROOM (Lokalna baza) ============

    public void insertOrder(OrderEntity order, String userEmail, OnOrderInsertedCallback callback) {
        executor.execute(() -> {
            long id = orderDao.insertOrder(order);
            order.setOrderId(id);
            saveToFirestore(order, userEmail);
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

    private void saveToFirestore(OrderEntity order, String userEmail) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("customerName", order.getCustomerName());
        data.put("customerAddress", order.getCustomerAddress());
        data.put("totalCost", order.getTotalCost());
        data.put("pizzaItems", order.getPizzaItems());
        data.put("dateTime", order.getDateTime());
        data.put("firebaseUserId", order.getFirebaseUserId());
        data.put("status", "RECEIVED");
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            data.put("customerEmail", userEmail);
        }

        firestore.collection("orders")
                .add(data)
                .addOnSuccessListener(ref -> queueOrderConfirmationEmailFallback(order, userEmail))
                .addOnFailureListener(e -> {
                    // Greska - podatocite ostanuvaat lokalno vo Room
                });
    }

    private void queueOrderConfirmationEmailFallback(OrderEntity order, String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) return;
        if (com.anas.pizzeria.util.EmailJsService.isConfigured(application)) return;

        Map<String, Object> mail = new HashMap<>();
        mail.put("to", java.util.Collections.singletonList(userEmail));

        Map<String, Object> message = new HashMap<>();
        message.put("subject", "Pizzeria - Potvrda za naracka");

        String text = "Zdravo " + order.getCustomerName() + ",\n\n"
                + "Narackata e primena. Ke vi bide dostavena za 1 cas.\n\n"
                + "Datum na naracka: " + order.getDateTime() + "\n"
                + "Adresa za dostava: " + order.getCustomerAddress() + "\n"
                + "Vkupno: MKD " + order.getTotalCost() + "\n\n"
                + "Vi blagodarime sto ja izbravte Pizzeria.";
        message.put("text", text);

        String html = "<p>Zdravo " + order.getCustomerName() + ",</p>"
                + "<p><b>Narackata e primena. Ke vi bide dostavena za 1 cas.</b></p>"
                + "<p><b>Datum na naracka:</b> " + order.getDateTime() + "<br/>"
                + "<b>Adresa za dostava:</b> " + order.getCustomerAddress() + "<br/>"
                + "<b>Vkupno:</b> MKD " + order.getTotalCost() + "</p>"
                + "<p>Vi blagodarime sto ja izbravte Pizzeria.</p>";
        message.put("html", html);
        mail.put("message", message);

        firestore.collection("mail").add(mail);
    }

    public interface OnOrderInsertedCallback {
        void onInserted(long orderId);
    }
}
