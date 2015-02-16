package com.app.university;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class CourseTimeActivity extends Activity {

    private String[] weekDay = new String[] {"MON", "TUE", "WED", "THU", "FRI"};
    private Spinner spWeekDat;
    private ArrayAdapter<String> listAdapter;
    private String timeString = "";
    private int startHR = 0;
    private int startMin = 0;
    private int endHR = 0;
    private int endMin = 0;
    private String day = "";
    private String courseID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_time);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        timeString = bundle.getString(Data.COURSE_TIME);
        courseID =  bundle.getString(Data.COURSE_ID);
        day = timeString.substring(0,3);
        startHR = Integer.valueOf(timeString.substring(4, 6));
        startMin = Integer.valueOf(timeString.substring(7, 9));
        endHR = Integer.valueOf(timeString.substring(10, 12));
        endMin = Integer.valueOf(timeString.substring(13, 15));

        EditText editCourseName =  (EditText)findViewById(R.id.edit_course_name);
        final TextView txStartTime = (TextView)findViewById(R.id.tx_start_time);
        final TextView txEndTime = (TextView)findViewById(R.id.tx_end_time);
        txStartTime.setText(String.valueOf(startHR)+":"+String.valueOf(startMin));
        txEndTime.setText(String.valueOf(endHR)+":"+String.valueOf(endMin));

        editCourseName.setText(bundle.getString(Data.COURSE_NAME));
        spWeekDat = (Spinner) findViewById(R.id.sp_week_day);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weekDay);
        listAdapter.setDropDownViewResource(R.layout.week_day);
        spWeekDat.setAdapter(listAdapter);
        spWeekDat.setSelection(Arrays.asList(weekDay).indexOf(day));

        txStartTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(CourseTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                startHR = hourOfDay;
                                startMin = minute;

                                txStartTime.setText(String.valueOf(startHR)+":"+String.valueOf(startMin));
                                txEndTime.setText(String.valueOf(endHR)+":"+String.valueOf(endMin));

                            }
                        }, startHR, startMin, true).show();
            }
        });

        txEndTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(CourseTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endHR = hourOfDay;
                                endMin = minute;
                                txStartTime.setText(String.valueOf(startHR)+":"+String.valueOf(startMin));
                                txEndTime.setText(String.valueOf(endHR)+":"+String.valueOf(endMin));
                            }
                        }, endHR, endMin, true).show();
            }
        });

        Button doneButton = (Button)findViewById(R.id.btn_done);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(startHR < endHR || ((startHR==endHR)&&(startMin<endMin))){
                    JSONArray mCourseJsonArray;
                    SharedPreferences settings = CourseTimeActivity.this.getSharedPreferences("ID", Context.MODE_PRIVATE);
                    String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
                    try {
                        mCourseJsonArray = new JSONArray(jsonCoursString);
                    } catch (JSONException e) {
                        mCourseJsonArray = new JSONArray();
                    }
                    String week = spWeekDat.getSelectedItem().toString();

                    String finalTimeString = week + "-"+ String.format("%02d", startHR) + ":" + String.format("%02d", startMin) + "~" + String.format("%02d", endHR) + ":" + String.format("%02d", endMin);
                    for (int i=0;i<mCourseJsonArray.length();i++){
                        try {
                            if( ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_ID).compareTo(courseID) == 0){
                                String tmpTimeString = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_TIME);
                                tmpTimeString = tmpTimeString.replace(timeString,finalTimeString);
                                ((JSONObject)mCourseJsonArray.get(i)).put(Data.COURSE_TIME,tmpTimeString);

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(Data.CURRENTCOURSE, mCourseJsonArray.toString());
                                editor.apply();
                                CourseTimeActivity.this.setResult(Activity.RESULT_OK);
                                CourseTimeActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    Toast.makeText(CourseTimeActivity.this, R.string.end_time_small, Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });

    }



}
