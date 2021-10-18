package com.cashflow.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cashflow.R;
import com.cashflow.activity.CashFlowActivity;
import com.cashflow.adapter.CashFlowAdapter;
import com.cashflow.db.cashflow.CashFlowDatabase;
import com.cashflow.db.cashflow.CashItem;
import com.cashflow.interfaces.onChanged;
import com.cashflow.interfaces.onDeleted;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class IncomeFragment extends Fragment implements onDeleted {


    TextView header;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    CashFlowAdapter adapter;

    public void onCreateCashFlow()
    {
        startActivity(new Intent(getContext(), CashFlowActivity.class).putExtra("type","income"));
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getActivity().finish();
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=  inflater.inflate(R.layout.fragment_income, container, false);
        header = view.findViewById(R.id.title_textView);
        header.setTextColor(Color.parseColor("#3fb950"));
        fab = view.findViewById(R.id.fab);
        fab.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        CashFlowDatabase database = Room.databaseBuilder(getContext(),CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
        List<CashItem> incomeList = database.getCashFlowDao().getAllItems("income");
        header.setText("Total: ₹ "+database.getCashFlowDao().getAmountSum("income"));
        adapter = new CashFlowAdapter("income",incomeList,getContext(),this);
        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCreateCashFlow();

            }
        });
        return view;
    }
    @Override
    public void onDeleted() {
        CashFlowDatabase database = Room.databaseBuilder(getContext(),CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
        header.setText("Total: ₹ "+database.getCashFlowDao().getAmountSum("income"));
        onChanged changed = (onChanged) getActivity();
        changed.onChanged();
    }
}