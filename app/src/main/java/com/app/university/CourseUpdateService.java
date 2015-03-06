package com.app.university;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CourseUpdateService extends Service {

    private Timer timer;
    private TimerTask task;
    private AppWidgetManager awm;

    private static JSONArray mCourseJsonArray;
    private static List<CourseInfo> courseList = new ArrayList<CourseInfo>();

    static class CourseInfo{
        String id = "";
        String name = "";
        String teacher = "";
        String color = "";
        String timeString = "";
        String time = "";
        String loc = "";
        String courseBlockTime = "";
        int    distance = 0;
    }

    public CourseUpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CourseUpdateService = ", "start timer!");
        startTimer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startTimer() {
        if (timer == null && task == null) {
            awm = AppWidgetManager.getInstance(this);
            timer = new Timer();
            task = new TimerTask() {

                @Override
                public void run() {
                    Log.d("CourseUpdateService = ", "service update!");
                    ComponentName provider = new ComponentName(
                            CourseUpdateService.this, NextCourseWidget.class);
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.next_course_widget);

                    SharedPreferences settings = getSharedPreferences("ID", Context.MODE_PRIVATE);
                    String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
                    try {
                        mCourseJsonArray = new JSONArray(jsonCoursString);
                    } catch (JSONException e) {
                        mCourseJsonArray = new JSONArray();
                    }

                    parseCourse();

                    CourseInfo nextCourse = getNextCourse();
                    views.setTextViewText(R.id.next_course_name, nextCourse.name);
                    views.setTextViewText(R.id.next_course_time, nextCourse.timeString);
                    if(nextCourse.loc == ""){
                        if(settings.contains(nextCourse.id)){
                            views.setTextViewText(R.id.next_course_loc, settings.getString(nextCourse.id,""));
                        }
                    }
                    else{
                        views.setTextViewText(R.id.next_course_loc, nextCourse.loc);
                    }

                    Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);

                    PendingIntent configPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, configIntent, 0);

                    views.setOnClickPendingIntent(R.id.course_widget, configPendingIntent);
                    awm.updateAppWidget(provider, views);

                }
            };
            timer.schedule(task, 0, 60000);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();

    }

    private void stopTimer() {
        if (timer != null && task != null) {
            timer.cancel();
            task.cancel();
            task = null;
            timer = null;
        }




    }


    public static CourseInfo getNextCourse(){
        int index = 0;
        int smallDistance = 500;
        for (int i = 0; i < courseList.size(); i++) {
            if(courseList.get(i).distance <smallDistance ){
                index = i;
                smallDistance = courseList.get(i).distance;
            }
        }
        return courseList.get(index);
    }

    public static void parseCourse() {
        try {

            for (int i = 0; i < mCourseJsonArray.length(); i++) {
                String courseTimeString = null;

                courseTimeString = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_TIME);
                Date date = new Date();
                Calendar calendar = GregorianCalendar.getInstance();

                int currentDay = calendar.get(Calendar.DAY_OF_WEEK)-2 ;
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY) ;

                List<String> weekSTring = new ArrayList<String>();
                weekSTring.add("MON");
                weekSTring.add("TUE");
                weekSTring.add("WED");
                weekSTring.add("THU");
                weekSTring.add("FRI");
                weekSTring.add("SAT");
                weekSTring.add("SUN");


                HashMap<String, Integer> weekMap = new HashMap<String, Integer>();
                for(int j=0 ; j<7 ; j++){
                    weekMap.put(weekSTring.get((currentDay+j)%7), j*24);
                }




                Integer index = 0;
                while (index + 15 <= courseTimeString.length()) {
                    CourseInfo courseInfo = new CourseInfo();
                    courseInfo.id = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_ID);
                    courseInfo.name = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_NAME);
                    courseInfo.teacher = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_TEACHER);
                    courseInfo.color = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_COLOR);
                    courseInfo.timeString = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_TIME).substring(index, index + 15);


                    if(((JSONObject) mCourseJsonArray.get(i)).has(Data.COURSE_LOC)){
                        courseInfo.loc = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_LOC);
                    }

                    int dayDistance = weekMap.get(courseInfo.timeString.substring(0, 3));
                    int courseHR = Integer.valueOf(courseInfo.timeString.substring(4, 6));
                    if(dayDistance == 0){
                        if((courseHR - currentHour)<0){
                            courseInfo.distance = 24-currentHour + 6*24 + courseHR;
                        }
                        else{
                            courseInfo.distance = courseHR - currentHour;
                        }
                    }
                    else{
                        courseInfo.distance =  dayDistance - currentHour + courseHR;

                    }

                    courseList.add(courseInfo);
                    index = index + 16;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}