package com.example.savemoney;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ShowDataList extends AppCompatActivity {

    EditText edamount,edreason;
    Button button;
    ListView listview;
    public static boolean SAVEMONEY = true;

    DataBaseHelper dbhelper;

    ArrayList<HashMap<String,String>> arrayList;
    HashMap<String,String> hashMap;

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
        dbhelper =new DataBaseHelper(this);

        loadData();

        if (SAVEMONEY==true){
            edamount.setHint("0");
            edreason.setHint("note");
        }
        else {
            edamount.setHint("0");
            edreason.setHint("note");
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edamount.length()>0 && edreason.length()>0){
                    String amount  = edamount.getText().toString();
                    String reason = edreason.getText().toString();
                    double amount1 = Double.parseDouble(amount);

                    if (SAVEMONEY==true){
                        dbhelper.addIncome(amount1,reason);
                        Toast.makeText(ShowDataList.this,"Income Added",Toast.LENGTH_LONG).show();
                    }else {
                        dbhelper.addExpense(amount1,reason);
                        Toast.makeText(ShowDataList.this,"Expense Added",Toast.LENGTH_LONG).show();
                    }
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("data_added", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();

                    loadData();
                    edamount.setText("");
                    edreason.setText("");

                }else {
                    Toast.makeText(ShowDataList.this,"All fields required",Toast.LENGTH_LONG).show();
                }

            }
        });






    }
    //==================start==adapter=======================================
    public class myadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myview  = layoutInflater.inflate(R.layout.item,parent,false);
            TextView tvamount = myview.findViewById(R.id.tvamount);
            TextView tvreason = myview.findViewById(R.id.tvreason);
            ImageView delete = myview.findViewById(R.id.delete);
            TextView title = myview.findViewById(R.id.title);
            TextView tvtime  = myview.findViewById(R.id.tvtime);

            hashMap = arrayList.get(position);
            String id = hashMap.get("id");
            String amount = hashMap.get("amount");
            String reason = hashMap.get("reason");
            String time = hashMap.get("time");


            if (SAVEMONEY==true){
                title.setText("Income");
            }
            else {
                title.setText("Expense");
                tvamount.setTextColor(Color.parseColor("#16CC1D"));
            }

            tvamount.setText("BDT: "+amount);
            tvreason.setText(reason);

            // ===== Date format করা হচ্ছে =====
            HashMap<String, String> hashMap = arrayList.get(position);
            String time = hashMap.get("time");
            SimpleDateFormat inFmt  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outFmt = new SimpleDateFormat("yyyy MMM dd hh:mm a", Locale.getDefault());
            try {
                Date   dt = inFmt.parse(time);
                String show = outFmt.format(dt);
                tvtime.setText(show);
            } catch (Exception e) {
                tvtime.setText("Invalid date");

            }

            // ==================================

            delete.setOnClickListener(v->{
                int dataId = Integer.parseInt(id);

                if (SAVEMONEY==true){
                    new AlertDialog.Builder(ShowDataList.this)
                            .setTitle("Delete Income Data")
                            .setMessage("Are you sure")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbhelper.deleteIncomeById(dataId);
                                    loadData();
                                    Toast.makeText(ShowDataList.this,"Expense Deleted",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                }
                else {
                    new AlertDialog.Builder(ShowDataList.this)
                            .setTitle("Delete Expense Data")
                            .setMessage("Are you sure")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbhelper.deleteExpenseById(dataId);
                                    loadData();
                                    Toast.makeText(ShowDataList.this,"Income Deleted",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                }

            });

            return myview;
        }

    }

    //=================================================================
    public void loadData (){

        Cursor cursor = null;
        if (SAVEMONEY==true) cursor = dbhelper.getAllIncome();
        else cursor = dbhelper.getAllExpense();
        

        if (cursor!=null & cursor.getCount()>0){

            arrayList = new ArrayList<>();

            while (cursor.moveToNext()){
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String reason = cursor.getString(2);
                String time = cursor.getString(3);

                hashMap = new HashMap<>();
                hashMap.put("id",String.valueOf(id));
                hashMap.put("amount",String.valueOf(amount));
                hashMap.put("reason",String.valueOf(reason));
                hashMap.put("time", time);
                arrayList.add(hashMap);
            }

            listview.setAdapter(new myadapter());

        }else {
           Toast.makeText(ShowDataList.this,"No Data",Toast.LENGTH_LONG).show();
        }



    }

}