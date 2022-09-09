package com.cashflow.db.reminder;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cashflow.db.cashflow.CashItem;

import java.util.List;

public interface ReminderDao {

    @Insert
    public long addItem(CashItem cashItem);

    @Delete
    public void deleteItem(CashItem cashItem);

    @Query("DELETE FROM CASHFLOW")
    public void deleteAll();

    @Query("SELECT * FROM CASHFLOW WHERE TYPE==:t")
    public List<CashItem> getAllItems(String t);

    @Query("SELECT * FROM CASHFLOW")
    public List<CashItem> getAllItems();

    @Query("SELECT time FROM CASHFLOW ORDER BY time LIMIT 1")
    public long getStartTimestamp();

    @Query("SELECT time FROM CASHFLOW ORDER BY time DESC LIMIT 1")
    public long getEndTimestamp();

    @Query("SELECT count(*) FROM CASHFLOW")
    public long getTotalCount();

    @Query("SELECT SUM(AMOUNT) FROM CASHFLOW WHERE TYPE==:t")
    public double getAmountSum(String t);
}
