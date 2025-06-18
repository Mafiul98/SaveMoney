package com.example.savemoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context) {
        super(context, "my_database", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS income_table (id INTEGER PRIMARY KEY AUTOINCREMENT, amount DOUBLE, reason TEXT, date TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS expense_table (id INTEGER PRIMARY KEY AUTOINCREMENT, amount DOUBLE, reason TEXT, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS income_table");
        db.execSQL("DROP TABLE IF EXISTS expense_table");
        onCreate(db);
    }

    public void addIncome(double amount, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("reason", reason);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        values.put("date", currentDate);
        db.insert("income_table", null, values);
    }

    public void addExpense(double amount, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("reason", reason);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        values.put("date", currentDate);
        db.insert("expense_table", null, values);
    }

    public Cursor getAllExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM expense_table ORDER BY id DESC", null);
    }

    public Cursor getAllIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM income_table ORDER BY id DESC", null);
    }

    public void deleteIncomeById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("income_table", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteExpenseById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("expense_table", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }


    public List<Income> getIncomesByDate(String date) {
        List<Income> incomeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM income_table WHERE date = ?", new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                Income income = new Income();
                income.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                income.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                income.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                income.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                incomeList.add(income);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return incomeList;
    }

    public List<Expense> getExpensesByDate(String date) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM expense_table WHERE date = ?", new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenseList;
    }

    public List<Income> getIncomesBetween(String from, String to) {
        List<Income> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM income_table WHERE date BETWEEN ? AND ?", new String[]{from, to});

        if (cursor.moveToFirst()) {
            do {
                Income income = new Income();
                income.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                income.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                income.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                income.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                list.add(income);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Expense> getExpensesBetween(String from, String to) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM expense_table WHERE date BETWEEN ? AND ?", new String[]{from, to});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                list.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}

