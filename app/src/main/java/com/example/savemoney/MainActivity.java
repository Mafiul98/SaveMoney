package com.example.savemoney;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PieChart chart;
    TextView tvtotalexpense,tvexpense,tvtotalincome,tvincome;
    DataBaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));}
        WindowCompat.getInsetsController(getWindow(),getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        setContentView(R.layout.activity_main);
        chart=findViewById(R.id.chart);
        tvtotalexpense=findViewById(R.id.tvtotalexpense);
        tvexpense=findViewById(R.id.tvexpense);
        tvtotalincome=findViewById(R.id.tvtotalincome);
        tvincome=findViewById(R.id.tvincome);
        dbhelper = new DataBaseHelper(this);



        tvexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDataList.SAVEMONEY = true;
                startActivity(new Intent(MainActivity.this,ShowDataList.class));
            }
        });

        tvincome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDataList.SAVEMONEY = false;
                startActivity(new Intent(MainActivity.this,ShowDataList.class));
            }
        });


        updateUi();


    }

    public void updateUi() {
        double income = dbhelper.getTotalIncome();
        double expense = dbhelper.getTotalExpense();
        double balance = income - expense;

        tvtotalincome.setText("BDT: " + income);
        tvtotalexpense.setText("BDT: " + expense);
        chart.setCenterText("Main Balance\n" + balance + " taka");

        updatePieChart(income, expense); // ✅ সঠিক ডেটা পাঠানো হলো
    }

    private void updatePieChart(double totalIncome, double totalExpense) {
            ArrayList<PieEntry> entries = new ArrayList<>();

            if (totalIncome == 0 && totalExpense == 0) {
                entries.add(new PieEntry(1f, "No Data"));
            } else {
                if (totalIncome > 0) {
                    entries.add(new PieEntry((float) totalIncome, "Income"));
                }
                if (totalExpense > 0) {
                    entries.add(new PieEntry((float) totalExpense, "Expense"));
                }
            }

            PieDataSet pieDataSet = new PieDataSet(entries, "Income vs Expense");

            if (totalIncome == 0 && totalExpense == 0) {
                pieDataSet.setColors(Color.LTGRAY);
                pieDataSet.setValueTextColor(Color.TRANSPARENT);
            } else {
                // আলাদা রঙ দিতে চাইলে নিজেই set করতে পারিস
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.rgb(76, 175, 80)); // Green for income
                colors.add(Color.rgb(244, 67, 54)); // Red for expense
                pieDataSet.setColors(colors);
                pieDataSet.setValueTextColor(Color.WHITE);
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



        //===================================================================


    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateUi();
    }


}