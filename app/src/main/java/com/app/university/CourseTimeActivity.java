package com.app.university;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CourseTimeActivity extends Activity {


    private Spinner spWeekDat;
    private ArrayAdapter<String> listAdapter;
    private String timeString = "";
    private int startHR = 0;
    private int startMin = 0;
    private int endHR = 0;
    private int endMin = 0;
    private String day = "";
    private String courseID = "";
    private boolean mNewCourse = false;
    private CourseColor courseColor;
    private Context mContext = this;
    private RequestQueue mQueue = null;


    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("AddCourseActivity", error.getMessage(), error);
            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    String schedule = jsonObject.getString(NETTag.POST_COURSE_STRING);
                    SharedPreferences settings = mContext.getSharedPreferences ("ID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Data.CURRENTCOURSE, schedule);
                    editor.commit();
                    CourseTimeActivity.this.setResult(Activity.RESULT_OK);
                    CourseTimeActivity.this.finish();
                }
                else{
                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        }
    };

    private int getWeekDay(String day){
        switch(day){
            case "MON":
                return 0;
            case "TUE":
                return 1;
            case "WED":
                return 2;
            case "THU":
                return 3;
            case "FRI":
                return 4;
            default:
                return 0;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_time);
        mQueue = Volley.newRequestQueue(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        timeString = bundle.getString(Data.COURSE_TIME);
        courseID =  bundle.getString(Data.COURSE_ID);
        if(courseID.compareTo("") == 0){
            mNewCourse = true;
            getActionBar().setTitle(R.string.course_add);
        }
        day = timeString.substring(0,3);
        startHR = Integer.valueOf(timeString.substring(4, 6));
        startMin = Integer.valueOf(timeString.substring(7, 9));
        endHR = Integer.valueOf(timeString.substring(10, 12));
        endMin = Integer.valueOf(timeString.substring(13, 15));

        EditText editCourseName =  (EditText)findViewById(R.id.edit_course_name);
        final TextView txStartTime = (TextView)findViewById(R.id.tx_start_time);
        final TextView txEndTime = (TextView)findViewById(R.id.tx_end_time);
        txStartTime.setText(String.format("%02d", startHR)+":"+(String.format("%02d", startMin)));
        txEndTime.setText(String.format("%02d", endHR)+":"+(String.format("%02d", endMin)));

        editCourseName.setText(bundle.getString(Data.COURSE_NAME));
        spWeekDat = (Spinner) findViewById(R.id.sp_week_day);
        spWeekDat.setSelection( getWeekDay(day));

        txStartTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(CourseTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                startHR = hourOfDay;
                                startMin = minute;
                                txStartTime.setText(String.format("%02d", startHR)+":"+String.format("%02d", startMin));
                                txEndTime.setText(String.format("%02d", endHR)+":"+String.format("%02d", endMin));

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
                                txStartTime.setText(String.format("%02d", startHR)+":"+String.format("%02d", startMin));
                                txEndTime.setText(String.format("%02d", endHR)+":"+String.format("%02d", endMin));
                            }
                        }, endHR, endMin, true).show();
            }
        });

        Button doneButton = (Button)findViewById(R.id.btn_done);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(startHR < endHR || ((startHR==endHR)&&(startMin<endMin))){
                    EditText editCourseName =  (EditText)findViewById(R.id.edit_course_name);
                    String courseName = editCourseName.getText().toString();
                    if(courseName.isEmpty()){
                        Toast.makeText(CourseTimeActivity.this, R.string.empty_course_name, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences settings = mContext.getSharedPreferences("ID", Context.MODE_PRIVATE);
                    String preJsonCourseString = settings.getString(Data.CURRENTCOURSE, "[]");

                    JSONArray postJsonArray;
                    try {
                        postJsonArray = new JSONArray(preJsonCourseString);
                    } catch (JSONException e) {
                        postJsonArray = new JSONArray();
                    }

                    String postCourseString = "";

                    String week = spWeekDat.getSelectedItem().toString();
                    String finalTimeString = week + "-"+ String.format("%02d", startHR) + ":" + String.format("%02d", startMin) + "~" + String.format("%02d", endHR) + ":" + String.format("%02d", endMin);

                    if(mNewCourse){
                        JSONObject courseItem = new JSONObject();
                        courseColor = new CourseColor(preJsonCourseString);
                        try {
                            courseItem.put(Data.COURSE_NAME ,courseName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            courseItem.put(Data.COURSE_TEACHER ,"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            courseItem.put(Data.COURSE_TIME ,finalTimeString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String color = courseColor.getNewColor();
                            if(color.length() == 0){
                                color = "6698ff";
                            }
                            courseItem.put(Data.COURSE_COLOR  ,color);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {

                            String myid = settings.getString(Data.USER_ID, null);
                            java.util.Date date= new java.util.Date();
                            courseItem.put(Data.COURSE_ID ,CommonUtil.getMD5(courseName+myid+ String.valueOf(date.getTime())));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        postJsonArray.put(courseItem);
                        postCourseString = postJsonArray.toString();

                    }
                    else{
                        for (int i=0;i<postJsonArray.length();i++){
                            try {
                                if( ((JSONObject)postJsonArray.get(i)).getString(Data.COURSE_ID).compareTo(courseID) == 0){
                                    String tmpTimeString = ((JSONObject)postJsonArray.get(i)).getString(Data.COURSE_TIME);
                                    tmpTimeString = tmpTimeString.replace(timeString,finalTimeString);
                                    ((JSONObject)postJsonArray.get(i)).put(Data.COURSE_TIME,tmpTimeString);
                                    ((JSONObject)postJsonArray.get(i)).put(Data.COURSE_NAME,courseName);
                                    postCourseString = postJsonArray.toString();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    AddCourseRequest stringRequest = new AddCourseRequest(mContext, NETTag.API_ADD_COURSE , "0", listener, errorListener,preJsonCourseString,postCourseString, "1");
                    mQueue.add(stringRequest);

                }
                else{
                    Toast.makeText(CourseTimeActivity.this, R.string.end_time_small, Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }



}
