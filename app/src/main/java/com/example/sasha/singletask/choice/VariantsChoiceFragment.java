package com.example.sasha.singletask.choice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsAdapter;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsDataSource;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsItem;

public class VariantsChoiceFragment {

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_variants_choice, null);
//
//
//
//        return view;
//    }
//
//    private void initRecyclerView() {
//        RecyclerView recyclerView =
//                (RecyclerView) getActivity().findViewById(R.id.categories_recycler_view);
//        dataSource = new VariantsDataSource(recyclerView);
//        layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
//        VariantsAdapter adapter = new VariantsAdapter(this, dataSource);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(layoutManager);
//        (findViewById(R.id.btnAddVariant)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View viewInFocus = getCurrentFocus();
//                if (dataSource.getCount() == 0
//                        || viewInFocus == null
//                        || viewInFocus == categoryNameEditText
//                        || !((EditText) viewInFocus).getText().toString().isEmpty()) {
//                    dataSource.addItem(new VariantsItem());
//                    layoutManager.scrollToPosition(dataSource.getCount() - 1);
//                } else {
//                    viewInFocus.setEnabled(true);
//                    viewInFocus.requestFocus();
//                }
//            }
//        });
//    }

}
