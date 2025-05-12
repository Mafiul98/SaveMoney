package com.example.savemoney;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
        tvmainbalance=findViewById(R.id.tvmainbalance);
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
        tvtotalexpense.setText("BDT: "+dbhelper.getTotalExpense() );
        tvtotalincome.setText("BDT: "+dbhelper.getTotalIncome() );
        double Balance = dbhelper.getTotalIncome()-dbhelper.getTotalExpense();
        tvmainbalance.setText("BDT: "+Balance);
    }

    //===================================================================


    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateUi();
    }
}