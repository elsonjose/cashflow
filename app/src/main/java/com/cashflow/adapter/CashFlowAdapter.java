package com.cashflow.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.cashflow.R;
import com.cashflow.db.CashFlowDatabase;
import com.cashflow.db.CashItem;
import com.cashflow.fragments.ExpenseFragment;
import com.cashflow.fragments.IncomeFragment;
import com.cashflow.interfaces.onDeleted;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CashFlowAdapter extends RecyclerView.Adapter<CashFlowAdapter.CashFlowViewHolder> {


    String type;
    List<CashItem> cashItemList;
    Context context;
    onDeleted deleted;

    public CashFlowAdapter(String type, List<CashItem> cashItemList, Context context, IncomeFragment incomeFragment) {
        this.type = type;
        this.cashItemList = cashItemList;
        this.context = context;
        deleted = incomeFragment;
    }
    public CashFlowAdapter(String type, List<CashItem> cashItemList, Context context, ExpenseFragment incomeFragment) {
        this.type = type;
        this.cashItemList = cashItemList;
        this.context = context;
        deleted = incomeFragment;
    }

    @Override
    public CashFlowViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new CashFlowViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cash_flow_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder( CashFlowAdapter.CashFlowViewHolder holder, int position) {

        if(type.equals("income"))
        {
            holder.amountTextView.setText("+ ₹"+cashItemList.get(position).getAmount());
            holder.amountTextView.setTextColor(Color.parseColor("#3fb950"));
        }
        else
        {
            holder.amountTextView.setText("- ₹"+cashItemList.get(position).getAmount());
            holder.amountTextView.setTextColor(Color.parseColor("#da3633"));
        }
        holder.descTextView.setText(cashItemList.get(position).getDesc());
        holder.timeTextView.setText(getDateAndTime(cashItemList.get(position).getTime()));

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CashFlowDatabase database = Room.databaseBuilder(context,CashFlowDatabase.class,"CashFlow").allowMainThreadQueries().build();
                database.getCashFlowDao().deleteItem(cashItemList.get(position));
                cashItemList.remove(position);
                notifyDataSetChanged();
                deleted.onDeleted();

            }
        });

    }

    @Override
    public int getItemCount() {
        return cashItemList.size();
    }

    public class CashFlowViewHolder extends RecyclerView.ViewHolder {

        TextView amountTextView,timeTextView,descTextView;
        ImageButton delBtn;
        public CashFlowViewHolder( View itemView) {
            super(itemView);

            amountTextView = itemView.findViewById(R.id.amount);
            timeTextView = itemView.findViewById(R.id.time);
            descTextView = itemView.findViewById(R.id.desc);
            delBtn = itemView.findViewById(R.id.delete_btn);
        }
    }

    public String getDateAndTime(String timestamp)
    {

        Date date = new Date(Long.parseLong(timestamp));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String formattedDate = sdf.format(date);
        return  formattedDate;
    }

}
