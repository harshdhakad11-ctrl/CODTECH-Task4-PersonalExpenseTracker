package com.dhakad.personalexpensetracker.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dhakad.personalexpensetracker.database.DBHelper;
import com.dhakad.personalexpensetracker.databinding.ActivityEditTransactionBinding;
import com.dhakad.personalexpensetracker.model.Transaction;
import com.dhakad.personalexpensetracker.utils.Constants;

public class EditTransactionActivity extends AppCompatActivity {

    private ActivityEditTransactionBinding binding;
    private DBHelper dbHelper;

    private Transaction transaction;

    private final String[] types = {
            Constants.TYPE_INCOME,
            Constants.TYPE_EXPENSE
    };

    private final String[] categories = {
            "Salary",
            "Food",
            "Shopping",
            "Travel",
            "Bills",
            "Health",
            "Education",
            "Entertainment",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        setupSpinner();

        int id = getIntent().getIntExtra("id", -1);

        if (id == -1) {
            finish();
            return;
        }

        transaction = dbHelper.getTransactionById(id);

        if (transaction == null) {
            finish();
            return;
        }

        loadData();

        binding.btnUpdate.setOnClickListener(v -> updateTransaction());

    }

    private void setupSpinner() {

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        types
                );

        binding.spType.setAdapter(typeAdapter);

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories
                );

        binding.spCategory.setAdapter(categoryAdapter);

    }

    private void loadData() {

        binding.etTitle.setText(transaction.getTitle());

        binding.etAmount.setText(
                String.valueOf(transaction.getAmount())
        );

        for (int i = 0; i < types.length; i++) {

            if (types[i].equals(transaction.getType())) {

                binding.spType.setSelection(i);

                break;
            }

        }

        for (int i = 0; i < categories.length; i++) {

            if (categories[i].equals(transaction.getCategory())) {

                binding.spCategory.setSelection(i);

                break;
            }

        }

    }

    private void updateTransaction() {

        String title =
                binding.etTitle.getText().toString().trim();

        String amountText =
                binding.etAmount.getText().toString().trim();

        if (title.isEmpty() || amountText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        transaction.setTitle(title);

        transaction.setAmount(
                Double.parseDouble(amountText)
        );

        transaction.setType(
                binding.spType.getSelectedItem().toString()
        );

        transaction.setCategory(
                binding.spCategory.getSelectedItem().toString()
        );

        boolean success =
                dbHelper.updateTransaction(transaction);

        if (success) {

            Toast.makeText(
                    this,
                    "Transaction Updated",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Update Failed",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }

}