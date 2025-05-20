package com.NT.banknotireader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.NT.banknotireader.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "transactions.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY, title TEXT, message TEXT, timestamp LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    public void insertTransaction(String title, String message, long timestamp) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("message", message);
        values.put("timestamp", timestamp);
        getWritableDatabase().insert("transactions", null, values);
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM transactions ORDER BY timestamp DESC", null);
        while (c.moveToNext()) {
            Transaction t = new Transaction();
            t.title = c.getString(1);
            t.message = c.getString(2);
            t.timestamp = c.getLong(3);
            list.add(t);
        }
        c.close();
        return list;
    }
}
