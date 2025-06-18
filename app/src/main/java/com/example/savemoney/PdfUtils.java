package com.example.savemoney;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfUtils {

    public static void generatePdf(Context context, List<Income> incomes, List<Expense> expenses, String title) {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 Size
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        int y = 50;

        // Title
        paint.setTextSize(20);
        canvas.drawText(title, 50, y, paint);

//===============================Income section start=========================================================

        y += 40;
        paint.setTextSize(16);
        canvas.drawText("Incomes", 50, y, paint);

        y += 30;
        double totalIncome = 0;

        for (Income income : incomes) {
            String line = "" + income.getReason() + " : " + income.getAmount();
            canvas.drawText(line, 50, y, paint);
            y += 25;
            totalIncome += income.getAmount();
        }

        y += 30;
        canvas.drawText("Total Income: " + totalIncome, 50, y, paint);

//==================================Expenses section start==================================================

        y += 50;
        canvas.drawText("Expenses", 50, y, paint);

        y += 30;
        double totalExpense = 0;

        for (Expense expense : expenses) {
            String line = "" + expense.getReason() + " : " + expense.getAmount();
            canvas.drawText(line, 50, y, paint);
            y += 25;
            totalExpense += expense.getAmount();
        }

        y += 30;
        canvas.drawText("Total Expense : " + totalExpense, 50, y, paint);

//=================================Main balance============================================================
        y += 50;
        double balance = totalIncome - totalExpense;
        canvas.drawText("Main Balance : " + balance, 50, y, paint);

        pdfDocument.finishPage(page);

//================================Page End==================================================================

//====================================== Save PDF file=========================================================
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SaveMoneyReports");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, title.replace(" ", "_") + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));

            // 👉 Open the PDF using FileProvider
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

            context.startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving PDF: " , Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
//============================Save pdf file end===============================================================

    }
}
