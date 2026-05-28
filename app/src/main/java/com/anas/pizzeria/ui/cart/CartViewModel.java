package com.anas.pizzeria.ui.cart;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.anas.pizzeria.data.OrderRepository;
import com.anas.pizzeria.data.local.entity.OrderEntity;

public class CartViewModel extends AndroidViewModel {

    private final OrderRepository repository;
    private final MutableLiveData<Boolean> orderSaved = new MutableLiveData<>();
    private OrderEntity lastSavedOrder;

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderRepository(application);
    }

    public MutableLiveData<Boolean> getOrderSaved() {
        return orderSaved;
    }

    public OrderEntity getLastSavedOrder() {
        return lastSavedOrder;
    }

    public void saveOrder(String name, String address, double total,
                          String pizzaJson, String dateTime, String userId, String userEmail) {
        OrderEntity order = new OrderEntity(name, address, total, pizzaJson, dateTime, userId);
        repository.insertOrder(order, userEmail, id -> {
            order.setOrderId(id);
            lastSavedOrder = order;
            orderSaved.postValue(true);
        });
    }
}
