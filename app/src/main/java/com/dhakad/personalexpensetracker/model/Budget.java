package com.dhakad.personalexpensetracker.model;

/**
 * Budget Model Class
 * Stores monthly budget information.
 */
public class Budget {

    // Budget ID
    private int id;

    // Budget Amount
    private double amount;

    // Month (1-12)
    private int month;

    // Year
    private int year;

    // Empty Constructor
    public Budget() {
    }

    // Constructor
    public Budget(double amount, int month, int year) {
        this.amount = amount;
        this.month = month;
        this.year = year;
    }

    // Getter & Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}