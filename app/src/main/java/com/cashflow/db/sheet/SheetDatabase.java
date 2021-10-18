package com.cashflow.db.sheet;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.cashflow.db.cashflow.CashItem;

@Database(entities = {SheetItem.class},version = 1)
public abstract class SheetDatabase extends RoomDatabase {

    public abstract SheetDao getSheetDao();
}
