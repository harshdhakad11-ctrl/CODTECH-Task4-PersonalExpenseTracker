package com.dhakad.personalexpensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dhakad.personalexpensetracker.adapter.TransactionAdapter;
import com.dhakad.personalexpensetracker.adapter.TransactionClickListener;
import com.dhakad.personalexpensetracker.database.DBHelper;
import com.dhakad.personalexpensetracker.databinding.ActivityHistoryBinding;
import com.dhakad.personalexpensetracker.model.Transaction;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity
        implements TransactionClickListener {

    private ActivityHistoryBinding binding;

    private DBHelper dbHelper;

    private ArrayList<Transaction> transactionList;
    private ArrayList<Transaction> originalList;

    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        refreshList();

        adapter = new TransactionAdapter(
                transactionList,
                this
        );

        binding.recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.recyclerHistory.setAdapter(adapter);

        setupSearch();
    }

    /**
     * Search Transactions
     */
    private void setupSearch() {

        binding.etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                String query = s.toString().trim().toLowerCase();

                ArrayList<Transaction> filteredList = new ArrayList<>();

                if (query.isEmpty()) {

                    filteredList.addAll(originalList);

                } else {

                    for (Transaction transaction : originalList) {

                        if (transaction.getTitle().toLowerCase().contains(query)
                                || transaction.getCategory().toLowerCase().contains(query)
                                || transaction.getType().toLowerCase().contains(query)
                                || transaction.getDate().toLowerCase().contains(query)
                                || String.valueOf(transaction.getAmount()).contains(query)) {

                            filteredList.add(transaction);

                        }

                    }

                }

                adapter.updateList(filteredList);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    /**
     * Reload Transactions
     */
    private void refreshList() {

        originalList = dbHelper.getAllTransactions();

        transactionList = new ArrayList<>(originalList);

    }

    @Override
    protected void onResume() {
        super.onResume();

        originalList = dbHelper.getAllTransactions();

        transactionList.clear();
        transactionList.addAll(originalList);

        adapter.notifyDataSetChanged();
    }

    /**
     * Edit Transaction
     */
    @Override
    public void onTransactionClick(Transaction transaction) {

        Intent intent = new Intent(
                HistoryActivity.this,
                EditTransactionActivity.class
        );

        intent.putExtra("id", transaction.getId());

        startActivity(intent);

    }

    /**
     * Delete Transaction
     */
    @Override
    public void onTransactionLongClick(Transaction transaction) {

        new AlertDialog.Builder(this)

                .setTitle("Delete Transaction")

                .setMessage("Are you sure you want to delete this transaction?")

                .setPositiveButton("Delete", (dialog, which) -> {

                    boolean deleted =
                            dbHelper.deleteTransaction(transaction.getId());

                    if (deleted) {

                        originalList = dbHelper.getAllTransactions();

                        transactionList.clear();
                        transactionList.addAll(originalList);

                        adapter.notifyDataSetChanged();

                        Toast.makeText(
                                this,
                                "Transaction Deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Delete Failed",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                })

                .setNegativeButton("Cancel", null)

                .show();

    }

}