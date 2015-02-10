package com.app.university;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created by matt on 2015/2/6.
 */
public class Schedule extends Fragment {

    private FrameLayout timeFrame;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.schedule, container, false);

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels;
        float dpWidth = displayMetrics.widthPixels;
        int timeBlockHeight = (int) (dpHeight/14);
        timeFrame = (FrameLayout) view.findViewById(R.id.timeframe);
        int timeStartTop = (int) (40 * displayMetrics.density);
        int timeStart  = 8;
        for (int i = 1; i <= 14; i++) {

            Button btn = new Button(getActivity());

            btn.setText(Integer.toString(timeStart) + ":00");
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params1.setMargins(0,timeStartTop,0,0);

            params1.height = timeBlockHeight;

            btn.setLayoutParams(params1);
            btn.setTextSize(12);
            btn.setPadding(2,2,2,2);
            btn.setGravity(Gravity.TOP|Gravity.CENTER);
            btn.setBackgroundColor(Color.rgb(255, 255, 255));
            timeStart = timeStart + 1;

            timeStartTop = timeStartTop + timeBlockHeight;
            timeFrame.addView(btn);

        }
        return view;
    }
}