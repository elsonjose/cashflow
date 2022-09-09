package com.cashflow.db.cashflow;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.cashflow.helper.Constants;

@Entity(tableName = "CashFlow")
public class CashItem implements Comparable<CashItem> {

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
    private long time;

    private boolean isGrouped;

    private long startDate;

    private long endDate;

    private int viewMode;

    public CashItem() {
    }

    public CashItem(long id, String desc, double amount, String type, long time) {
        this.id = id;
        this.desc = desc;
        this.amount = amount;
        this.type = type;
        this.time = time;
    }

    public int getViewMode() {
        return viewMode;
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isGrouped() {
        return isGrouped;
    }

    public void setGrouped(boolean grouped) {
        isGrouped = grouped;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }


    @Override
    public int compareTo(CashItem cashItem) {
        return (this.getTime() - cashItem.getTime()) > 0 ? 1 : -1;
    }
}
