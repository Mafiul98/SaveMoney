package com.example.savemoney;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShowDataList extends AppCompatActivity {

    EditText edamount,edreason;
    Button button;
    ListView listview;
    public static boolean SAVEMONEY = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));}
        WindowCompat.getInsetsController(getWindow(),getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        setContentView(R.layout.activity_show_data_list);
        edamount=findViewById(R.id.edamount);
        edreason=findViewById(R.id.edreason);
        button=findViewById(R.id.button);
        listview=findViewById(R.id.listview);

        if (SAVEMONEY==true){
            edamount.setHint("0");
            edreason.setHint("reason");
        }
        else {
            edamount.setHint("00");
            edreason.setHint("Note");
        }

    }
}