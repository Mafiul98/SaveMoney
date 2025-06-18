package com.example.savemoney;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    PieChart chart;
    TextView tvtotalexpense,tvexpense,tvtotalincome,tvincome;
    TextView txtDate;
    ImageView imageleft,imageright;
    Calendar calendar;

    DataBaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        setContentView(R.layout.activity_main);
        chart = findViewById(R.id.chart);
        tvtotalexpense = findViewById(R.id.tvtotalexpense);
        tvexpense = findViewById(R.id.tvexpense);
        tvtotalincome = findViewById(R.id.tvtotalincome);
        tvincome = findViewById(R.id.tvincome);
        dbhelper = new DataBaseHelper(this);

        txtDate = findViewById(R.id.txtDate);
        imageleft = findViewById(R.id.imageleft);
        imageright = findViewById(R.id.imageright);
        calendar = Calendar.getInstance();



        updateUi();
        updateDateText();

        tvexpense.setOnClickListener(v -> {
            ShowDataList.SAVEMONEY = true;
            startActivity(new Intent(MainActivity.this, ShowDataList.class));
        });

        tvincome.setOnClickListener(v -> {
            ShowDataList.SAVEMONEY = false;
            startActivity(new Intent(MainActivity.this, ShowDataList.class));
        });

        txtDate.setOnClickListener(v -> showDatePicker());

        imageleft.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateDateText();
            updateUi();
            generateMonthlyPdf();
        });

        imageright.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateDateText();
            updateUi();
            generateMonthlyPdf();

        });
    }

    public void updateUi() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.getTime());

        List<Income> incomes = dbhelper.getIncomesByDate(date);
        double income = 0;
        for (Income inc : incomes) {
            income += inc.getAmount();
        }

        List<Expense> expenses = dbhelper.getExpensesByDate(date);
        double expense = 0;
        for (Expense exp : expenses) {
            expense += exp.getAmount();
        }

        double balance = income - expense;

        tvtotalincome.setText("BDT: " + income);
        tvtotalexpense.setText("BDT: " + expense);
        chart.setCenterText("Main Balance\n" + balance + " taka");

        updatePieChart(income, expense);



    }

    private void updatePieChart(double totalIncome, double totalExpense) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (totalIncome == 0 && totalExpense == 0) {
            entries.add(new PieEntry(1f, "No Data"));
        } else {
            float expensePercent = (float) ((totalExpense / totalIncome) * 100);
            float savingsPercent = 100f - expensePercent;

            entries.add(new PieEntry(expensePercent, "Expense"));
            entries.add(new PieEntry(savingsPercent, "Savings"));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Income vs Expense");

        if (totalIncome == 0 && totalExpense == 0) {
            pieDataSet.setColors(Color.LTGRAY);
            pieDataSet.setValueTextColor(Color.TRANSPARENT);
        } else {
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#e67e22"));
            colors.add(Color.parseColor("#1e8449"));
            pieDataSet.setColors(colors);
            pieDataSet.setValueTextColor(Color.WHITE);
            pieDataSet.setValueFormatter(new PercentFormatter(chart));
        }

        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);
        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.setDrawEntryLabels(true);
        chart.getDescription().setEnabled(false);

        double balance = totalIncome - totalExpense;
        chart.setCenterText("Balance\n" + (int) balance + "৳");
        chart.setCenterTextColor(Color.BLACK);
        chart.setCenterTextSize(18f);
        chart.setEntryLabelTextSize(10f);
        chart.setEntryLabelColor(Color.BLACK);
        chart.animateY(1000);
        chart.invalidate();
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("< dd-MM-yyyy >", Locale.getDefault());
        txtDate.setText(sdf.format(calendar.getTime()));
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);
            calendar = selectedDate;
            updateDateText();
            updateUi();
            String selected = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate.getTime());
            generateDailyPdf(selected);
        }, year, month, day);

        dpd.show();
    }

    private void generateDailyPdf(String date) {
        List<Income> incomes = dbhelper.getIncomesByDate(date);
        List<Expense> expenses = dbhelper.getExpensesByDate(date);
        PdfUtils.generatePdf(this, incomes, expenses, "Daily Report - " + date);
    }

    private void generateMonthlyPdf() {
        Calendar start = (Calendar) calendar.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = (Calendar) calendar.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        String from = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(start.getTime());
        String to = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(end.getTime());

        List<Income> incomes = dbhelper.getIncomesBetween(from, to);
        List<Expense> expenses = dbhelper.getExpensesBetween(from, to);

        String monthYear = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime());
        PdfUtils.generatePdf(this, incomes, expenses, "Monthly Report - " + monthYear);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateUi();


    }


}