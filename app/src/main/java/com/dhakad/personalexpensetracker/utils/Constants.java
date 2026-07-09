package com.dhakad.personalexpensetracker.utils;

public class Constants {

    // Database Information
    public static final String DATABASE_NAME = "expense_tracker.db";
    public static final int DATABASE_VERSION = 3;

    // Transaction Table
    public static final String TABLE_TRANSACTION = "transactions";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";

    // Transaction Types
    public static final String TYPE_INCOME = "Income";
    public static final String TYPE_EXPENSE = "Expense";

    // Budget Table
    public static final String TABLE_BUDGET = "budget";

    // Budget Columns
    public static final String COLUMN_BUDGET_ID = "budget_id";
    public static final String COLUMN_BUDGET_AMOUNT = "budget_amount";
    public static final String COLUMN_BUDGET_MONTH = "budget_month";
    public static final String COLUMN_BUDGET_YEAR = "budget_year";
}