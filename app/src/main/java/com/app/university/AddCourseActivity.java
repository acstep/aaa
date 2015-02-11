package com.app.university;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class AddCourseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Button doneButton = (Button)findViewById(R.id.btn_create_course_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Data.COURSE_SCHEDULE_SET,true);
                editor.apply();

                Intent intent = new Intent(arg0.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }



}
