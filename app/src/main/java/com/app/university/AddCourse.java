package com.app.university;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class AddCourse extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Button loginButton = (Button)findViewById(R.id.btn_create_course_done);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }



}
