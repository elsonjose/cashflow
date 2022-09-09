package com.cashflow.adapter;

import static com.cashflow.helper.Constants.STATEMENT_TYPE_EXPENSE;
import static com.cashflow.helper.Constants.STATEMENT_TYPE_INCOME;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_MONTHLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_WEEKLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_YEARLY;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashflow.R;
import com.cashflow.db.cashflow.CashItem;
import com.cashflow.fragments.StatementFragment;
import com.cashflow.interfaces.onDeleted;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CashFlowAdapter extends RecyclerView.Adapter<CashFlowAdapter.CashFlowViewHolder> {


    List<CashItem> cashItemList;
    Context context;
    onDeleted deleted;

    public CashFlowAdapter(List<CashItem> cashItemList, Context context, StatementFragment incomeFragment) {
        this.cashItemList = cashItemList;
        this.context = context;
        deleted = incomeFragment;
    }

    @Override
    public CashFlowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CashFlowViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cash_flow_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(CashFlowAdapter.CashFlowViewHolder holder, int position) {

        if (cashItemList.get(position).getType().equals(STATEMENT_TYPE_INCOME)) {
            holder.amountTextView.setText("+ ₹" + cashItemList.get(position).getAmount());
            holder.amountTextView.setTextColor(Color.parseColor("#3fb950"));
        } else if (cashItemList.get(position).getType().equals(STATEMENT_TYPE_EXPENSE)) {

            holder.amountTextView.setText("- ₹" + cashItemList.get(position).getAmount());
            holder.amountTextView.setTextColor(Color.parseColor("#da3633"));
        }
        holder.descTextView.setText(cashItemList.get(position).getDesc());
        holder.timeTextView.setText(getTimeData(cashItemList.get(position)));
        holder.viewTypeTextView.setText(getViewModeText(cashItemList.get(position).getViewMode()));

//        holder.delBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CashFlowDatabase database = Room.databaseBuilder(context,CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
//                database.getCashFlowDao().deleteItem(cashItemList.get(position));
//                cashItemList.remove(position);
//                notifyDataSetChanged();
//                deleted.onDeleted();
//
//            }
//        });

    }

    private String getViewModeText(int viewMode) {
        switch (viewMode)
        {
            case STATEMENT_VIEW_MODE_WEEKLY: {
                return "W";
            }
            case STATEMENT_VIEW_MODE_MONTHLY: {
                return "M";
            }
            case STATEMENT_VIEW_MODE_YEARLY: {
                return "Y";
            }
            default:
                return "I";
        }
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

        TextView amountTextView, timeTextView, descTextView, viewTypeTextView;

        public CashFlowViewHolder(View itemView) {
            super(itemView);

            amountTextView = itemView.findViewById(R.id.amount);
            timeTextView = itemView.findViewById(R.id.time);
            descTextView = itemView.findViewById(R.id.desc);
            viewTypeTextView = itemView.findViewById(R.id.view_type);
        }
    }

}
