package com.cashflow.db.cashflow;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CashItem.class}, version = 3)
public abstract class CashFlowDatabase extends RoomDatabase {
    public abstract CashFlowDao getCashFlowDao();
}
