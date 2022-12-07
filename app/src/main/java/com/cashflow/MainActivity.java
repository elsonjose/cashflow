package com.cashflow;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_CREDIT;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_DEBIT;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_DEFAULT;
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
import com.cashflow.fragments.ReminderFragment;
import com.cashflow.fragments.StatementFragment;
import com.cashflow.helper.Constants;
import com.cashflow.helper.PrefHelper;
import com.cashflow.interfaces.onChanged;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements onChanged {

    private static final String TAG = "MainActivityTAG";
    ViewPager mainViewPager;
    TextView headerTextView;
    TabLayout tabLayout;
    CardView actionbar;
    ImageButton filterBtn, addBtn, viewModeBtn;
    LinearLayout btnWrapper;
    ReminderFragment reminderFragment;
    StatementFragment statementFragment;
    long filterStart = 0, filterEnd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reminderFragment = new ReminderFragment();
        statementFragment = new StatementFragment();

        btnWrapper = findViewById(R.id.actionbar_btn_wrapper);
        filterBtn = findViewById(R.id.actionbar_filter_btn);
        addBtn = findViewById(R.id.actionbar_add_btn);
        viewModeBtn = findViewById(R.id.actionbar_view_btn);
        actionbar = findViewById(R.id.actionbar);
        tabLayout = findViewById(R.id.tab_layout);
        headerTextView = findViewById(R.id.actionbar_textView);
        mainViewPager = findViewById(R.id.main_view_pager);
        mainViewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager(), 0));
        tabLayout.setupWithViewPager(mainViewPager);

        CashFlowDatabase database = Room.databaseBuilder(getApplicationContext(), CashFlowDatabase.class, "CashFlow")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        double income = database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_CREDIT);
        double expense = database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_DEBIT);
        double diff = income - expense;
        if (diff > 0) {
            headerTextView.setText("Balance: ₹ " + Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#3fb950"));
        } else if (diff < 0) {
            headerTextView.setText("Balance: - ₹ " + Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#da3633"));
        } else {
            headerTextView.setText("Balance: ₹ " + Math.abs(diff));
        }

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    btnWrapper.setVisibility(View.VISIBLE);
                } else {
                    btnWrapper.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, filterBtn);

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

        filterBtn.setOnClickListener(v -> {
            MaterialDatePicker dateRangePicker = MaterialDatePicker.Builder.dateRangePicker().build();
            dateRangePicker.show(getSupportFragmentManager(), dateRangePicker.getTag());
            dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                statementFragment.isDateRangePicked = true;
                String[] ranges = selection.toString().replace("Pair{", "").replace("}", "").trim().split(" ");
                filterStart = Long.parseLong(ranges[0]);
                filterEnd = Long.parseLong(ranges[1]);
                statementFragment.loadStatement(filterStart, filterEnd);

            });
        });

        viewModeBtn.setOnClickListener(view -> {

            PopupMenu popupMenu = new PopupMenu(MainActivity.this, viewModeBtn);
            popupMenu.getMenuInflater().inflate(R.menu.statement_view_mode, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.view_mode_single: {
                            new PrefHelper(MainActivity.this).setPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_INDIVIDUAL);
                            break;
                        }
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
                            new PrefHelper(MainActivity.this).setPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_DEFAULT);
                        }
                    }
                    statementFragment.loadStatement(filterStart, filterEnd);
                    return true;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public void onChanged() {
        CashFlowDatabase database = Room.databaseBuilder(getApplicationContext(), CashFlowDatabase.class, "CashFlow").allowMainThreadQueries().build();
        double income = database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_CREDIT);
        double expense = database.getCashFlowDao().getAmountSum(STATEMENT_TYPE_DEBIT);
        double diff = income - expense;
        if (diff >= 0) {
            headerTextView.setText("Balance: ₹ " + Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#3fb950"));
        } else if (diff < 0) {
            headerTextView.setText("Balance: ₹ " + Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#da3633"));
        } else {
            headerTextView.setText("Balance: ₹ " + Math.abs(diff));

        }
    }

    @Override
    public void onBackPressed() {
        if(mainViewPager.getCurrentItem() != 0)
        {
            mainViewPager.setCurrentItem(0);
        }
        else if (statementFragment.isDateRangePicked) {
            statementFragment.isDateRangePicked = false;
            filterStart = 0;
            filterEnd = 0;
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
            if (position == 1) {
                return reminderFragment;
            }
            return statementFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return "Reminder";
                default:
                    return "Statement";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}