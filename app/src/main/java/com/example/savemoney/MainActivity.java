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
    TextView tvmainbalance,tvtotalexpense,tvexpense,tvtotalincome,tvincome;
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

    public void updateUi(){
        double totalIncome = dbhelper.getTotalIncome();
        double totalExpense = dbhelper.getTotalExpense();
        double balance = totalIncome - totalExpense;

        tvtotalincome.setText("BDT: " + totalIncome);
        tvtotalexpense.setText("BDT: " + totalExpense);
        chart.setCenterText("Main Balance\n" + balance + " taka");

        updatePieChart(totalIncome, totalExpense);
    }

    private void updatePieChart(double totalIncome, double totalExpense) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        double balance = totalIncome - totalExpense;

        if (balance > 0) {
            entries.add(new PieEntry((float) balance, "Balance"));
        }
        if (totalExpense > 0) {
            entries.add(new PieEntry((float) totalExpense, "Expense"));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Usage Summary");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);
        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.setDrawEntryLabels(true);
        chart.getDescription().setEnabled(false);
        chart.setCenterText("Balance\n" + (int)(totalIncome - totalExpense));
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