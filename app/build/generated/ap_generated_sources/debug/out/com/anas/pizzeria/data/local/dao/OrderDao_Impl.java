package com.anas.pizzeria.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.anas.pizzeria.data.local.entity.OrderEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class OrderDao_Impl implements OrderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OrderEntity> __insertionAdapterOfOrderEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public OrderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOrderEntity = new EntityInsertionAdapter<OrderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `orders` (`orderId`,`customerName`,`customerAddress`,`totalCost`,`pizzaItems`,`dateTime`,`firebaseUserId`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final OrderEntity entity) {
        statement.bindLong(1, entity.getOrderId());
        if (entity.getCustomerName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCustomerName());
        }
        if (entity.getCustomerAddress() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCustomerAddress());
        }
        statement.bindDouble(4, entity.getTotalCost());
        if (entity.getPizzaItems() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPizzaItems());
        }
        if (entity.getDateTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDateTime());
        }
        if (entity.getFirebaseUserId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFirebaseUserId());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM orders";
        return _query;
      }
    };
  }

  @Override
  public long insertOrder(final OrderEntity order) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfOrderEntity.insertAndReturnId(order);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public LiveData<List<OrderEntity>> getAllOrders() {
    final String _sql = "SELECT * FROM orders ORDER BY orderId DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"orders"}, false, new Callable<List<OrderEntity>>() {
      @Override
      @Nullable
      public List<OrderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "orderId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfCustomerAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "customerAddress");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfPizzaItems = CursorUtil.getColumnIndexOrThrow(_cursor, "pizzaItems");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfFirebaseUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseUserId");
          final List<OrderEntity> _result = new ArrayList<OrderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OrderEntity _item;
            _item = new OrderEntity();
            final long _tmpOrderId;
            _tmpOrderId = _cursor.getLong(_cursorIndexOfOrderId);
            _item.setOrderId(_tmpOrderId);
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            _item.setCustomerName(_tmpCustomerName);
            final String _tmpCustomerAddress;
            if (_cursor.isNull(_cursorIndexOfCustomerAddress)) {
              _tmpCustomerAddress = null;
            } else {
              _tmpCustomerAddress = _cursor.getString(_cursorIndexOfCustomerAddress);
            }
            _item.setCustomerAddress(_tmpCustomerAddress);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            _item.setTotalCost(_tmpTotalCost);
            final String _tmpPizzaItems;
            if (_cursor.isNull(_cursorIndexOfPizzaItems)) {
              _tmpPizzaItems = null;
            } else {
              _tmpPizzaItems = _cursor.getString(_cursorIndexOfPizzaItems);
            }
            _item.setPizzaItems(_tmpPizzaItems);
            final String _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getString(_cursorIndexOfDateTime);
            }
            _item.setDateTime(_tmpDateTime);
            final String _tmpFirebaseUserId;
            if (_cursor.isNull(_cursorIndexOfFirebaseUserId)) {
              _tmpFirebaseUserId = null;
            } else {
              _tmpFirebaseUserId = _cursor.getString(_cursorIndexOfFirebaseUserId);
            }
            _item.setFirebaseUserId(_tmpFirebaseUserId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<OrderEntity>> getOrdersByUser(final String userId) {
    final String _sql = "SELECT * FROM orders WHERE firebaseUserId = ? ORDER BY orderId DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"orders"}, false, new Callable<List<OrderEntity>>() {
      @Override
      @Nullable
      public List<OrderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "orderId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfCustomerAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "customerAddress");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfPizzaItems = CursorUtil.getColumnIndexOrThrow(_cursor, "pizzaItems");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfFirebaseUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseUserId");
          final List<OrderEntity> _result = new ArrayList<OrderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OrderEntity _item;
            _item = new OrderEntity();
            final long _tmpOrderId;
            _tmpOrderId = _cursor.getLong(_cursorIndexOfOrderId);
            _item.setOrderId(_tmpOrderId);
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            _item.setCustomerName(_tmpCustomerName);
            final String _tmpCustomerAddress;
            if (_cursor.isNull(_cursorIndexOfCustomerAddress)) {
              _tmpCustomerAddress = null;
            } else {
              _tmpCustomerAddress = _cursor.getString(_cursorIndexOfCustomerAddress);
            }
            _item.setCustomerAddress(_tmpCustomerAddress);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            _item.setTotalCost(_tmpTotalCost);
            final String _tmpPizzaItems;
            if (_cursor.isNull(_cursorIndexOfPizzaItems)) {
              _tmpPizzaItems = null;
            } else {
              _tmpPizzaItems = _cursor.getString(_cursorIndexOfPizzaItems);
            }
            _item.setPizzaItems(_tmpPizzaItems);
            final String _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getString(_cursorIndexOfDateTime);
            }
            _item.setDateTime(_tmpDateTime);
            final String _tmpFirebaseUserId;
            if (_cursor.isNull(_cursorIndexOfFirebaseUserId)) {
              _tmpFirebaseUserId = null;
            } else {
              _tmpFirebaseUserId = _cursor.getString(_cursorIndexOfFirebaseUserId);
            }
            _item.setFirebaseUserId(_tmpFirebaseUserId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
