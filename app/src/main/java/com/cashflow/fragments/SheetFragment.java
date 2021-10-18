package com.cashflow.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cashflow.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class SheetFragment extends Fragment {

    TextView header;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view=  inflater.inflate(R.layout.fragment_income, container, false);
        header = view.findViewById(R.id.title_textView);
        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        header.setText("Net Worth: ");

        return view;
    }
}