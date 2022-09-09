package com.cashflow.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cashflow.MainActivity;
import com.cashflow.R;
import com.cashflow.db.cashflow.CashFlowDatabase;
import com.cashflow.db.cashflow.CashItem;
import com.google.android.material.textfield.TextInputEditText;

public class CashFlowActivity extends AppCompatActivity {

    private static final String TAG = "CashFlowActivity";

    public void createCashItem(View v)
    {
        if(amount.getText().toString().length()==0)
        {
            amount.setError("Cannot be empty");
            return;
        }
        else if(desc.getText().toString().length()==0)
        {
            desc.setError("Cannot be empty");
            return;
        }
        else
        {
            CashFlowDatabase database = Room.databaseBuilder(getApplicationContext(),CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
            double amountVal = Double.parseDouble(amount.getText().toString());
            String descVal = desc.getText().toString();
            database.getCashFlowDao().addItem(new CashItem(0,descVal,amountVal,type,System.currentTimeMillis()));
            onBackPressed();
        }


    }

    String type="";
    TextInputEditText amount,desc;
    TextView typeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_flow);

        typeTextView = findViewById(R.id.actionbar_textView);
        type = getIntent().getExtras().getString("type");
        typeTextView.setText("Add new "+type);
        amount = findViewById(R.id.amount);
        desc = findViewById(R.id.desc);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CashFlowActivity.this, MainActivity.class).putExtra("type","income"));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}