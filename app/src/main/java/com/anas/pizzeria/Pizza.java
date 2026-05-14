package com.anas.pizzeria;

import android.os.Parcel;
import android.os.Parcelable;

public class Pizza implements Parcelable {
    private final String name;
    private final String price;
    private int quantity;

    public Pizza(String name, String price) {
        this.name = name;
        this.price = price;
        this.quantity = 0;
    }

    protected Pizza(Parcel in) {
        name = in.readString();
        price = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<Pizza> CREATOR = new Creator<Pizza>() {
        @Override
        public Pizza createFromParcel(Parcel in) {
            return new Pizza(in);
        }

        @Override
        public Pizza[] newArray(int size) {
            return new Pizza[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeInt(quantity);
    }
}
