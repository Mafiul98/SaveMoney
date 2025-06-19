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

    private static final String DB_NAME = "my_database";
    private static final int DB_VERSION = 4; // Updated version
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS income_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount REAL, reason TEXT, date TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS expense_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount REAL, reason TEXT, date TEXT)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_income_date ON income_table(date)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_expense_date ON expense_table(date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_income_date ON income_table(date)");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_expense_date ON expense_table(date)");
        }
    }

    // Insert income
    public void addIncome(double amount, String reason) {
        insert("income_table", amount, reason);
    }

    // Insert expense
    public void addExpense(double amount, String reason) {
        insert("expense_table", amount, reason);
    }

    private void insert(String table, double amount, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("reason", reason);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        values.put("date", currentDate);
        db.insert(table, null, values);
    }

    // Get all income
    public Cursor getAllIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM income_table ORDER BY id DESC", null);
    }

    // Get all expense
    public Cursor getAllExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM expense_table ORDER BY id DESC", null);
    }

    // Delete income by ID
    public void deleteIncomeById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("income_table", "id = ?", new String[]{String.valueOf(id)});
    }

    // Delete expense by ID
    public void deleteExpenseById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("expense_table", "id = ?", new String[]{String.valueOf(id)});
    }

    // ------------------------- Report Queries ----------------------------

    // Daily (Exact date)
    public List<Income> getIncomesByDate(String date) {
        return getIncomeList("SELECT * FROM income_table WHERE date = ?", date);
    }

    public List<Expense> getExpensesByDate(String date) {
        return getExpenseList("SELECT * FROM expense_table WHERE date = ?", date);
    }

    // Monthly (yyyy-MM)
    public List<Income> getIncomesByMonth(String yearMonth) {
        return getIncomeList("SELECT * FROM income_table WHERE date LIKE ?", yearMonth + "%");
    }

    public List<Expense> getExpensesByMonth(String yearMonth) {
        return getExpenseList("SELECT * FROM expense_table WHERE date LIKE ?", yearMonth + "%");
    }

    // Yearly (yyyy)
    public List<Income> getIncomesByYear(String year) {
        return getIncomeList("SELECT * FROM income_table WHERE date LIKE ?", year + "%");
    }

    public List<Expense> getExpensesByYear(String year) {
        return getExpenseList("SELECT * FROM expense_table WHERE date LIKE ?", year + "%");
    }
/*
    // Between two dates
    public List<Income> getIncomesBetween(String from, String to) {
        return getIncomeList("SELECT * FROM income_table WHERE date BETWEEN ? AND ?", from, to);
    }

    public List<Expense> getExpensesBetween(String from, String to) {
        return getExpenseList("SELECT * FROM expense_table WHERE date BETWEEN ? AND ?", from, to);
    }

 */

    // ---------------------- Generic List Builders ------------------------

    private List<Income> getIncomeList(String query, String... args) {
        List<Income> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            do {
                Income income = new Income();
                income.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                income.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                income.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                income.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                list.add(income);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private List<Expense> getExpenseList(String query, String... args) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                list.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
