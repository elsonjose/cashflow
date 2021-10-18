package com.cashflow.db.sheet;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cashflow.db.cashflow.CashItem;

import java.util.List;

@Dao
public interface SheetDao {

    @Insert
    public long addItem(SheetItem sheetItem);

    @Delete
    public void deleteItem(SheetItem sheetItem);

    @Query("SELECT * FROM BALANCESHEET WHERE TYPE==:t")
    public List<SheetItem> getAllItemsOfType(String t);

    @Query("SELECT * FROM BALANCESHEET")
    public List<SheetItem> getAllItems();

    @Query("SELECT SUM(WORTH) FROM BALANCESHEET WHERE TYPE==:t")
    public double getWorthSum(String t);
}
