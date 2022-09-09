package com.cashflow.helper;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_EXPENSE;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_INCOME;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_UNKNOWN;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_DEFAULT;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_INDIVIDUAL;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_MONTHLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_WEEKLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_YEARLY;

import android.util.Log;

import com.cashflow.db.cashflow.CashItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashFlowViewTypeHelper {

    private static final String TAG = "CashFlowViewTypeHelper";
    int viewType;

    public CashFlowViewTypeHelper(int viewType) {
        this.viewType = viewType;
    }

    public List<CashItem> GetCashFlowList(List<CashItem> cashItemList, long startDate, long endDate) {
        switch (this.viewType) {
            case STATEMENT_VIEW_MODE_DEFAULT: {
                return getDefaultCashFlow(cashItemList);
            }
            case STATEMENT_VIEW_MODE_WEEKLY: {
                return getWeeklyCashFlow(cashItemList, startDate, endDate);
            }
            case STATEMENT_VIEW_MODE_MONTHLY: {
                return getMonthlyCashFlow(cashItemList, startDate, endDate);
            }
            case STATEMENT_VIEW_MODE_YEARLY: {
                return getYearlyCashFlow(cashItemList, startDate, endDate);
            }
            default: {
                return cashItemList;
            }
        }
    }

    private int getSpanType(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month+=1;
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        Date date = new Date(timeStamp);
        SimpleDateFormat yearlyFormat = new SimpleDateFormat("yyyy");
        String yearNumberKey = yearlyFormat.format(date);

        SimpleDateFormat yearMonthlyFormat = new SimpleDateFormat("yyyy-MM");
        String yearMonthlyNumberKey = yearMonthlyFormat.format(date);

        SimpleDateFormat yearWeeklyFormat = new SimpleDateFormat("yyyy-ww");
        String yearWeeklyNumberKey = yearWeeklyFormat.format(date);

        if (yearWeeklyNumberKey.equals(year + "-" + week)) {
            return STATEMENT_VIEW_MODE_INDIVIDUAL;
        } else if (yearMonthlyNumberKey.equals(year + "-" + String.format("%02d", month))) {
            return STATEMENT_VIEW_MODE_WEEKLY;
        } else if (yearNumberKey.equals(String.valueOf(year))) {
            return STATEMENT_VIEW_MODE_MONTHLY;
        }
        return STATEMENT_VIEW_MODE_YEARLY;
    }

    private List<CashItem> getDefaultCashFlow(List<CashItem> cashItemList) {

        List<CashItem> defaultCashFlowList = new ArrayList<>();

        List<CashItem> currentWeeklyItems = new ArrayList<>();
        List<CashItem> currentMonthlyItems = new ArrayList<>();
        List<CashItem> currentYearlyItems = new ArrayList<>();
        List<CashItem> yearlyItems = new ArrayList<>();

        for (CashItem item : cashItemList) {
            int span = getSpanType(item.getTime());
            Log.i(TAG, "getDefaultCashFlow: span "+span);
            Date date = new Date(item.getTime());
            SimpleDateFormat yearlyFormat = new SimpleDateFormat("dd-MM-yyyy");
            String yearNumberKey = yearlyFormat.format(date);
            Log.i(TAG, "getDefaultCashFlow: time "+yearNumberKey);
            if (span == STATEMENT_VIEW_MODE_INDIVIDUAL) {
                currentWeeklyItems.add(item);
            } else if (span == STATEMENT_VIEW_MODE_WEEKLY) {
                currentMonthlyItems.add(item);
                Collections.sort(currentWeeklyItems);
            } else if (span == STATEMENT_VIEW_MODE_MONTHLY) {
                currentYearlyItems.add(item);
                Collections.sort(currentYearlyItems);
            } else if (span == STATEMENT_VIEW_MODE_YEARLY) {
                yearlyItems.add(item);
                Collections.sort(yearlyItems);
            }
        }

        defaultCashFlowList.addAll(currentWeeklyItems);
        if(currentMonthlyItems.size() > 0)
        {
            long weeklyEndDate = currentMonthlyItems.get(currentMonthlyItems.size()-1).getTime();
            long weeklyStartDate = currentMonthlyItems.get(0).getTime();
            defaultCashFlowList.addAll(getWeeklyCashFlow(currentMonthlyItems,weeklyStartDate,weeklyEndDate));
        }
        if(currentYearlyItems.size() > 0)
        {
            long currentYearEndDate = currentYearlyItems.get(currentYearlyItems.size()-1).getTime();
            long currentYearStartDate = currentYearlyItems.get(0).getTime();
            defaultCashFlowList.addAll(getMonthlyCashFlow(currentYearlyItems,currentYearStartDate,currentYearEndDate));
        }
        if(yearlyItems.size() > 0)
        {
            long yearEndDate = yearlyItems.get(yearlyItems.size()-1).getTime();
            long yearStartDate = yearlyItems.get(0).getTime();
            defaultCashFlowList.addAll(getYearlyCashFlow(yearlyItems,yearStartDate,yearEndDate));
        }
        return defaultCashFlowList;
    }

    private List<CashItem> getYearlyCashFlow(List<CashItem> cashItemList, long startDate, long endDate) {
        List<CashItem> yearlyData = new ArrayList<>();
        Map<String, CashItem> allYearlyData = new HashMap<>();
        Map<String, Integer> transactionCounter = new HashMap<>();

        long currentMonthDate = startDate;
        while (currentMonthDate <= endDate) {
            CashItem yearData = new CashItem();

            Date date = new Date(currentMonthDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String yearNumberKey = sdf.format(date);

            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, Integer.parseInt(yearNumberKey));
            Date yearStartDate = calendar.getTime();

            int year = Integer.parseInt(yearNumberKey);

            calendar.set(Calendar.YEAR, year + 1);
            Date yearEndDate = calendar.getTime();

            yearData.setStartDate(yearStartDate.getTime());
            yearData.setEndDate(yearEndDate.getTime() - 1000);
            yearData.setViewMode(STATEMENT_VIEW_MODE_YEARLY);
            yearData.setAmount(0);
            yearData.setTime(currentMonthDate);
            yearData.setType(STATEMENT_TYPE_UNKNOWN);

            allYearlyData.put(yearNumberKey, yearData);
            transactionCounter.put(yearNumberKey, 0);
            currentMonthDate = yearEndDate.getTime();
        }

        for (CashItem item : cashItemList) {
            Date date = new Date(item.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String yearNumberKey = sdf.format(date);

            transactionCounter.put(yearNumberKey, transactionCounter.get(yearNumberKey) + 1);
            CashItem yearlyItem = allYearlyData.get(yearNumberKey);
            if (item.getType().equals(STATEMENT_TYPE_INCOME)) {
                yearlyItem.setAmount(yearlyItem.getAmount() + item.getAmount());
            } else if (item.getType().equals(STATEMENT_TYPE_EXPENSE)) {
                yearlyItem.setAmount(yearlyItem.getAmount() - item.getAmount());
            }

            if (yearlyItem.getAmount() > -1) {
                yearlyItem.setType(STATEMENT_TYPE_INCOME);
            } else {
                yearlyItem.setType(STATEMENT_TYPE_EXPENSE);
            }

            yearlyItem.setAmount(Math.abs(yearlyItem.getAmount()));

            if (transactionCounter.get(yearNumberKey) != 1) {
                yearlyItem.setDesc(transactionCounter.get(yearNumberKey) + " transactions");

            } else {
                yearlyItem.setDesc(transactionCounter.get(yearNumberKey) + " transaction");
            }
        }
        yearlyData.addAll(allYearlyData.values());
        return yearlyData;

    }

    private List<CashItem> getMonthlyCashFlow(List<CashItem> cashItemList, long startDate, long endDate) {


        List<CashItem> monthlyData = new ArrayList<>();
        Map<String, CashItem> allMonthlyData = new HashMap<>();
        Map<String, Integer> transactionCounter = new HashMap<>();

        long currentMonthDate = startDate;
        while (currentMonthDate <= endDate) {
            CashItem weekData = new CashItem();

            Date date = new Date(currentMonthDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String[] yearMonthNumber = sdf.format(date).split("-");
            String yearMonthNumberKey = sdf.format(date);

            int processingMonth = Integer.parseInt(yearMonthNumber[1]) - 1;

            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, Integer.parseInt(yearMonthNumber[0]));
            calendar.set(Calendar.MONTH, processingMonth);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date monthStartDate = calendar.getTime();

            int year = Integer.parseInt(yearMonthNumber[0]);
            int lastDay = 30;
            if (processingMonth == 1) {
                if ((year % 100 == 0 && year % 400 == 0) || (year % 100 != 0 && year % 4 == 0)) {
                    lastDay = 29;
                } else {
                    lastDay = 28;
                }
            } else if ((processingMonth == 0) || (processingMonth == 2 || (processingMonth == 4) || (processingMonth == 6) || (processingMonth == 7) || (processingMonth == 9) || (processingMonth == 11))) {
                lastDay = 31;
            }

            calendar.set(Calendar.DAY_OF_MONTH, lastDay);
            Date monthEndDate = calendar.getTime();

            weekData.setStartDate(monthStartDate.getTime());
            weekData.setEndDate(monthEndDate.getTime() + (24 * 60 * 60 * 1000) - 1000);
            weekData.setViewMode(STATEMENT_VIEW_MODE_MONTHLY);
            weekData.setAmount(0);
            weekData.setType(STATEMENT_TYPE_UNKNOWN);
            weekData.setTime(currentMonthDate);
            allMonthlyData.put(yearMonthNumberKey, weekData);
            transactionCounter.put(yearMonthNumberKey, 0);
            currentMonthDate = monthEndDate.getTime() + (24 * 60 * 60 * 1000);

        }

        for (CashItem item : cashItemList) {
            Date date = new Date(item.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String yearMonthNumberKey = sdf.format(date);

            transactionCounter.put(yearMonthNumberKey, transactionCounter.get(yearMonthNumberKey) + 1);
            CashItem monthlyItem = allMonthlyData.get(yearMonthNumberKey);
            if (item.getType().equals(STATEMENT_TYPE_INCOME)) {
                monthlyItem.setAmount(monthlyItem.getAmount() + item.getAmount());
            } else if (item.getType().equals(STATEMENT_TYPE_EXPENSE)) {
                monthlyItem.setAmount(monthlyItem.getAmount() - item.getAmount());
            }

            if (monthlyItem.getAmount() > -1) {
                monthlyItem.setType(STATEMENT_TYPE_INCOME);
            } else {
                monthlyItem.setType(STATEMENT_TYPE_EXPENSE);
            }
            monthlyItem.setAmount(Math.abs(monthlyItem.getAmount()));

            if (transactionCounter.get(yearMonthNumberKey) != 1) {
                monthlyItem.setDesc(transactionCounter.get(yearMonthNumberKey) + " transactions");

            } else {
                monthlyItem.setDesc(transactionCounter.get(yearMonthNumberKey) + " transaction");
            }
        }
        monthlyData.addAll(allMonthlyData.values());
        return monthlyData;

    }

    private List<CashItem> getWeeklyCashFlow(List<CashItem> cashItemList, long startDate, long endDate) {
        List<CashItem> weeklyData = new ArrayList<>();
        Map<String, CashItem> allWeeklyData = new HashMap<>();
        Map<String, Integer> transactionCounter = new HashMap<>();

        long currentWeekDate = startDate;
        while (currentWeekDate <= endDate) {
            CashItem weekData = new CashItem();

            Date date = new Date(currentWeekDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-ww");
            String[] yearWeekNumber = sdf.format(date).split("-");
            String yearWeekNumberKey = sdf.format(date);


            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, Integer.parseInt(yearWeekNumber[0]));
            calendar.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(yearWeekNumber[1]));

            calendar.set(Calendar.DAY_OF_WEEK, 1);
            Date weekStartDate = calendar.getTime();

            long weekEndDate = weekStartDate.getTime() + 6 * (24 * 60 * 60 * 1000);

            weekData.setStartDate(weekStartDate.getTime());
            weekData.setEndDate(weekEndDate + (24 * 60 * 60 * 1000) - 1000);
            weekData.setViewMode(STATEMENT_VIEW_MODE_WEEKLY);
            weekData.setAmount(0);
            weekData.setType(STATEMENT_TYPE_UNKNOWN);
            weekData.setTime(currentWeekDate);
            allWeeklyData.put(yearWeekNumberKey, weekData);
            transactionCounter.put(yearWeekNumberKey, 0);
            currentWeekDate = weekEndDate + (24 * 60 * 60 * 1000);
            if (Integer.parseInt(yearWeekNumber[1]) == 52) {
                calendar.setTime(new Date(currentWeekDate));
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int remainingDays = 31 - currentDay + 1;
                currentWeekDate += remainingDays * (24 * 60 * 60 * 1000);

            }
        }

        for (CashItem item : cashItemList) {
            Date date = new Date(item.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-ww");
            String yearWeekNumberKey = sdf.format(date);

            transactionCounter.put(yearWeekNumberKey, transactionCounter.get(yearWeekNumberKey) + 1);
            CashItem weeklyItem = allWeeklyData.get(yearWeekNumberKey);
            if (item.getType().equals(STATEMENT_TYPE_INCOME)) {
                weeklyItem.setType(STATEMENT_TYPE_INCOME);
                weeklyItem.setAmount(weeklyItem.getAmount() + item.getAmount());
            } else if (item.getType().equals(STATEMENT_TYPE_EXPENSE)) {
                weeklyItem.setType(STATEMENT_TYPE_EXPENSE);
                weeklyItem.setAmount(weeklyItem.getAmount() - item.getAmount());
            }

            if (weeklyItem.getAmount() > -1) {
                weeklyItem.setType(STATEMENT_TYPE_INCOME);
            } else {
                weeklyItem.setType(STATEMENT_TYPE_EXPENSE);
            }
            weeklyItem.setAmount(Math.abs(weeklyItem.getAmount()));

            if (transactionCounter.get(yearWeekNumberKey) != 1) {
                weeklyItem.setDesc(transactionCounter.get(yearWeekNumberKey) + " transactions");

            } else {
                weeklyItem.setDesc(transactionCounter.get(yearWeekNumberKey) + " transaction");
            }
        }
        weeklyData.addAll(allWeeklyData.values());
        return weeklyData;

    }
}
