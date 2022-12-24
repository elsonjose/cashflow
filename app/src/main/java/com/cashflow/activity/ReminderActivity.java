package com.cashflow.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.cashflow.R;
import com.cashflow.helper.DateTimeHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    TextInputEditText dateLayout;

    public void openDatePicker(View v) {

        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Calendar c1 = Calendar.getInstance();
                c1.set(Calendar.YEAR,year);
                c1.set(Calendar.MONTH,month);
                c1.set(Calendar.DAY_OF_MONTH,day);
                long timeStamp = c1.getTimeInMillis();
                dateLayout.setText(DateTimeHelper.getDateWithDay(timeStamp));

            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        dateLayout = findViewById(R.id.reminder_date);
    }
}