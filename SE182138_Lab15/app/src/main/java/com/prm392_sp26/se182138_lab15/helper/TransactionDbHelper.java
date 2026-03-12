package com.prm392_sp26.se182138_lab15.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "transactions.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE = "transactions";
    private static final String COL_ID = "id";
    private static final String COL_PRODUCT = "product_name";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_METHOD = "method";
    private static final String COL_STATUS = "status";
    private static final String COL_CREATED_AT = "created_at";

    public TransactionDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PRODUCT + " TEXT NOT NULL, "
                + COL_QUANTITY + " INTEGER NOT NULL, "
                + COL_AMOUNT + " INTEGER NOT NULL, "
                + COL_METHOD + " TEXT NOT NULL, "
                + COL_STATUS + " TEXT NOT NULL, "
                + COL_CREATED_AT + " INTEGER NOT NULL"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insertTransaction(String productName, int quantity, int amount, String method, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT, productName == null ? "" : productName);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_AMOUNT, amount);
        values.put(COL_METHOD, method == null ? "" : method);
        values.put(COL_STATUS, status == null ? "" : status);
        values.put(COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE, null, values);
    }

    public List<String> getTransactionSummaries() {
        List<String> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String orderBy = COL_CREATED_AT + " DESC";
        String[] columns = {
                COL_PRODUCT,
                COL_QUANTITY,
                COL_AMOUNT,
                COL_METHOD,
                COL_STATUS,
                COL_CREATED_AT
        };

        try (Cursor cursor = db.query(TABLE, columns, null, null, null, null, orderBy)) {
            int idxProduct = cursor.getColumnIndexOrThrow(COL_PRODUCT);
            int idxQuantity = cursor.getColumnIndexOrThrow(COL_QUANTITY);
            int idxAmount = cursor.getColumnIndexOrThrow(COL_AMOUNT);
            int idxMethod = cursor.getColumnIndexOrThrow(COL_METHOD);
            int idxStatus = cursor.getColumnIndexOrThrow(COL_STATUS);
            int idxCreatedAt = cursor.getColumnIndexOrThrow(COL_CREATED_AT);

            DateFormat df = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,
                    DateFormat.SHORT,
                    Locale.getDefault()
            );

            while (cursor.moveToNext()) {
                String product = cursor.getString(idxProduct);
                int quantity = cursor.getInt(idxQuantity);
                int amount = cursor.getInt(idxAmount);
                String method = cursor.getString(idxMethod);
                String status = cursor.getString(idxStatus);
                long createdAt = cursor.getLong(idxCreatedAt);

                String time = df.format(new Date(createdAt));
                StringBuilder line = new StringBuilder();
                line.append(time)
                        .append(" | ")
                        .append(method)
                        .append(" | ")
                        .append(status)
                        .append(" | ");
                if (product == null || product.isEmpty()) {
                    line.append("San pham");
                } else {
                    line.append(product);
                }
                line.append(" x").append(Math.max(0, quantity));
                line.append(" | ").append(formatCurrency(amount));
                items.add(line.toString());
            }
        }

        return items;
    }

    private String formatCurrency(int value) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(
                Locale.forLanguageTag("vi-VN")
        );
        return nf.format(value) + " VND";
    }
}
