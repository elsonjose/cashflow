package com.cashflow.db.cashflow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cashflow.db.cashflow.models.RangeCashItem;

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

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, 0 AS amount, count(*) AS count, t1.week_key,  (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.week_key = t2.week_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.week_key = t3.week_key AND type='debit') AS debit FROM CASHFLOW t1  GROUP BY t1.week_key LIMIT :take OFFSET :skip;")
    List<RangeCashItem> getWeeklyItems(int take, int skip);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, 0 AS amount, count(*) AS count, t1.week_key,  (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.week_key = t2.week_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.week_key = t3.week_key AND type='debit') AS debit FROM CASHFLOW t1 WHERE t1.time between :start AND :end GROUP BY t1.week_key LIMIT :take OFFSET :skip;")
    List<RangeCashItem> getWeeklyItemsForDateRange(long start, long end, int take, int skip);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count, 0 as amount, t1.month_key, (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.month_key = t2.month_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.month_key = t3.month_key AND type='debit') AS debit FROM CASHFLOW t1 GROUP BY t1.month_key LIMIT :take OFFSET :skip;")
    List<RangeCashItem> getMonthlyItems(int take, int skip);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count,  0 as amount, t1.month_key, (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.month_key = t2.month_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.month_key = t3.month_key AND type='debit') AS debit FROM CASHFLOW t1  WHERE t1.time between :start AND :end GROUP BY t1.month_key LIMIT :take OFFSET :skip;")
    List<RangeCashItem> getMonthlyItemsForDateRange(long start, long end, int take, int skip);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count, 0 as amount, t1.year_key, (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.year_key = t2.year_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.year_key = t3.year_key AND type='debit') AS debit FROM CASHFLOW t1 GROUP BY t1.year_key LIMIT :take OFFSET :skip;")
    List<RangeCashItem> getYearlyItems(int take, int skip);

    @Query("SELECT 0 AS id,0 AS time,0 AS startDate, 0 AS endDate, 0 AS viewMode, count(*) AS count, 0 as amount, t1.year_key, (SELECT SUM(amount) FROM CASHFLOW t2 WHERE t1.year_key = t2.year_key AND type='credit') AS credit, (SELECT SUM(ABS(amount)) FROM CASHFLOW t3 WHERE t1.year_key = t3.year_key AND type='debit') AS debit FROM CASHFLOW t1 WHERE time between :start AND :end GROUP BY t1.year_key LIMIT :take OFFSET :skip;")
    List<RangeCashItem> getYearlyItemsForDateRange(long start, long end, int take, int skip);

    @Query("SELECT * FROM CASHFLOW LIMIT :take OFFSET :skip;")
    List<CashItem> getAllItems(int take, int skip);

    @Query("SELECT * FROM CASHFLOW WHERE time between :start AND :end LIMIT :take OFFSET :skip;")
    List<CashItem> getAllItemsForDateRange(long start, long end, int take, int skip);

    @Query("SELECT SUM(ABS(AMOUNT)) FROM CASHFLOW WHERE TYPE==:t")
    double getAmountSum(String t);

    @Query("SELECT SUM(ABS(AMOUNT)) FROM CASHFLOW WHERE time BETWEEN :start AND :end AND TYPE==:t")
    double getAmountSumForDateRangeByType(String t, long start, long end);

    @Query("DELETE FROM CASHFLOW")
    void deleteAll();
}
