package com.example.savemoney;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ShowDataList extends AppCompatActivity {

    EditText edamount, edreason;
    Button button;
    ListView listview;
    public static boolean SAVEMONEY = true;

    DataBaseHelper dbhelper;
    ArrayList<HashMap<String, String>> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        setContentView(R.layout.activity_show_data_list);

        edamount = findViewById(R.id.edamount);
        edreason = findViewById(R.id.edreason);
        button = findViewById(R.id.button);
        listview = findViewById(R.id.listview);
        dbhelper = new DataBaseHelper(this);

        loadData();

        edamount.setHint("0");
        edreason.setHint("note");

        button.setOnClickListener(v -> {
            if (edamount.length() > 0 && edreason.length() > 0) {
                double amount = Double.parseDouble(edamount.getText().toString());
                String reason = edreason.getText().toString();

                if (SAVEMONEY) {
                    dbhelper.addIncome(amount, reason);
                    Toast.makeText(this, "Income Added", Toast.LENGTH_SHORT).show();
                } else {
                    dbhelper.addExpense(amount, reason);
                    Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show();
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("data_added", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== Adapter ==================
    public class myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myview = layoutInflater.inflate(R.layout.item, parent, false);

            TextView tvamount = myview.findViewById(R.id.tvamount);
            TextView tvreason = myview.findViewById(R.id.tvreason);
            ImageView delete = myview.findViewById(R.id.delete);
            TextView title = myview.findViewById(R.id.title);
            TextView tvtime = myview.findViewById(R.id.tvtime);

            HashMap<String, String> hashMap = arrayList.get(position);
            String id = hashMap.get("id");
            String amount = hashMap.get("amount");
            String reason = hashMap.get("reason");
            String time = hashMap.get("time");

            title.setText(SAVEMONEY ? "Income" : "Expense");
            if (!SAVEMONEY) {
                tvamount.setTextColor(Color.parseColor("#16CC1D"));
            }

            tvamount.setText("BDT: " + amount);
            tvreason.setText(reason);

            // Date formatting
            SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outFmt = new SimpleDateFormat("yyyy MMM dd hh:mm a", Locale.getDefault());
            try {
                Date dt = inFmt.parse(time);
                String show = outFmt.format(dt);
                tvtime.setText(show);
            } catch (Exception e) {
                tvtime.setText("Invalid date");
            }

            // Delete item
            delete.setOnClickListener(v -> {
                int dataId = Integer.parseInt(id);
                String title1 = SAVEMONEY ? "Delete Income Data" : "Delete Expense Data";
                String message = "Are you sure?";
                String toastMsg = SAVEMONEY ? "Income Deleted" : "Expense Deleted";

                new AlertDialog.Builder(ShowDataList.this)
                        .setTitle(title1)
                        .setMessage(message)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (SAVEMONEY) {
                                dbhelper.deleteIncomeById(dataId);
                            } else {
                                dbhelper.deleteExpenseById(dataId);
                            }
                            loadData();
                            Toast.makeText(ShowDataList.this, toastMsg, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            return myview;
        }
    }

    // ================== Load Data ==================
    public void loadData() {
        Cursor cursor = SAVEMONEY ? dbhelper.getAllIncome() : dbhelper.getAllExpense();

        if (cursor != null && cursor.getCount() > 0) {
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String reason = cursor.getString(2);
                String time = cursor.getString(3);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", String.valueOf(id));
                hashMap.put("amount", String.valueOf(amount));
                hashMap.put("reason", reason);
                hashMap.put("time", time);

                arrayList.add(hashMap);
            }
            cursor.close();
            listview.setAdapter(new myadapter());
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
}
