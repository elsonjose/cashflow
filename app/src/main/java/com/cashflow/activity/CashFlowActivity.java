package com.cashflow.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
        }
        else if(desc.getText().toString().length()==0)
        {
            desc.setError("Cannot be empty");
        }
        else
        {
            CashFlowDatabase database = Room.databaseBuilder(getApplicationContext(),CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
            double amountVal = Double.parseDouble(amount.getText().toString());
            String descVal = desc.getText().toString();

            if(isEdit)
            {
                database.getCashFlowDao().UpdateItem(new CashItem(cashFlowItem.getId(),descVal,amountVal,cashFlowItem.getType(),cashFlowItem.getTime()));

            }
            else
            {
                database.getCashFlowDao().addItem(new CashItem(0,descVal,amountVal,type,System.currentTimeMillis()));

            }

            onBackPressed();
        }


    }

    String type="";
    TextInputEditText amount,desc;
    TextView typeTextView;
    CashItem cashFlowItem;
    boolean isEdit=false;
    Button createOrUpdateBtn,deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_flow);

        createOrUpdateBtn = findViewById(R.id.create_or_update_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        typeTextView = findViewById(R.id.actionbar_textView);
        type = getIntent().getExtras().getString("type");
        long cashFlowItemId = getIntent().getExtras().getLong("id");
        amount = findViewById(R.id.amount);
        desc = findViewById(R.id.desc);

        if(!TextUtils.isEmpty(type))
        {
            isEdit = false;
            typeTextView.setText("Add new "+type);
            createOrUpdateBtn.setText("Create");
            deleteBtn.setVisibility(View.GONE);
        }
        else
        {
            isEdit = true;
            deleteBtn.setVisibility(View.VISIBLE);
            createOrUpdateBtn.setText("Update");
            CashFlowDatabase database = Room.databaseBuilder(CashFlowActivity.this, CashFlowDatabase.class, "CashFlow").fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
            cashFlowItem = database.getCashFlowDao().getItem(cashFlowItemId);
            amount.setText(""+cashFlowItem.getAmount());
            desc.setText(cashFlowItem.getDesc());
            typeTextView.setText("Edit "+cashFlowItem.getType());
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(CashFlowActivity.this)
                        .setTitle("Deleting "+cashFlowItem.getType())
                        .setMessage("Are you sure you want to delete ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                CashFlowDatabase database = Room.databaseBuilder(CashFlowActivity.this,CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
                                database.getCashFlowDao().deleteItem(cashFlowItem);
                                onBackPressed();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CashFlowActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}