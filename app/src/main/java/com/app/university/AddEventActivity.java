package com.app.university;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AddEventActivity extends Activity {

    private JSONArray mCourseJsonArray;
    private Calendar m_Calendar = Calendar.getInstance();
    private Context mContext;
    private TextView mDateText;
    private TextView mHourText;
    private Spinner mCourseSpinner;
    private Spinner mEventType;
    private CheckBox mShareCheckBox;
    private EditText mContent;
    private EditText mLoc;
    private int mYear = 9999;
    private int mMonth = 0;
    private int mDay = 0;
    private int mHour = 99;
    private int mMin = 0;
    private Calendar eventCalendar = Calendar.getInstance();

    private List<String> mCourseName = new ArrayList<String>();
    private List<CourseInfo> mCourseList = new ArrayList<CourseInfo>();;
    private JSONArray mEventJsonArray;

    class CourseInfo{
        String id = "";
        String name = "";
    }

    class EventInfo{
        String name = "";
        String courseid = "";
        String content = "";
        long time = 0;
        String loc = "";
        int type = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Tracker t = ((UniversityApp) getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("Add schedule event");
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        mContext = this;
        SharedPreferences settings = getSharedPreferences("ID", Context.MODE_PRIVATE);
        String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
        try {
            mCourseJsonArray = new JSONArray(jsonCoursString);
        } catch (JSONException e) {
            mCourseJsonArray = new JSONArray();
        }


        for (int i=0;i<mCourseJsonArray.length();i++) {
            try {
                mCourseName.add(((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_NAME));
                CourseInfo course = new CourseInfo();
                course.id = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_ID);
                course.name = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_NAME);
                mCourseList.add(course);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mCourseName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCourseSpinner = (Spinner)findViewById(R.id.spinner_event_course);
        mCourseSpinner.setAdapter(adapter);

        mDateText = (TextView) findViewById(R.id.text_event_date);
        mHourText = (TextView) findViewById(R.id.text_event_hour);
        mShareCheckBox = (CheckBox) findViewById(R.id.addevent_share_check);
        mContent = (EditText) findViewById(R.id.edit_event_content);
        mLoc = (EditText) findViewById(R.id.edit_event_loc);
        mEventType = (Spinner)findViewById(R.id.spinner_event_type);

        final DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                String formatStr = "%02d";
                mDateText.setText(String.valueOf(year) + "-" + String.format(formatStr, monthOfYear + 1) + "-" + String.format(formatStr, dayOfMonth));
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
            }
        };



        mDateText.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DatePickerDialog dialog =
                        new DatePickerDialog(mContext,
                                datepicker,
                                m_Calendar.get(Calendar.YEAR),
                                m_Calendar.get(Calendar.MONTH),
                                m_Calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });


        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker v, int h, int m){
                String formatStr = "%02d";
                mHourText.setText(String.valueOf(h) + " : " +String.format(formatStr, m));
                mHour = h;
                mMin = m;
            }
        };

        mHourText.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TimePickerDialog dialog;
                dialog = new TimePickerDialog(mContext,
                        timeSetListener,
                        12,0,true);
                dialog.show();
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_event_done) {
            if(mYear==9999 || mHour==99){
                Toast.makeText(this, R.string.event_no_time, Toast.LENGTH_SHORT).show();
                return true;
            }
            if(mContent.length() == 0){
                Toast.makeText(this, R.string.event_content_empty, Toast.LENGTH_SHORT).show();
                return true;
            }


            eventCalendar.set(mYear,mMonth,mDay,mHour,mMin);
            Calendar nowCalendar = Calendar.getInstance();
            if(eventCalendar.getTimeInMillis() < nowCalendar.getTimeInMillis() ){
                Toast.makeText(this, R.string.event_time_expired, Toast.LENGTH_SHORT).show();
                return true;
            }
            EventInfo eventItem = new EventInfo();
            eventItem.content = mContent.getText().toString();
            eventItem.loc = mLoc.getText().toString();
            eventItem.time = (long)(eventCalendar.getTimeInMillis()/1000);
            eventItem.type = mEventType.getSelectedItemPosition();
            eventItem.courseid = mCourseList.get(mCourseSpinner.getSelectedItemPosition()).id;
            eventItem.name = mCourseList.get(mCourseSpinner.getSelectedItemPosition()).name;

            JSONObject scheduleItem = new JSONObject();
            try {
                scheduleItem.put(NETTag.MY_SCHEDULE_NAME ,eventItem.name);
                scheduleItem.put(NETTag.MY_SCHEDULE_CONTENT ,eventItem.content);
                scheduleItem.put(NETTag.MY_SCHEDULE_TIME ,eventItem.time);
                scheduleItem.put(NETTag.MY_SCHEDULE_LOC ,eventItem.loc);
                scheduleItem.put(NETTag.MY_SCHEDULE_TYPE ,eventItem.type);
                scheduleItem.put(NETTag.MY_SCHEDULE_COURSEID ,eventItem.courseid);
                scheduleItem.put(NETTag.MY_SCHEDULE_NAME ,eventItem.name);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String scheduleString = scheduleItem.toString();
            String postString = getAfterString(eventItem);
            String preString = getPreviousString();
            int share = 0;
            if(mShareCheckBox.isChecked()){
                share = 1;
            }
            postEvent(share,scheduleString,preString,postString);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.d("AddEventActivity", response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){

                    SharedPreferences settings =getSharedPreferences("ID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(NETTag.MY_SCHEDULE_EVENT, jsonObject.getString(NETTag.POST_COURSE_STRING));
                    editor.commit();
                    finish();


                }
                else{

                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {

                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            } finally {

            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("AddEventActivity", error.getMessage(), error);
            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    public void postEvent(int share, String scheduleString , String prestring, String poststring) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(NETTag.MY_SCHEDULE_EVENT, scheduleString);
            jsonObject.put(NETTag.PRE_COURSE_STRING, prestring);
            jsonObject.put(NETTag.POST_COURSE_STRING, poststring);


            jsonObject.put(NETTag.MY_SCHEDULE_SHARE, share);
            Log.d("NewMessageActivity post string = ", jsonObject.toString());
            PostEventRequest stringRequest = new PostEventRequest(this, listener, errorListener, jsonObject.toString());
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }



    public String getAfterString(EventInfo item){

        JSONArray tmpEventJsonArray;
        JSONArray postEventJsonArray = new JSONArray();
        SharedPreferences settings =getSharedPreferences("ID", Context.MODE_PRIVATE);
        String jsonEventString = settings.getString(NETTag.MY_SCHEDULE_EVENT, "[]");

        try {
            tmpEventJsonArray = new JSONArray(jsonEventString);
        } catch (JSONException e) {
            tmpEventJsonArray = new JSONArray();
        }

        JSONObject eventItem = new JSONObject();
        try {
            eventItem.put(NETTag.MY_SCHEDULE_NAME ,item.name);
            eventItem.put(NETTag.MY_SCHEDULE_CONTENT ,item.content);
            eventItem.put(NETTag.MY_SCHEDULE_TIME ,item.time);
            eventItem.put(NETTag.MY_SCHEDULE_LOC ,item.loc);
            eventItem.put(NETTag.MY_SCHEDULE_TYPE ,item.type);
            eventItem.put(NETTag.MY_SCHEDULE_COURSEID ,item.courseid);
            eventItem.put(NETTag.MY_SCHEDULE_NAME ,item.name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tmpEventJsonArray.put(eventItem);
        Calendar nowCalendar = Calendar.getInstance();
        for (int i=0;i<tmpEventJsonArray.length();i++){
            try {
                if(tmpEventJsonArray.getJSONObject(i).getLong(NETTag.MY_SCHEDULE_TIME) > (long)(nowCalendar.getTimeInMillis()/1000)){
                    postEventJsonArray.put(tmpEventJsonArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return postEventJsonArray.toString();
    }

    public String getPreviousString(){
        SharedPreferences settings =getSharedPreferences("ID", Context.MODE_PRIVATE);
        String jsonEventString = settings.getString(NETTag.MY_SCHEDULE_EVENT, "[]");
        return jsonEventString;
    }
}
