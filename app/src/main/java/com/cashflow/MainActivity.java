package com.cashflow;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_CREDIT;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_DEBIT;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_INDIVIDUAL;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_MONTHLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_WEEKLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_YEARLY;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.cashflow.activity.CashFlowActivity;
import com.cashflow.db.cashflow.CashFlowDatabase;
import com.cashflow.fragments.StatementFragment;
import com.cashflow.helper.CashFlowHelper;
import com.cashflow.helper.Constants;
import com.cashflow.helper.PrefHelper;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;

import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTAG";
    ViewPager mainViewPager;
    TextView headerTextView, balanceTextView;
    TabLayout tabLayout;
    CardView actionbar;
    ImageButton statementFilterBtn, statementAddBtn, statementViewModeBtn;
    ImageButton reminderAddBtn;
    LinearLayout statementBtnWrapper, reminderBtnWrapper;
    StatementFragment statementFragment;
    long filterStart = 0, filterEnd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CashFlowHelper.database = Room.databaseBuilder(MainActivity.this, CashFlowDatabase.class, "CashFlow").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        statementFragment = new StatementFragment();

        statementBtnWrapper = findViewById(R.id.actionbar_statement_btn_wrapper);
        reminderBtnWrapper = findViewById(R.id.actionbar_reminder_btn_wrapper);
        statementFilterBtn = findViewById(R.id.statement_filter_btn);
        statementAddBtn = findViewById(R.id.statement_add_btn);
        statementViewModeBtn = findViewById(R.id.statement_view_btn);

        reminderAddBtn = findViewById(R.id.reminder_add_btn);

        actionbar = findViewById(R.id.actionbar);
        tabLayout = findViewById(R.id.tab_layout);
        headerTextView = findViewById(R.id.actionbar_textView);
        balanceTextView = findViewById(R.id.actionbar_balance_textView);
        mainViewPager = findViewById(R.id.main_view_pager);
        mainViewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager(), 0));
        tabLayout.setupWithViewPager(mainViewPager);
        tabLayout.setVisibility(View.GONE);

        setBalanceAmount(0, 0);

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    statementBtnWrapper.setVisibility(View.VISIBLE);
                    reminderBtnWrapper.setVisibility(View.GONE);
                } else {
                    statementBtnWrapper.setVisibility(View.GONE);
                    reminderBtnWrapper.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        statementAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, statementAddBtn);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.statement_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        switch (menuItem.getItemId()) {
                            case R.id.income: {
                                startActivity(new Intent(MainActivity.this, CashFlowActivity.class).putExtra("type", STATEMENT_TYPE_CREDIT));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                                break;
                            }
                            default: {
                                startActivity(new Intent(MainActivity.this, CashFlowActivity.class).putExtra("type", STATEMENT_TYPE_DEBIT));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });

        statementFilterBtn.setOnClickListener(v -> {

            MaterialDatePicker dateRangePicker = MaterialDatePicker.Builder.dateRangePicker().build();
            dateRangePicker.show(getSupportFragmentManager(), dateRangePicker.getTag());
            dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                statementFragment.isDateRangePicked = true;
                String[] ranges = selection.toString().replace("Pair{", "").replace("}", "").trim().split(" ");
                long offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
                filterStart = Long.parseLong(ranges[0]) - offset;
                filterEnd = Long.parseLong(ranges[1]) - offset;
                setBalanceAmount(filterStart, filterEnd + (24 * 60 * 60 * 1000) - 1000);
                statementFragment.loadStatement(filterStart, filterEnd);
            });
        });

        statementViewModeBtn.setOnClickListener(view -> {

            PopupMenu popupMenu = new PopupMenu(MainActivity.this, statementViewModeBtn);
            popupMenu.getMenuInflater().inflate(R.menu.statement_view_mode, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.view_mode_monthly: {
                            new PrefHelper(MainActivity.this).setPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_MONTHLY);
                            break;
                        }
                        case R.id.view_mode_yearly: {
                            new PrefHelper(MainActivity.this).setPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_YEARLY);
                            break;
                        }
                        case R.id.view_mode_weekly: {
                            new PrefHelper(MainActivity.this).setPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_WEEKLY);
                            break;
                        }
                        default: {
                            new PrefHelper(MainActivity.this).setPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_INDIVIDUAL);
                        }
                    }
                    statementFragment.loadStatement(filterStart, filterEnd);
                    return true;
                }
            });
            popupMenu.show();
        });
    }

    private void setBalanceAmount(long start, long end) {
        double income = statementFragment.cashFlowHelper.getTotalAmountForType(STATEMENT_TYPE_CREDIT, start, end);
        double expense = statementFragment.cashFlowHelper.getTotalAmountForType(STATEMENT_TYPE_DEBIT, start, end);
        double diff = getSubtractedValue(income, expense);
        if (diff > 0) {
            headerTextView.setText("₹ " + Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#3fb950"));
            balanceTextView.setTextColor(Color.parseColor("#3fb950"));
        } else if (diff < 0) {
            headerTextView.setText("₹ " + Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#da3633"));
            balanceTextView.setTextColor(Color.parseColor("#da3633"));
        } else {
            headerTextView.setText("₹ " + Math.abs(diff));
        }
    }

    private double getSubtractedValue(double income, double expense) {
        int noOfDecimals = Math.max(getNumberOfDecimals(income), getNumberOfDecimals(expense));
        double updatedIncome = income * Math.pow(10, noOfDecimals);
        double updatedExpense = expense * Math.pow(10, noOfDecimals);
        double diff = updatedIncome - updatedExpense;
        return diff * Math.pow(10, -1 * noOfDecimals);
    }

    private int getNumberOfDecimals(double value) {
        String val = String.valueOf(value);
        if (!val.contains(".")) {
            return 0;
        }
        String[] values = val.split("\\.");
        return values[1].length();
    }

    @Override
    public void onBackPressed() {
        if (mainViewPager.getCurrentItem() != 0) {
            mainViewPager.setCurrentItem(0);
        } else if (statementFragment.isDateRangePicked) {
            statementFragment.dateRangeView.setVisibility(View.GONE);
            statementFragment.isDateRangePicked = false;
            filterStart = 0;
            filterEnd = 0;
            setBalanceAmount(filterStart, filterEnd);
            statementFragment.loadStatement(filterStart, filterEnd);
        } else {
            super.onBackPressed();
        }
    }

    private class MainFragmentAdapter extends FragmentPagerAdapter {

        public MainFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return statementFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Statement";
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}