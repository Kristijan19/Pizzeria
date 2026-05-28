package com.anas.pizzeria.data;

import com.anas.pizzeria.model.PizzaMenuCatalog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MenuRepository {

    public interface PricesCallback {
        void onSuccess(Map<String, Double> prices);
        void onError(Exception e);
    }

    public interface SaveCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public void loadPrices(PricesCallback callback) {
        firestore.collection("menu_prices")
                .document(PizzaMenuCatalog.DOC_ID)
                .get()
                .addOnSuccessListener(doc -> {
                    Map<String, Double> defaults = PizzaMenuCatalog.getDefaultPrices();
                    if (!doc.exists()) {
                        callback.onSuccess(defaults);
                        return;
                    }
                    Map<String, Double> merged = new HashMap<>(defaults);
                    for (PizzaMenuCatalog.Entry entry : PizzaMenuCatalog.getEntries()) {
                        Double value = doc.getDouble(entry.id);
                        if (value != null && value > 0) {
                            merged.put(entry.id, value);
                        }
                    }
                    callback.onSuccess(merged);
                })
                .addOnFailureListener(callback::onError);
    }

    public void savePrices(Map<String, Double> prices, SaveCallback callback) {
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<String, Double> e : prices.entrySet()) {
            data.put(e.getKey(), e.getValue());
        }
        firestore.collection("menu_prices")
                .document(PizzaMenuCatalog.DOC_ID)
                .set(data)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }
}
