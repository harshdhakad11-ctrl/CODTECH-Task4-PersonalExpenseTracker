package com.dhakad.personalexpensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.dhakad.personalexpensetracker.model.Transaction;
import com.dhakad.personalexpensetracker.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Database Helper Class
 * Handles all SQLite operations for Transactions and Budget.
 */
public class DBHelper extends SQLiteOpenHelper {

    // ===========================
    // Constructor
    // ===========================

    public DBHelper(@Nullable Context context) {
        super(
                context,
                Constants.DATABASE_NAME,
                null,
                Constants.DATABASE_VERSION
        );
    }

    // ===========================
    // Create Database Tables
    // ===========================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Transaction Table
        String CREATE_TRANSACTION_TABLE =
                "CREATE TABLE " + Constants.TABLE_TRANSACTION + " ("
                        + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + Constants.COLUMN_TITLE + " TEXT NOT NULL, "
                        + Constants.COLUMN_AMOUNT + " REAL NOT NULL, "
                        + Constants.COLUMN_TYPE + " TEXT NOT NULL, "
                        + Constants.COLUMN_CATEGORY + " TEXT NOT NULL, "
                        + Constants.COLUMN_DATE + " TEXT NOT NULL"
                        + ");";

        db.execSQL(CREATE_TRANSACTION_TABLE);

        // Budget Table
        String CREATE_BUDGET_TABLE =
                "CREATE TABLE " + Constants.TABLE_BUDGET + " ("
                        + Constants.COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + Constants.COLUMN_BUDGET_AMOUNT + " REAL NOT NULL, "
                        + Constants.COLUMN_BUDGET_MONTH + " INTEGER NOT NULL, "
                        + Constants.COLUMN_BUDGET_YEAR + " INTEGER NOT NULL"
                        + ");";

