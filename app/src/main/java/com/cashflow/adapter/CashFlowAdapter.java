package com.cashflow.adapter;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_CREDIT;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_DEBIT;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_INDIVIDUAL;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_MONTHLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_WEEKLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_YEARLY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.cashflow.R;
import com.cashflow.activity.CashFlowActivity;
import com.cashflow.db.cashflow.CashItem;
import com.cashflow.fragments.StatementFragment;
import com.cashflow.helper.Constants;
import com.cashflow.helper.PrefHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CashFlowAdapter extends RecyclerView.Adapter<CashFlowAdapter.CashFlowViewHolder> {


    private static final String TAG = "CashFlowAdapter";
    List<CashItem> cashItemList;
    Context context;

    public CashFlowAdapter(List<CashItem> cashItemList, Context context, StatementFragment incomeFragment) {
        this.cashItemList = cashItemList;
        this.context = context;
    }

    @Override
    public CashFlowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CashFlowViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cash_flow_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(CashFlowAdapter.CashFlowViewHolder holder, int position) {

        Log.i(TAG, "onBindViewHolder: " + cashItemList.get(position).getTime());

        if (cashItemList.get(position).getType().equals(STATEMENT_TYPE_CREDIT)) {
            holder.amountTextView.setText("₹ " + cashItemList.get(position).getAmount());
            holder.amountTextView.setTextColor(Color.parseColor("#3fb950"));
        } else if (cashItemList.get(position).getType().equals(STATEMENT_TYPE_DEBIT)) {

            holder.amountTextView.setText("₹ " + Math.abs(cashItemList.get(position).getAmount()));
            holder.amountTextView.setTextColor(Color.parseColor("#da3633"));
        }
        holder.descTextView.setText(cashItemList.get(position).getDesc());
        holder.timeTextView.setText(getTimeData(cashItemList.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int viewMode = new PrefHelper(context).getIntPreference(Constants.CURRENT_VIEW_MODE, STATEMENT_VIEW_MODE_INDIVIDUAL);
                if (viewMode == STATEMENT_VIEW_MODE_INDIVIDUAL) {
                    context.startActivity(new Intent(context, CashFlowActivity.class).putExtra("id", cashItemList.get(position).getId()));
                    ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, "Edit allowed only for individual view", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String getTimeData(CashItem cashItem) {
        switch (cashItem.getViewMode()) {
            case STATEMENT_VIEW_MODE_WEEKLY: {
                return getWeeklyDate(cashItem);
            }
            case STATEMENT_VIEW_MODE_MONTHLY: {
                return getMonthlyDate(cashItem);
            }
            case STATEMENT_VIEW_MODE_YEARLY: {
                return getYearlyDate(cashItem);
            }
            default:
                return getDateAndTime(cashItem.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return cashItemList.size();
    }

    private String getDateAndTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    private String getWeeklyDate(CashItem cashItem) {
        Date sDate = new Date(cashItem.getStartDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        String startDate = sdf.format(sDate);

        Date eDate = new Date(cashItem.getEndDate());
        SimpleDateFormat edf = new SimpleDateFormat("dd MMM yyyy");
        String endDate = edf.format(eDate);

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        if (yearFormat.format(sDate).equals(yearFormat.format(eDate))) {
            return startDate + " to " + endDate;
        } else {
            Date syDate = new Date(cashItem.getStartDate());
            SimpleDateFormat sydf = new SimpleDateFormat("dd MMM yyyy");
            String startYearDate = sydf.format(syDate);
            return startYearDate + " to " + endDate;

        }

    }

    private String getMonthlyDate(CashItem cashItem) {
        Date date = new Date(cashItem.getStartDate());
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    private String getYearlyDate(CashItem cashItem) {
        Date date = new Date(cashItem.getStartDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public class CashFlowViewHolder extends RecyclerView.ViewHolder {

        TextView amountTextView, timeTextView, descTextView;

        public CashFlowViewHolder(View itemView) {
            super(itemView);

            amountTextView = itemView.findViewById(R.id.amount);
            timeTextView = itemView.findViewById(R.id.time);
            descTextView = itemView.findViewById(R.id.desc);
        }
    }

}
