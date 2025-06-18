package com.example.savemoney;

public class Expense {
    private int id;
    private String date;
    private String reason;
    private double amount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
