package com.example.savemoney;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvtotalexpense, tvexpense,
            tvtotalincome, tvincome,
            tvmainbalance, tvreport;
    private final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat DB_DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private DataBaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        setContentView(R.layout.activity_main);
        tvtotalexpense = findViewById(R.id.tvtotalexpense);
        tvexpense      = findViewById(R.id.tvexpense);
        tvtotalincome  = findViewById(R.id.tvtotalincome);
        tvincome       = findViewById(R.id.tvincome);
        tvmainbalance  = findViewById(R.id.tvmainbalance);
        tvreport =findViewById(R.id.tvreport);
        dbhelper = new DataBaseHelper(this);

        updateUi();


        tvincome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowDataList.SAVEMONEY=true;
                startActivity(new Intent(MainActivity.this,ShowDataList.class));

            }
        });

        tvexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowDataList.SAVEMONEY=false;
                startActivity(new Intent(MainActivity.this,ShowDataList.class));

            }
        });




        tvreport.setOnClickListener(v -> showReportOptionDialog());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateUi();            // অন্য স্ক্রিন থেকে ফিরে এলে রিফ্রেশ
    }



    // ------------------------------------------------------------------ core UI refresh
    private void updateUi() {
        String today = DB_DATE_FMT.format(calendar.getTime());

        // ইনকাম
        List<Income> incomes = dbhelper.getIncomesByDate(today);
        double incomeSum = 0;
        for (Income inc : incomes) incomeSum += inc.getAmount();

        // এক্সপেন্স
        List<Expense> expenses = dbhelper.getExpensesByDate(today);
        double expenseSum = 0;
        for (Expense exp : expenses) expenseSum += exp.getAmount();

        double balance = incomeSum - expenseSum;

        // UI‑তে বসানো
        tvtotalincome.setText("BDT: " + incomeSum);
        tvtotalexpense.setText("BDT: " + expenseSum);
        tvmainbalance.setText("Main Balance: " + balance + " ৳");
    }

    // ------------------------------------------------------------------ report dialogs
    private void showReportOptionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Select Report Type")
                .setItems(new CharSequence[]{"Daily", "Monthly", "Yearly"}, (d, which) -> {
                    switch (which) {
                        case 0: showDatePickerDialog();   break;
                        case 1: showMonthPickerDialog();  break;
                        case 2: showYearPickerDialog();   break;
                    }
                }).show();
    }

    private void showDatePickerDialog() {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, yy, mm, dd) -> {
            calendar.set(yy, mm, dd);
            String selected = DB_DATE_FMT.format(calendar.getTime());
            generateDailyPdf(selected);
        }, y, m, d).show();
    }

    private void showMonthPickerDialog() {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.dialog_month_year_picke, null);

        NumberPicker monthPicker = v.findViewById(R.id.picker_month);
        NumberPicker yearPicker  = v.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(currentYear);

        new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Select Month & Year")
                .setPositiveButton("OK", (d, w) -> {
                    int year  = yearPicker.getValue();
                    int month = monthPicker.getValue();
                    String ym = String.format(Locale.getDefault(), "%04d-%02d", year, month);

                    List<Income> incomes = dbhelper.getIncomesByMonth(ym);
                    List<Expense> expenses = dbhelper.getExpensesByMonth(ym);
                    generatePdfReport(incomes, expenses, "Monthly Report - " + ym);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showYearPickerDialog() {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.dialog_year_picker, null);

        NumberPicker yearPicker = v.findViewById(R.id.picker_year);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(currentYear);

        new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Select Year")
                .setPositiveButton("OK", (d, w) -> {
                    String year = String.valueOf(yearPicker.getValue());

                    List<Income> incomes = dbhelper.getIncomesByYear(year);
                    List<Expense> expenses = dbhelper.getExpensesByYear(year);
                    generatePdfReport(incomes, expenses, "Yearly Report - " + year);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ------------------------------------------------------------------ PDF helpers
    private void generateDailyPdf(String date) {
        List<Income> incomes = dbhelper.getIncomesByDate(date);
        List<Expense> expenses = dbhelper.getExpensesByDate(date);
        PdfUtils.generatePdf(this, incomes, expenses, "Daily Report - " + date);
    }

    private void generatePdfReport(List<Income> incomes, List<Expense> expenses, String title) {
        PdfUtils.generatePdf(this, incomes, expenses, title);
    }
}
