package com.cashflow.db.reminder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "Reminders")

public class ReminderItem {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "remind_at")
    private long remindAt;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "count")
    private int count;
}
