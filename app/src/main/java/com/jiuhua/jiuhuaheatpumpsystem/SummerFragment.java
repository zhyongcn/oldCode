package com.jiuhua.jiuhuaheatpumpsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class SummerFragment extends Fragment implements View.OnClickListener,
        TimePicker.OnTimeChangedListener, NumberPicker.OnValueChangeListener {

    private Button starttimebtn1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summer_fragment, container, false);
        starttimebtn1 = view.findViewById(R.id.starttime1);
        starttimebtn1.setBackgroundColor(Color.argb(125,0,0,255));
        starttimebtn1.setTextColor(Color.argb(255,255,255,255));
        starttimebtn1.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

    }
}
