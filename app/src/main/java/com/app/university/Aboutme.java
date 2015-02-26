package com.app.university;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by matt on 2015/2/17.
 */
public class Aboutme extends Fragment {
    private LinearLayout mAboutme;
    private LinearLayout mAddCourse;
    private LinearLayout mAddNewCourse;
    private LinearLayout mLogout;
    private Spinner mNotifyType;

    private View mView;
    private static final String IMAGE_FILE_NAME = "face.jpg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.about_me, container, false);

        mAboutme = (LinearLayout)mView.findViewById(R.id.about_me);
        mAboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                Log.d("Aboutme ", "pressed");
            }
        });

        mAddCourse = (LinearLayout)mView.findViewById(R.id.add_course);
        mAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivity(intent);
                Log.d("Aboutme ", "add_course pressed");
            }
        });

        mAddNewCourse = (LinearLayout)mView.findViewById(R.id.create_course);
        mAddNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivity(intent);
                Log.d("Aboutme ", "add_course pressed");
            }
        });

        mNotifyType = (Spinner)mView.findViewById(R.id.spinner_profile_notify);
        SharedPreferences settings = getActivity().getSharedPreferences ("ID", Context.MODE_PRIVATE);
        mNotifyType.setSelection(settings.getInt(Data.NOTIFICATION_TYPE,0));
        mNotifyType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = getActivity().getSharedPreferences ("ID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Data.NOTIFICATION_TYPE, position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mLogout = (LinearLayout)mView.findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences settings = getActivity().getSharedPreferences ("ID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                Log.d("Aboutme ", "add_course pressed");
            }
        });




        return mView;
    }

    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

    }





}
