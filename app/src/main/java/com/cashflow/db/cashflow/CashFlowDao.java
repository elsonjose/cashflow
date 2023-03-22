package com.cashflow.db.cashflow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cashflow.db.cashflow.models.WeeklyCashItem;
import com.cashflow.helper.Constants;

import java.util.List;

@Dao
public interface CashFlowDao {

    @Insert
    long addItem(CashItem cashItem);

    @Update
    void updateItem(CashItem item);

    @Delete
    void deleteItem(CashItem cashItem);

    @Query("SELECT * FROM CASHFLOW WHERE id==:id")
    CashItem getItemById(long id);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode,0 AS amount, count(*) AS count, t1.week_key,  (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.week_key = t2.week_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.week_key = t3.week_key AND type='debit') AS debit FROM CASHFLOW t1  GROUP BY week_key ORDER BY time;")
    List<WeeklyCashItem> getWeeklyItems();

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode,0 AS amount, count(*) AS count, t1.week_key,  (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.week_key = t2.week_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.week_key = t3.week_key AND type='debit') AS debit FROM CASHFLOW t1 WHERE time between :start AND :end GROUP BY week_key ORDER BY time")
    List<WeeklyCashItem> getWeeklyItemsForDateRange(long start, long end);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count,  SUM(amount) as amount, month_key FROM CASHFLOW GROUP BY month_key ORDER BY time")
    List<CashItem> getMonthlyItems();

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count,  SUM(amount) as amount, month_key FROM CASHFLOW  WHERE time between :start AND :end GROUP BY month_key ORDER BY time")
    List<CashItem> getMonthlyItemsForDateRange(long start, long end);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count,  SUM(amount) as amount, year_key FROM CASHFLOW GROUP BY year_key ORDER BY time")
    List<CashItem> getYearlyItems();

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count,  SUM(amount) as amount, year_key FROM CASHFLOW  WHERE time between :start AND :end GROUP BY year_key ORDER BY time")
    List<CashItem> getYearlyItemsForDateRange(long start, long end);

    @Query("SELECT * FROM CASHFLOW")
    List<CashItem> getAllItems();

    @Query("SELECT * FROM CASHFLOW WHERE time between :start AND :end")
    List<CashItem> getAllItemsForDateRange(long start, long end);

    @Query("SELECT SUM(ABS(AMOUNT)) FROM CASHFLOW WHERE TYPE==:t")
    double getAmountSum(String t);

    @Query("SELECT SUM(ABS(AMOUNT)) FROM CASHFLOW WHERE time BETWEEN :start AND :end AND TYPE==:t")
    double getAmountSumForDateRangeByType(String t, long start, long end);

    @Query("DELETE FROM CASHFLOW")
    void deleteAll();
}
