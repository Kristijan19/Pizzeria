package com.anas.pizzeria.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.anas.pizzeria.data.OrderRepository;
import com.anas.pizzeria.data.local.entity.OrderEntity;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final OrderRepository repository;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderRepository(application);
    }

    public LiveData<List<OrderEntity>> getOrdersByUser(String userId) {
        return repository.getOrdersByUser(userId);
    }

    public LiveData<List<OrderEntity>> getAllOrders() {
        return repository.getAllOrders();
    }
}
