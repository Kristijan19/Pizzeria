package com.anas.pizzeria.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class OrderEntity {

    @PrimaryKey(autoGenerate = true)
    private long orderId;

    private String customerName;
    private String customerAddress;
    private double totalCost;
    private String pizzaItems;   // JSON string so listata na pici
    private String dateTime;
    private String firebaseUserId;

    public OrderEntity() {}

    public OrderEntity(String customerName, String customerAddress,
                       double totalCost, String pizzaItems,
                       String dateTime, String firebaseUserId) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.totalCost = totalCost;
        this.pizzaItems = pizzaItems;
        this.dateTime = dateTime;
        this.firebaseUserId = firebaseUserId;
    }

    // Getters
    public long getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerAddress() { return customerAddress; }
    public double getTotalCost() { return totalCost; }
    public String getPizzaItems() { return pizzaItems; }
    public String getDateTime() { return dateTime; }
    public String getFirebaseUserId() { return firebaseUserId; }

    // Setters
    public void setOrderId(long orderId) { this.orderId = orderId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public void setPizzaItems(String pizzaItems) { this.pizzaItems = pizzaItems; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setFirebaseUserId(String firebaseUserId) { this.firebaseUserId = firebaseUserId; }
}
