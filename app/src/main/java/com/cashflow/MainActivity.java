package com.cashflow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.cashflow.db.CashFlowDatabase;
import com.cashflow.fragments.ExpenseFragment;
import com.cashflow.fragments.IncomeFragment;
import com.cashflow.interfaces.onChanged;
import com.cashflow.interfaces.onDeleted;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements onChanged {

    ViewPager mainViewPager;
    TextView headerTextView;
    TabLayout tabLayout;
    CardView actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionbar = findViewById(R.id.actionbar);
        tabLayout = findViewById(R.id.tab_layout);
        headerTextView = findViewById(R.id.actionbar_textView);
        mainViewPager = findViewById(R.id.main_view_pager);
        mainViewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager(),0));
        tabLayout.setupWithViewPager(mainViewPager);

        CashFlowDatabase database = Room.databaseBuilder(getApplicationContext(),CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
        double income = database.getCashFlowDao().getAmountSum("income");
        double expense = database.getCashFlowDao().getAmountSum("expense");
        double diff = income-expense;
        if(diff>0)
        {
            headerTextView.setText("Balance: ₹ "+Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#3fb950"));
        }
        else if(diff<0)
        {
            headerTextView.setText("Balance: ₹ "+Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#da3633"));
        }
        else
        {
            headerTextView.setText("Balance: ₹ "+Math.abs(diff));

        }
    }

    @Override
    public void onChanged() {
        CashFlowDatabase database = Room.databaseBuilder(getApplicationContext(),CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
        double income = database.getCashFlowDao().getAmountSum("income");
        double expense = database.getCashFlowDao().getAmountSum("expense");
        double diff = income-expense;
        if(diff>=0)
        {
            headerTextView.setText("Balance: ₹ "+Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#3fb950"));
        }
        else if(diff<0)
        {
            headerTextView.setText("Balance: ₹ "+Math.abs(diff));
            headerTextView.setTextColor(Color.parseColor("#da3633"));
        }
        else
        {
            headerTextView.setText("Balance: ₹ "+Math.abs(diff));

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
                return new IncomeFragment();
            }
            return new ExpenseFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position)
            {
                case 1:return "Income";
                default:return "Expense";
            }
        }

        @Override
        public int getCount() {
            return 2;        }
    }

}