package com.example.sasha.singletask.choice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.Utils;


public class SelectTimeFragment extends Fragment {

    NumberPicker hourNumberPicker;
    NumberPicker minuteNumberPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_time, null);

        hourNumberPicker = (NumberPicker) view.findViewById(R.id.numberPickerHour);
        minuteNumberPicker = (NumberPicker) view.findViewById(R.id.numberPickerMinute);

        hourNumberPicker.setMinValue(0);
        hourNumberPicker.setMaxValue(23);

        minuteNumberPicker.setMinValue(0);
        minuteNumberPicker.setMaxValue(59);

        return view;
    }

    public int getTime() {
        int hours = hourNumberPicker.getValue();
        int minutes = minuteNumberPicker.getValue();

        return Utils.getTimeAsInt(Utils.getTimeAsString(hours, minutes));
    }
}
