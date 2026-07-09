package com.dhakad.personalexpensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dhakad.personalexpensetracker.adapter.TransactionAdapter;
import com.dhakad.personalexpensetracker.adapter.TransactionClickListener;
import com.dhakad.personalexpensetracker.database.DBHelper;
import com.dhakad.personalexpensetracker.databinding.ActivityMainBinding;
import com.dhakad.personalexpensetracker.model.Transaction;

import java.util.ArrayList;
import java.util.Locale;

import android.view.LayoutInflater;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import com.dhakad.personalexpensetracker.R;

/**
 * Main dashboard screen of the application.
 * Displays balance, income, expense and transaction list.
 */
public class MainActivity extends AppCompatActivity
        implements TransactionClickListener {

    private ActivityMainBinding binding;

    private DBHelper dbHelper;

    private ArrayList<Transaction> transactionList;

    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();

        setupRecyclerView();

        setupFab();

        setupBudgetButton();

        loadBudget();

        setupBottomNavigation();

    }

    private void setupBottomNavigation() {

        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                return true;

            } else if (id == R.id.nav_statistics) {

                startActivity(
                        new Intent(
                                MainActivity.this,
                                StatisticsActivity.class
                        )
                );

                return true;

            } else if (id == R.id.nav_history) {

                startActivity(new Intent(this, HistoryActivity.class));
                return true;

            } else if (id == R.id.nav_settings) {

                startActivity(
                        new Intent(
                                MainActivity.this,
                                SettingsActivity.class
                        )
                );

                return true;
            }

            return false;

        });

    }

    /**
     * Initialize objects
     */
    private void initialize() {

        dbHelper = new DBHelper(this);

        transactionList = new ArrayList<>();

    }

    /**
     * Initialize RecyclerView
     */
    private void setupRecyclerView() {

        adapter = new TransactionAdapter(
                transactionList,
                this
        );

        binding.recyclerTransactions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.recyclerTransactions.setAdapter(adapter);

    }

    /**
     * Floating Action Button click
     */
    private void setupFab() {

        binding.fabAdd.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddTransactionActivity.class
            );

            startActivity(intent);

        });

        setupBudgetButton();
    }


    @Override
    protected void onResume() {
        super.onResume();

        loadTransactions();

        updateSummary();

        loadBudget();
    }
    /**
     * Load all transactions from SQLite database.
     */
    private void loadTransactions() {

        transactionList.clear();

        transactionList.addAll(dbHelper.getAllTransactions());

        adapter.notifyDataSetChanged();

        // Show message if no transaction is available
        if (transactionList.isEmpty()) {
            Toast.makeText(this,
                    "No transactions found",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Update dashboard summary cards.
     */
    private void updateSummary() {

        double income = dbHelper.getTotalIncome();
        double expense = dbHelper.getTotalExpense();
        double balance = dbHelper.getBalance();

        binding.tvIncome.setText(
                String.format(Locale.getDefault(), "₹ %.2f", income));

        binding.tvExpense.setText(
                String.format(Locale.getDefault(), "₹ %.2f", expense));

        binding.tvBalance.setText(
                String.format(Locale.getDefault(), "₹ %.2f", balance));
    }

    /**
     * Called when user taps a transaction.
     */
    @Override
    public void onTransactionClick(Transaction transaction) {

        Intent intent = new Intent(
                MainActivity.this,
                EditTransactionActivity.class
        );

        intent.putExtra("id", transaction.getId());

        startActivity(intent);

    }

    /**
     * Called when user long presses a transaction.
     */
    @Override
    public void onTransactionLongClick(Transaction transaction) {

        showDeleteDialog(transaction);
    }

    /**
     * Display confirmation dialog before deleting.
     */
    private void showDeleteDialog(@NonNull Transaction transaction) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    boolean deleted =
                            dbHelper.deleteTransaction(transaction.getId());

                    if (deleted) {

                        Toast.makeText(
                                this,
                                "Transaction deleted successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadTransactions();
                        updateSummary();

                    } else {

                        Toast.makeText(
                                this,
                                "Unable to delete transaction",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Open Budget Dialog
     */
    private void setupBudgetButton() {

        binding.btnSetBudget.setOnClickListener(v -> showBudgetDialog());

    }

    /**
     * Show Budget Dialog
     */
    private void showBudgetDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);

        android.view.View view =
                inflater.inflate(R.layout.dialog_budget, null);

        TextInputEditText etBudget =
                view.findViewById(R.id.etBudget);

        new MaterialAlertDialogBuilder(this)

                .setTitle("Monthly Budget")

                .setView(view)

                .setPositiveButton("Save", (dialog, which) -> {

                    String text =
                            etBudget.getText().toString().trim();

                    if (text.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Enter Budget",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    double amount =
                            Double.parseDouble(text);

                    Calendar calendar =
                            Calendar.getInstance();

                    int month =
                            calendar.get(Calendar.MONTH) + 1;

                    int year =
                            calendar.get(Calendar.YEAR);

                    dbHelper.saveBudget(
                            amount,
                            month,
                            year
                    );

                    loadBudget();

                    Toast.makeText(
                            this,
                            "Budget Saved",
                            Toast.LENGTH_SHORT
                    ).show();

                })

                .setNegativeButton("Cancel", null)

                .show();

    }

    /**
     * Load Budget
     */
    private void loadBudget() {

        Calendar calendar =
                Calendar.getInstance();

        int month =
                calendar.get(Calendar.MONTH) + 1;

        int year =
                calendar.get(Calendar.YEAR);

        double budget =
                dbHelper.getBudget(month, year);

        double remaining =
                dbHelper.getRemainingBudget(month, year);

        int percentage =
                dbHelper.getBudgetPercentage(month, year);

        double expense=dbHelper.getMonthlyExpense(month,year);

        binding.tvBudget.setText(
                "₹ " + budget
        );

        binding.tvRemainingBudget.setText(
                "₹ " + remaining
        );

        binding.progressBudget.setProgress(
                percentage
        );

        binding.tvBudgetPercentage.setText(
                percentage + "% Used"
        );

        // Budget Status
        if (percentage >= 100) {

            binding.tvBudgetStatus.setText("❌ Budget Exceeded");

        } else if (percentage >= 80) {

            binding.tvBudgetStatus.setText("🔴 Budget Almost Finished");

        } else if (percentage >= 60) {

            binding.tvBudgetStatus.setText("🟡 Budget Usage is High");

        } else {

            binding.tvBudgetStatus.setText("🟢 Budget is Healthy");


        }

    }
}