package com.cashflow.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CashFlow")
public class CashItem {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "desc")
    private String desc;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "time")
    private String time;

    public CashItem(long id, String desc, double amount, String type, String time) {
        this.id = id;
        this.desc = desc;
        this.amount = amount;
        this.type = type;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
