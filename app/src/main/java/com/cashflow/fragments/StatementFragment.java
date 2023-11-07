package com.cashflow.fragments;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_CREDIT;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_DEBIT;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_UNKNOWN;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_INDIVIDUAL;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cashflow.R;
import com.cashflow.adapter.CashFlowAdapter;
import com.cashflow.db.cashflow.CashItem;
import com.cashflow.helper.CashFlowHelper;
import com.cashflow.helper.Constants;
import com.cashflow.helper.DateTimeHelper;
import com.cashflow.helper.PrefHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StatementFragment extends Fragment {

    private static final String TAG = "StatementFragment";
    public boolean isDateRangePicked = false;
    public long startDate = 0, endDate = 0;
    public TextView dateRangeView;
    public CashFlowHelper cashFlowHelper;
    TextView incomeTextView, expenseTextView, expenseTitleTextView, incomeTitleTextView;
    RecyclerView recyclerView;
    CashFlowAdapter adapter;
    List<CashItem> realStatementList = new ArrayList<>();
    View view;
    TextView emptyStatementTextView;
    RelativeLayout contentWrapper;

    public StatementFragment() {
        cashFlowHelper = new CashFlowHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_item, container, false);

        dateRangeView = view.findViewById(R.id.date_range_text_view);

        expenseTextView = view.findViewById(R.id.title_expense_textView);
        expenseTitleTextView = view.findViewById(R.id.title_expense_title_textView);
        expenseTextView.setTextColor(Color.parseColor("#da3633"));
        expenseTitleTextView.setTextColor(Color.parseColor("#da3633"));

        incomeTextView = view.findViewById(R.id.title_income_textView);
        incomeTitleTextView = view.findViewById(R.id.title_income_title_textView);
        incomeTextView.setTextColor(Color.parseColor("#3fb950"));
        incomeTitleTextView.setTextColor(Color.parseColor("#3fb950"));

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CashFlowAdapter(realStatementList, getContext(), this);
        recyclerView.setAdapter(adapter);

        emptyStatementTextView = view.findViewById(R.id.empty_statement_text_view);
        emptyStatementTextView.setText("No statements to view.");
        contentWrapper = view.findViewById(R.id.content_wrapper);

        loadStatement(startDate, endDate);

//        recyclerView.setOnScrollChangeListener();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !cashFlowHelper.allItemsFetched) {
                    loadStatement(startDate, endDate);
                    Log.i(TAG, "onScrolled: loading more items");
                    Log.i(TAG, "onScrolled: dx "+dx+" dy "+dy);
                } else {
                    Toast.makeText(getContext(), "No more items to view", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onScrolled: end reached");
                }
            }
        });

        return view;
    }

    public void loadStatementForDateRange(long start, long end) {
        dateRangeView.setText("Range selected: " + DateTimeHelper.getDate(start) + " to " + DateTimeHelper.getDate(end));
        dateRangeView.setVisibility(View.VISIBLE);
        isDateRangePicked = true;

        // add end of day millis to end datetime
        end += (24 * 60 * 60 * 1000) - 1000;

        int viewMode = new PrefHelper(getContext()).getIntPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_INDIVIDUAL);
        List<CashItem> statementList = cashFlowHelper.getCashItems(viewMode, start, end);

        Log.i(TAG, "loadStatement: items loaded: " + statementList.size());

        long totalCount = statementList.size();
        if (totalCount > 0) {
            emptyStatementTextView.setVisibility(View.GONE);
            contentWrapper.setVisibility(View.VISIBLE);
        } else {
            emptyStatementTextView.setVisibility(View.VISIBLE);
            contentWrapper.setVisibility(View.GONE);
        }
        expenseTextView.setText("₹ " + cashFlowHelper.getTotalAmountForType(STATEMENT_TYPE_DEBIT, start, end));
        incomeTextView.setText("₹ " + cashFlowHelper.getTotalAmountForType(STATEMENT_TYPE_CREDIT, start, end));
        populateData(statementList);
    }

    public void loadStatement(long start, long end) {
        realStatementList.clear();
        recyclerView.removeAllViews();

        startDate = start;
        endDate = end;

        Log.i(TAG, "loadStatement: start " + startDate + " end " + endDate);

        if (isDateRangePicked) {
            loadStatementForDateRange(start, end);
        } else {
            int viewMode = new PrefHelper(getContext()).getIntPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_INDIVIDUAL);
            List<CashItem> statementList = cashFlowHelper.getCashItems(viewMode, 0, 0);

            Log.i(TAG, "loadStatement: items loaded: " + statementList.size());

            long totalCount = statementList.size();
            if (totalCount > 0) {
                emptyStatementTextView.setVisibility(View.GONE);
                contentWrapper.setVisibility(View.VISIBLE);
            } else {
                emptyStatementTextView.setVisibility(View.VISIBLE);
                contentWrapper.setVisibility(View.GONE);
            }
            expenseTextView.setText("₹ " + cashFlowHelper.getTotalAmountForType(STATEMENT_TYPE_DEBIT, start, end));
            incomeTextView.setText("₹ " + cashFlowHelper.getTotalAmountForType(STATEMENT_TYPE_CREDIT, start, end));
            populateData(statementList);
        }
    }

    private void populateData(List<CashItem> statementList) {
        for (CashItem item : statementList) {
            if (!item.getType().equals(STATEMENT_TYPE_UNKNOWN)) {
                realStatementList.add(item);
            }
        }
        Collections.sort(realStatementList);
        adapter.notifyDataSetChanged();
    }
}