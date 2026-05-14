package com.anas.pizzeria.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.anas.pizzeria.data.local.dao.OrderDao;
import com.anas.pizzeria.data.local.entity.OrderEntity;

@Database(entities = {OrderEntity.class}, version = 1, exportSchema = false)
public abstract class PizzeriaDatabase extends RoomDatabase {

    private static volatile PizzeriaDatabase INSTANCE;
    private static final String DB_NAME = "pizzeria_db";

    public abstract OrderDao orderDao();

    public static PizzeriaDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PizzeriaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            PizzeriaDatabase.class,
                            DB_NAME
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
