package com.cashflow.fragments;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_EXPENSE;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_INCOME;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_UNKNOWN;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_DEFAULT;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_INDIVIDUAL;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_MONTHLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_WEEKLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_YEARLY;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.cashflow.MainActivity;
import com.cashflow.R;
import com.cashflow.adapter.CashFlowAdapter;
import com.cashflow.db.cashflow.CashFlowDatabase;
import com.cashflow.db.cashflow.CashItem;
import com.cashflow.helper.CashFlowViewTypeHelper;
import com.cashflow.helper.Constants;
import com.cashflow.helper.PrefHelper;
import com.cashflow.interfaces.onChanged;
import com.cashflow.interfaces.onDeleted;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class StatementFragment extends Fragment implements onDeleted {

    private static final String TAG = "StatementFragment";

    TextView incomeTextView, expenseTextView;
    RecyclerView recyclerView;
    CashFlowAdapter adapter;
    List<CashItem> realStatementList = new ArrayList<>();

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statement, container, false);

        expenseTextView = view.findViewById(R.id.title_expense_textView);
        expenseTextView.setTextColor(Color.parseColor("#da3633"));

        incomeTextView = view.findViewById(R.id.title_income_textView);
        incomeTextView.setTextColor(Color.parseColor("#3fb950"));

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CashFlowAdapter(realStatementList, getContext(), this);
        recyclerView.setAdapter(adapter);

        loadStatement();

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                onCreateCashFlow();
//
//            }
//        });

        return view;
    }


    public void loadStatement() {

        realStatementList.clear();

        CashFlowDatabase database = Room.databaseBuilder(getContext(), CashFlowDatabase.class, "CashFlow").fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        List<CashItem> statementList = database.getCashFlowDao().getAllItems();
        Collections.sort(statementList);

        long totalCount = database.getCashFlowDao().getTotalCount();
        if (totalCount > 0) {
            long start = database.getCashFlowDao().getStartTimestamp();
            long end = database.getCashFlowDao().getEndTimestamp();
            int viewMode = new PrefHelper(getContext()).getIntPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_DEFAULT);
            statementList = new CashFlowViewTypeHelper(viewMode).GetCashFlowList(statementList, start, end);
        }

        for (CashItem item : statementList) {
            if (!item.getType().equals(STATEMENT_TYPE_UNKNOWN)) {
                realStatementList.add(item);
            }
        }

        Collections.sort(realStatementList);

        expenseTextView.setText("Debit: -₹ " + database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_EXPENSE));
        incomeTextView.setText("Credit: ₹ " + database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_INCOME));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleted() {
        CashFlowDatabase database = Room.databaseBuilder(getContext(), CashFlowDatabase.class, "CashFlow").allowMainThreadQueries().build();
        expenseTextView.setText("Total: -₹ " + database.getCashFlowDao().getAmountSum("expense"));
        incomeTextView.setText("Total: ₹ " + database.getCashFlowDao().getAmountSum("income"));
        onChanged changed = (onChanged) getActivity();
        changed.onChanged();
    }
}