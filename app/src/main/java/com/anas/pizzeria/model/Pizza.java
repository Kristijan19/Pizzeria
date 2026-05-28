package com.anas.pizzeria.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Pizza implements Parcelable {

    private final String name;
    private double price;
    private int quantity;
    private final int imageResId;

    public Pizza(String name, double price, int imageResId) {
        this.name = name;
        this.price = price;
        this.quantity = 0;
        this.imageResId = imageResId;
    }

    protected Pizza(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        imageResId = in.readInt();
    }

    public static final Creator<Pizza> CREATOR = new Creator<Pizza>() {
        @Override
        public Pizza createFromParcel(Parcel in) { return new Pizza(in); }

        @Override
        public Pizza[] newArray(int size) { return new Pizza[size]; }
    };

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getImageResId() { return imageResId; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setPrice(double price) { this.price = price; }

    public String getFormattedPrice() {
        return String.format("MKD %.0f", price);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeInt(imageResId);
    }
}
