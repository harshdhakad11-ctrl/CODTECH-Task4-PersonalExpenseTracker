package com.dhakad.personalexpensetracker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.dhakad.personalexpensetracker.database.DBHelper;
import com.dhakad.personalexpensetracker.model.Transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PDFUtils {

    public static File generateExpenseReport(Context context) {

        PdfDocument pdfDocument = new PdfDocument();

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(22);
        titlePaint.setFakeBoldText(true);

        Paint textPaint = new Paint();
        textPaint.setTextSize(14);

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        int y = 50;

        canvas.drawText("PERSONAL EXPENSE TRACKER", 150, y, titlePaint);

        y += 30;

        String date = new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(new Date());

        canvas.drawText("Date : " + date, 40, y, textPaint);

        DBHelper db = new DBHelper(context);

        y += 40;

        canvas.drawText(
                "Total Income : ₹ " + db.getTotalIncome(),
                40,
                y,
                textPaint
        );

        y += 25;

        canvas.drawText(
                "Total Expense : ₹ " + db.getTotalExpense(),
                40,
                y,
                textPaint
        );

        y += 25;

        canvas.drawText(
                "Current Balance : ₹ " + db.getBalance(),
                40,
                y,
                textPaint
        );

        y += 40;

        canvas.drawText(
                "Transactions",
                40,
                y,
                titlePaint
        );

        y += 30;

        ArrayList<Transaction> list =
                db.getAllTransactions();

        for (Transaction t : list) {

            String line =
                    t.getDate()
                            + " | "
                            + t.getTitle()
                            + " | "
                            + t.getCategory()
                            + " | "
                            + t.getType()
                            + " | ₹"
                            + t.getAmount();

            canvas.drawText(
                    line,
                    40,
                    y,
                    textPaint
            );

            y += 20;

            if (y > 780) break;
        }

        pdfDocument.finishPage(page);

        File folder =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS
                );

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file =
                new File(
                        folder,
                        "Expense_Report.pdf"
                );

        try {

            pdfDocument.writeTo(
                    new FileOutputStream(file)
            );

            Toast.makeText(
                    context,
                    "PDF Saved Successfully",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            e.printStackTrace();

        }

        pdfDocument.close();

        return file;

    }

}