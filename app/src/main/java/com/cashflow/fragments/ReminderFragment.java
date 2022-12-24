package com.cashflow.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cashflow.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ReminderFragment extends Fragment {

    TextView header, emptyTextView;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    View view;
    LinearLayout bottomHeaderWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view=  inflater.inflate(R.layout.fragment_tab_item, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        emptyTextView = view.findViewById(R.id.empty_statement_text_view);

        bottomHeaderWrapper = view.findViewById(R.id.title_textView_wrapper);
        bottomHeaderWrapper.setVisibility(View.GONE);

        emptyTextView.setText("Working on it");
        emptyTextView.setVisibility(View.VISIBLE);

        return view;
    }
}