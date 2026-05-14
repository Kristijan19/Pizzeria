package com.anas.pizzeria.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.anas.pizzeria.data.local.entity.OrderEntity;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long insertOrder(OrderEntity order);

    @Query("SELECT * FROM orders ORDER BY orderId DESC")
    LiveData<List<OrderEntity>> getAllOrders();

    @Query("SELECT * FROM orders WHERE firebaseUserId = :userId ORDER BY orderId DESC")
    LiveData<List<OrderEntity>> getOrdersByUser(String userId);

    @Query("DELETE FROM orders")
    void deleteAll();
}
