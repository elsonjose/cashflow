package com.cashflow.db.cashflow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CashFlowDao {

    @Insert
    public long addItem(CashItem cashItem);

    @Delete
    public void deleteItem(CashItem cashItem);

    @Query("SELECT * FROM CASHFLOW WHERE TYPE==:t")
    public List<CashItem> getAllItems(String t);

    @Query("SELECT SUM(AMOUNT) FROM CASHFLOW WHERE TYPE==:t")
    public double getAmountSum(String t);

}