        db.execSQL(CREATE_BUDGET_TABLE);
    }

    // ===========================
    // Upgrade Database
    // ===========================

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_BUDGET);

        onCreate(db);
    }

    // ===========================
    // Insert Transaction
    // ===========================

    public boolean insertTransaction(Transaction transaction) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_TITLE, transaction.getTitle());
        values.put(Constants.COLUMN_AMOUNT, transaction.getAmount());
        values.put(Constants.COLUMN_TYPE, transaction.getType());
        values.put(Constants.COLUMN_CATEGORY, transaction.getCategory());
        values.put(Constants.COLUMN_DATE, transaction.getDate());

        long result = db.insert(
                Constants.TABLE_TRANSACTION,
                null,
                values
        );

        db.close();

        return result != -1;
    }

    // ===========================
    // Update Transaction
    // ===========================

    public boolean updateTransaction(Transaction transaction) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_TITLE, transaction.getTitle());
        values.put(Constants.COLUMN_AMOUNT, transaction.getAmount());
        values.put(Constants.COLUMN_TYPE, transaction.getType());
        values.put(Constants.COLUMN_CATEGORY, transaction.getCategory());
        values.put(Constants.COLUMN_DATE, transaction.getDate());

        int result = db.update(
                Constants.TABLE_TRANSACTION,
                values,
                Constants.COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(transaction.getId())
                }
        );

        db.close();

        return result > 0;
    }

    // ===========================
    // Delete Transaction
    // ===========================

    public boolean deleteTransaction(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                Constants.TABLE_TRANSACTION,
                Constants.COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );

        db.close();

        return result > 0;
    }

    // ===========================
    // Get All Transactions
    // ===========================

    public ArrayList<Transaction> getAllTransactions() {

        ArrayList<Transaction> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + Constants.TABLE_TRANSACTION +
                        " ORDER BY " + Constants.COLUMN_ID + " DESC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Transaction transaction = new Transaction();

                transaction.setId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(Constants.COLUMN_ID)
                        )
                );

                transaction.setTitle(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(Constants.COLUMN_TITLE)
                        )
                );

                transaction.setAmount(
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(Constants.COLUMN_AMOUNT)
                        )
                );

                transaction.setType(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(Constants.COLUMN_TYPE)
                        )
                );

                transaction.setCategory(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY)
                        )
                );

                transaction.setDate(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(Constants.COLUMN_DATE)
                        )
                );

                list.add(transaction);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return list;
    }

    // ===========================
    // Get Transaction By ID
    // ===========================

    public Transaction getTransactionById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                Constants.TABLE_TRANSACTION,
                null,
                Constants.COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null
        );

        Transaction transaction = null;

        if (cursor.moveToFirst()) {

            transaction = new Transaction();

            transaction.setId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_ID))
            );

            transaction.setTitle(
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_TITLE))
            );

            transaction.setAmount(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_AMOUNT))
            );

            transaction.setType(
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_TYPE))
            );

            transaction.setCategory(
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY))
            );

            transaction.setDate(
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATE))
            );

        }

        cursor.close();
        db.close();

        return transaction;
    }

    // ===========================
    // Save Monthly Budget
    // ===========================

    public boolean saveBudget(double amount, int month, int year) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Remove old budget of same month
        db.delete(
                Constants.TABLE_BUDGET,
                Constants.COLUMN_BUDGET_MONTH + "=? AND " +
                        Constants.COLUMN_BUDGET_YEAR + "=?",
                new String[]{
                        String.valueOf(month),
                        String.valueOf(year)
                }
        );

        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_BUDGET_AMOUNT, amount);
        values.put(Constants.COLUMN_BUDGET_MONTH, month);
        values.put(Constants.COLUMN_BUDGET_YEAR, year);

        long result = db.insert(
                Constants.TABLE_BUDGET,
                null,
                values
        );

        db.close();

        return result != -1;
    }

    // ===========================
    // Get Monthly Budget
    // ===========================

    public double getBudget(int month, int year) {

        SQLiteDatabase db = this.getReadableDatabase();

        double budget = 0;

        Cursor cursor = db.rawQuery(
                "SELECT " + Constants.COLUMN_BUDGET_AMOUNT +
                        " FROM " + Constants.TABLE_BUDGET +
                        " WHERE " +
                        Constants.COLUMN_BUDGET_MONTH + "=? AND " +
                        Constants.COLUMN_BUDGET_YEAR + "=?",
                new String[]{
                        String.valueOf(month),
                        String.valueOf(year)
                }
        );

        if (cursor.moveToFirst()) {

            budget = cursor.getDouble(0);

        }

        cursor.close();
        db.close();

        return budget;
    }

    // ===========================
    // Total Income
    // ===========================

    public double getTotalIncome() {

        SQLiteDatabase db = this.getReadableDatabase();

        double income = 0;

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + Constants.COLUMN_AMOUNT + ") FROM "
                        + Constants.TABLE_TRANSACTION
                        + " WHERE "
                        + Constants.COLUMN_TYPE + "=?",
                new String[]{
                        Constants.TYPE_INCOME
                }
        );

        if (cursor.moveToFirst()) {

            income = cursor.getDouble(0);

        }

        cursor.close();
        db.close();

        return income;
    }

    // ===========================
    // Total Expense
    // ===========================

    public double getTotalExpense() {

        SQLiteDatabase db = this.getReadableDatabase();

        double expense = 0;

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + Constants.COLUMN_AMOUNT + ") FROM "
                        + Constants.TABLE_TRANSACTION
                        + " WHERE "
                        + Constants.COLUMN_TYPE + "=?",
                new String[]{
                        Constants.TYPE_EXPENSE
                }
        );

        if (cursor.moveToFirst()) {

            expense = cursor.getDouble(0);

        }

        cursor.close();
        db.close();

        return expense;
    }

    // ===========================
    // Current Balance
    // ===========================

    public double getBalance() {

        return getTotalIncome() - getTotalExpense();

    }

    // ===========================
    // Remaining Budget
    // ===========================

    public double getRemainingBudget(int month, int year) {

        double budget = getBudget(month, year);

        double expense=getMonthlyExpense(month,year);

        return budget - expense;
    }

    // ===========================
    // Budget Used Percentage
    // ===========================

    public int getBudgetPercentage(int month, int year) {

        double budget = getBudget(month, year);

        if (budget <= 0) {

            return 0;

        }

        double expense=getMonthlyExpense(month,year);

        return (int) ((expense * 100) / budget);

    }

    public double getMonthlyExpense(int month, int year){

        SQLiteDatabase db=this.getReadableDatabase();

        double total=0;

        Cursor cursor=db.rawQuery(

                "SELECT SUM(" + Constants.COLUMN_AMOUNT + ") FROM "
                        + Constants.TABLE_TRANSACTION
                        + " WHERE "
                        + Constants.COLUMN_TYPE + "=?"
                        + " AND substr("
                        + Constants.COLUMN_DATE
                        + ",4,2)=?"
                        + " AND substr("
                        + Constants.COLUMN_DATE
                        + ",7,4)=?",

                new String[]{
                        Constants.TYPE_EXPENSE,
                        String.format("%02d",month),
                        String.valueOf(year)
                }

        );

        if(cursor.moveToFirst()){

            total=cursor.getDouble(0);

        }

        cursor.close();

        db.close();

        return total;

    }

    public void deleteAllTransactions() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                Constants.TABLE_TRANSACTION,
                null,
                null
        );

        db.close();

    }

    // ==========================
    // Expense by Category
    // ==========================

    public HashMap<String, Float> getExpenseByCategory() {

        HashMap<String, Float> map = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + Constants.COLUMN_CATEGORY +
                        ", SUM(" + Constants.COLUMN_AMOUNT + ")" +
                        " FROM " + Constants.TABLE_TRANSACTION +
                        " WHERE " + Constants.COLUMN_TYPE + "=? " +
                        " GROUP BY " + Constants.COLUMN_CATEGORY,
                new String[]{Constants.TYPE_EXPENSE}
        );

        if (cursor.moveToFirst()) {

            do {

                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);

                map.put(category, amount);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return map;
    }

    // ==========================
    // Total Transactions
    // ==========================

    public int getTransactionCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + Constants.TABLE_TRANSACTION,
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {

            count = cursor.getInt(0);

        }

        cursor.close();
        db.close();

        return count;
    }

    public String getTopExpenseCategory() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + Constants.COLUMN_CATEGORY +
                        ", SUM(" + Constants.COLUMN_AMOUNT + ") AS total " +
                        "FROM " + Constants.TABLE_TRANSACTION +
                        " WHERE " + Constants.COLUMN_TYPE + "=? " +
                        "GROUP BY " + Constants.COLUMN_CATEGORY +
                        " ORDER BY total DESC LIMIT 1",
                new String[]{Constants.TYPE_EXPENSE}
        );

        String category = "No Data";

        if (cursor.moveToFirst()) {
            category = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return category;
    }

    public double getTopExpenseAmount() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + Constants.COLUMN_AMOUNT + ") AS total " +
                        "FROM " + Constants.TABLE_TRANSACTION +
                        " WHERE " + Constants.COLUMN_TYPE + "=? " +
                        "GROUP BY " + Constants.COLUMN_CATEGORY +
                        " ORDER BY total DESC LIMIT 1",
                new String[]{Constants.TYPE_EXPENSE}
        );

        double amount = 0;

        if (cursor.moveToFirst()) {
            amount = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return amount;
    }

    // ===========================
    // Category Wise Expense
    // ===========================

    public java.util.HashMap<String, Float> getCategoryExpenses() {

        java.util.HashMap<String, Float> map = new java.util.HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + Constants.COLUMN_CATEGORY +
                        ", SUM(" + Constants.COLUMN_AMOUNT + ") " +
                        "FROM " + Constants.TABLE_TRANSACTION +
                        " WHERE " + Constants.COLUMN_TYPE + "=? " +
                        "GROUP BY " + Constants.COLUMN_CATEGORY,
                new String[]{Constants.TYPE_EXPENSE}
        );

        if (cursor.moveToFirst()) {

            do {

                String category = cursor.getString(0);

                float amount = cursor.getFloat(1);

                map.put(category, amount);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return map;
    }
}
