package com.dhakad.personalexpensetracker.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dhakad.personalexpensetracker.database.DBHelper;
import com.dhakad.personalexpensetracker.databinding.ActivityStatisticsBinding;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private ActivityStatisticsBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        loadStatistics();
    }

    private void loadStatistics() {

        double income = dbHelper.getTotalIncome();
        double expense = dbHelper.getTotalExpense();
        double balance = dbHelper.getBalance();

        binding.tvTotalIncome.setText(
                String.format(Locale.getDefault(),
                        "Income : ₹ %.2f",
                        income));

        binding.tvTotalExpense.setText(
                String.format(Locale.getDefault(),
                        "Expense : ₹ %.2f",
                        expense));

        binding.tvNetBalance.setText(
                String.format(Locale.getDefault(),
                        "Balance : ₹ %.2f",
                        balance));

        setupCategoryPieChart();
    }

    // ===========================
    // Setup Category Wise Pie Chart
    // ===========================

    private void setupCategoryPieChart() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        java.util.HashMap<String, Float> categoryMap =
                dbHelper.getCategoryExpenses();

        if (categoryMap.isEmpty()) {

            binding.pieChart.clear();

            binding.pieChart.setNoDataText("No Expense Data Available");

            return;
        }

        for (String category : categoryMap.keySet()) {

            entries.add(
                    new PieEntry(
                            categoryMap.get(category),
                            category
                    )
            );

        }

        PieDataSet dataSet =
                new PieDataSet(entries, "Expense by Category");

        // Chart Colors
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        dataSet.setValueTextSize(14f);

        dataSet.setValueTextColor(Color.WHITE);

        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);

        binding.pieChart.setData(data);

        // Chart Settings
        binding.pieChart.getDescription().setEnabled(false);

        binding.pieChart.setCenterText("Expenses");

        binding.pieChart.setCenterTextSize(20f);

        binding.pieChart.setHoleRadius(50f);

        binding.pieChart.setTransparentCircleRadius(55f);

        binding.pieChart.setUsePercentValues(true);

        binding.pieChart.setEntryLabelColor(Color.BLACK);

        binding.pieChart.setEntryLabelTextSize(12f);

        binding.pieChart.animateY(1500);

        // Legend Settings
        binding.pieChart.getLegend().setEnabled(true);
        binding.pieChart.getLegend().setTextSize(12f);
        binding.pieChart.getLegend().setWordWrapEnabled(true);

        // Extra Padding
        binding.pieChart.setExtraOffsets(10f, 10f, 10f, 10f);

        // Rotation
        binding.pieChart.setRotationEnabled(true);
        binding.pieChart.setHighlightPerTapEnabled(true);

        binding.pieChart.invalidate();
    }
}