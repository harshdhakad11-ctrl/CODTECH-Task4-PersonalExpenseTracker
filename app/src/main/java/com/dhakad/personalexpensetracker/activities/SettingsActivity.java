package com.dhakad.personalexpensetracker.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.dhakad.personalexpensetracker.database.DBHelper;
import com.dhakad.personalexpensetracker.databinding.ActivitySettingsBinding;

import android.content.Intent;
import androidx.core.content.FileProvider;
import com.dhakad.personalexpensetracker.utils.PDFUtils;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;


    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);


        setupListeners();

    }


    private void setupListeners() {

        binding.btnReset.setOnClickListener(v -> showResetDialog());

        binding.btnAbout.setOnClickListener(v -> showAbout());

        binding.btnExportPdf.setOnClickListener(v -> {

            File file = PDFUtils.generateExpenseReport(this);

            if (file == null)
                return;

            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("application/pdf");

            intent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".provider",
                            file
                    )
            );

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(
                    Intent.createChooser(
                            intent,
                            "Share Expense Report"
                    )
            );

        });
    }

    private void showResetDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Reset Data")

                .setMessage("Delete all transactions?")

                .setPositiveButton("Delete", (dialog, which) -> {

                    dbHelper.getWritableDatabase()
                            .delete("transactions", null, null);

                    Toast.makeText(
                            this,
                            "All Data Deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                })

                .setNegativeButton("Cancel", null)

                .show();

    }

    private void showAbout() {

        new AlertDialog.Builder(this)

                .setTitle("Personal Expense Tracker")

                .setMessage(
                        "Version 1.0\n\n" +
                                "Developed by Harsh Dhakad\n\n" +
                                "Track Income, Expenses and Budget easily."
                )

                .setPositiveButton("OK", null)

                .show();

    }

}