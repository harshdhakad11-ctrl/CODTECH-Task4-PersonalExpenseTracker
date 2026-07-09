package com.dhakad.personalexpensetracker.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dhakad.personalexpensetracker.R;
import com.dhakad.personalexpensetracker.database.DBHelper;
import com.dhakad.personalexpensetracker.databinding.ActivityAddTransactionBinding;
import com.dhakad.personalexpensetracker.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    // ViewBinding
    private ActivityAddTransactionBinding binding;

    // Database helper
    private DBHelper dbHelper;

    // Calendar object
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        calendar = Calendar.getInstance();

        setupSpinners();

        setupDatePicker();

        binding.btnSave.setOnClickListener(v -> saveTransaction());

    }

    /**
     * Initialize Spinner Data
     */
    private void setupSpinners() {

        ArrayAdapter<CharSequence> typeAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.transaction_type,
                        android.R.layout.simple_spinner_dropdown_item);

        binding.spType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> categoryAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.transaction_category,
                        android.R.layout.simple_spinner_dropdown_item);

        binding.spCategory.setAdapter(categoryAdapter);

    }

    /**
     * Opens Date Picker Dialog
     */
    private void setupDatePicker() {

        updateDate();

        binding.etDate.setOnClickListener(v -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTransactionActivity.this,
                    (view, year, month, dayOfMonth) -> {

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        updateDate();

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();

        });

    }

    /**
     * Update selected date in EditText
     */
    private void updateDate() {

        // Display date to user
        SimpleDateFormat displayFormat =
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        binding.etDate.setText(displayFormat.format(calendar.getTime()));

    }

    /**
     * Validate input and save transaction
     */
    private void saveTransaction() {

        String title = binding.etTitle.getText().toString().trim();
        String amountText = binding.etAmount.getText().toString().trim();

        String type = binding.spType.getText().toString().trim();
        String category = binding.spCategory.getText().toString().trim();

        // Save date in database as dd/MM/yyyy
        SimpleDateFormat databaseFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String date = databaseFormat.format(calendar.getTime());

        // Validate title
        if (title.isEmpty()) {

            binding.etTitle.setError("Enter transaction title");
            binding.etTitle.requestFocus();
            return;
        }

        // Validate amount
        if (amountText.isEmpty()) {

            binding.etAmount.setError("Enter amount");
            binding.etAmount.requestFocus();
            return;
        }

        double amount = Double.parseDouble(amountText);

        Transaction transaction = new Transaction(
                title,
                amount,
                type,
                category,
                date
        );

        boolean isInserted = dbHelper.insertTransaction(transaction);

        if (isInserted) {

            Toast.makeText(
                    this,
                    "Transaction Added Successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to Save Transaction",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }

}