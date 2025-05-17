package com.example.savemoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context) {
        super(context, "my_database", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table expense (id INTEGER PRIMARY KEY AUTOINCREMENT,amount DOUBLE,reason TEXT,time DOUBLE)");
        db.execSQL("create table income( id INTEGER PRIMARY KEY AUTOINCREMENT,amount DOUBLE,reason TEXT,time DOUBLE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expense");
        db.execSQL("DROP TABLE IF EXISTS income");

    }

    public void addExpense (double amount,String reason){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conval = new ContentValues();
        conval.put("amount",amount);
        conval.put("reason",reason);
        conval.put("time",System.currentTimeMillis());
        db.insert("expense",null,conval);
    }

    public void addIncome (double amount,String reason){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conval = new ContentValues();
        conval.put("amount",amount);
        conval.put("reason",reason);
        conval.put("time",System.currentTimeMillis());
        db.insert("income",null,conval);
    }


    public Cursor getAllExpense(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from expense order by id desc",null);
        return cursor;
    }

    public Cursor getAllIncome(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from income order by id desc",null);
        return cursor;
    }

    public double getTotalExpense(){
        double totalexpense = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from expense",null);
        if (cursor!=null & cursor.getCount()>0){
            while (cursor.moveToNext()){
                double amount = cursor.getDouble(1);
                totalexpense = totalexpense+amount;
            }
        }
        return totalexpense;
    }


    public double getTotalIncome() {
        double totalincome = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from income", null);
        if (cursor != null & cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                double amount = cursor.getDouble(1);
                totalincome = totalincome + amount;
            }
        }

        return totalincome;
    }

    public void deleteExpense(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from expense where id like "+id);
    }
    public void deleteIncome(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from income where id like "+id);
    }



}


