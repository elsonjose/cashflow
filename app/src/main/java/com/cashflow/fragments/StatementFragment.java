package com.cashflow.fragments;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_DEBIT;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_CREDIT;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_UNKNOWN;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_DEFAULT;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.cashflow.R;
import com.cashflow.adapter.CashFlowAdapter;
import com.cashflow.db.cashflow.CashFlowDatabase;
import com.cashflow.db.cashflow.CashItem;
import com.cashflow.helper.CashFlowViewTypeHelper;
import com.cashflow.helper.Constants;
import com.cashflow.helper.PrefHelper;
import com.cashflow.interfaces.onChanged;
import com.cashflow.interfaces.onDeleted;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StatementFragment extends Fragment implements onDeleted {

    private static final String TAG = "StatementFragment";

    TextView incomeTextView, expenseTextView;
    RecyclerView recyclerView;
    CashFlowAdapter adapter;
    List<CashItem> realStatementList = new ArrayList<>();
    public boolean isDateRangePicked = false;
    View view;
    TextView emptyStatementTextView;
    RelativeLayout contentWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_item, container, false);

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

        emptyStatementTextView  = view.findViewById(R.id.empty_statement_text_view);
        emptyStatementTextView.setText("No statements to view.");
        contentWrapper  = view.findViewById(R.id.content_wrapper);

        loadStatement(0,0);
        return view;
    }

    public void loadStatementForDateRange(long start, long end){
        isDateRangePicked = true;
        realStatementList.clear();

        CashFlowDatabase database = Room.databaseBuilder(getContext(), CashFlowDatabase.class, "CashFlow").fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        List<CashItem> statementList = database.getCashFlowDao().getAmountForDateRange(start,end);

        long totalCount = database.getCashFlowDao().getTotalCount();
        if (totalCount > 0) {
            emptyStatementTextView.setVisibility(View.GONE);
            contentWrapper.setVisibility(View.VISIBLE);
            int viewMode = new PrefHelper(getContext()).getIntPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_DEFAULT);
            statementList = new CashFlowViewTypeHelper(viewMode).GetCashFlowList(statementList, start, end);
        }
        else
        {
            emptyStatementTextView.setVisibility(View.VISIBLE);
            contentWrapper.setVisibility(View.GONE);
        }

        expenseTextView.setText("Debit: -₹ " + database.getCashFlowDao().getSumAmountForDateRange(start,end, STATEMENT_TYPE_DEBIT));
        incomeTextView.setText("Credit: ₹ " + database.getCashFlowDao().getSumAmountForDateRange(start,end, STATEMENT_TYPE_CREDIT));
        populateData(statementList);
    }

    public void loadStatement(long start, long end) {

        if(isDateRangePicked)
        {
            loadStatementForDateRange(start,end);
        }
        else
        {
            realStatementList.clear();

            CashFlowDatabase database = Room.databaseBuilder(getContext(), CashFlowDatabase.class, "CashFlow").fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();

            List<CashItem> statementList = database.getCashFlowDao().getAllItems();
            Collections.sort(statementList);

            long totalCount = database.getCashFlowDao().getTotalCount();
            if (totalCount > 0) {
                long startTime = database.getCashFlowDao().getStartTimestamp();
                long endTime = database.getCashFlowDao().getEndTimestamp();
                int viewMode = new PrefHelper(getContext()).getIntPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_DEFAULT);
                statementList = new CashFlowViewTypeHelper(viewMode).GetCashFlowList(statementList, startTime, endTime);

                emptyStatementTextView.setVisibility(View.GONE);
                contentWrapper.setVisibility(View.VISIBLE);
            }
            else
            {
                emptyStatementTextView.setVisibility(View.VISIBLE);
                contentWrapper.setVisibility(View.GONE);
            }
            expenseTextView.setText("Debit: -₹ " + database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_DEBIT));
            incomeTextView.setText("Credit: ₹ " + database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_CREDIT));
            populateData(statementList);

        }

    }

    private void populateData(List<CashItem> statementList){
        for (CashItem item : statementList) {
            if (!item.getType().equals(STATEMENT_TYPE_UNKNOWN)) {
                realStatementList.add(item);
            }
        }
        Collections.sort(realStatementList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleted() {
        CashFlowDatabase database = Room.databaseBuilder(getContext(), CashFlowDatabase.class, "CashFlow").allowMainThreadQueries().build();
        expenseTextView.setText("Total: -₹ " + database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_DEBIT));
        incomeTextView.setText("Total: ₹ " + database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_CREDIT));
        onChanged changed = (onChanged) getActivity();
        changed.onChanged();
    }
}