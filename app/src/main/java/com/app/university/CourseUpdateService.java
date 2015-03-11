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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
    private static int mCurrentDayofWeek = -1;
    private  int number = 0;

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
                    number = number + 1;
                    if(number%4 == 0) {
                        Tracker t = ((UniversityApp) getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
                        // Set screen name.
                        // Where path is a String representing the screen name.
                        t.setScreenName("course widget service");
                        // Send a screen view.
                        t.send(new HitBuilders.AppViewBuilder().build());
                    }

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
                    //checkCourse();
                    //if(inCourse()){
                    //    Intent i = new Intent();
                    //    i.setClass(getApplicationContext(), MobileSilenceActivity.class);
                    //    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //    startActivity(i);
                    //}

                    CourseInfo nextCourse = getNextCourse();
                    if(nextCourse != null){
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


    public int checkCourse(){
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_WEEK)-1 ;
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY) ;
        int currentMin = calendar.get(Calendar.MINUTE) ;
        int currentDayMin = currentHour*60 + currentMin;
        List<String> weekSTring = new ArrayList<String>();
        weekSTring.add("SUN");
        weekSTring.add("MON");
        weekSTring.add("TUE");
        weekSTring.add("WED");
        weekSTring.add("THU");
        weekSTring.add("FRI");
        weekSTring.add("SAT");

        for (int i = 0; i < courseList.size(); i++) {
            String time = courseList.get(i).timeString;
            String day = time.substring(0,3);
            int startHR = Integer.valueOf(time.substring(4, 6));
            int startMin = Integer.valueOf(time.substring(7, 9));
            int endHR = Integer.valueOf(time.substring(10, 12));
            int endMin = Integer.valueOf(time.substring(13, 15));
            if(day.compareTo(weekSTring.get(currentDay)) == 0){
                int courseStartMin = startHR*60 + startMin;
                int courseEndMin = endHR*60 + endMin;
                if(courseStartMin-1 == currentDayMin){
                    Log.d("CourseUpdateService = ", "before course one min!");
                    return 0;
                }
                else if(courseEndMin+1 == currentDayMin){
                    Log.d("CourseUpdateService = ", "after course one min!");
                    return 1;
                }
            }

        }
        return 2;
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
        if(smallDistance == 500){
            return null;
        }
        else{
            return courseList.get(index);
        }

    }

    public static void parseCourse() {
        try {

            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance();

            int currentDay = calendar.get(Calendar.DAY_OF_WEEK)-1 ;
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY) ;
            List<String> weekSTring = new ArrayList<String>();
            weekSTring.add("SUN");
            weekSTring.add("MON");
            weekSTring.add("TUE");
            weekSTring.add("WED");
            weekSTring.add("THU");
            weekSTring.add("FRI");
            weekSTring.add("SAT");


            courseList.clear();
            mCurrentDayofWeek = currentDay;



            for (int i = 0; i < mCourseJsonArray.length(); i++) {
                String courseTimeString = null;

                courseTimeString = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_TIME);
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

                    int startMin = Integer.valueOf(courseInfo.timeString.substring(7, 9));
                    int currentMin = calendar.get(Calendar.MINUTE) ;

                    int courseHR = Integer.valueOf(courseInfo.timeString.substring(4, 6));
                    if(dayDistance == 0){
                        if((courseHR - currentHour)<=0 && startMin < currentMin){
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