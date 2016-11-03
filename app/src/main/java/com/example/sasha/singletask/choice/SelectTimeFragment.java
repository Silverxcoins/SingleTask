package com.example.sasha.singletask.choice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.sasha.singletask.R;


public class SelectTimeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_time, null);

        NumberPicker hourNumberPicker = (NumberPicker) view.findViewById(R.id.numberPickerHour);
        NumberPicker minuteNumberPicker = (NumberPicker) view.findViewById(R.id.numberPickerMinute);

        hourNumberPicker.setMinValue(0);
        hourNumberPicker.setMaxValue(23);

        minuteNumberPicker.setMinValue(0);
        minuteNumberPicker.setMaxValue(59);

        return view;
    }
}
