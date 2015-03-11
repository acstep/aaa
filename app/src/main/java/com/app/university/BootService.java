package com.app.university;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by matt on 2015/3/10.
 */
public class BootService extends Service {
    private Timer timer;
    private TimerTask task;
    private List<String> weekString;
    private static JSONArray mCourseJsonArray;
    public static final int NOTIFICATION_ID = 2;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private  int number = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BootService = ", "startTimer");

        weekString = new ArrayList<String>();
        weekString.add("SUN");
        weekString.add("MON");
        weekString.add("TUE");
        weekString.add("WED");
        weekString.add("THU");
        weekString.add("FRI");
        weekString.add("SAT");
        startTimer();
    }

    public BootService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void startTimer() {
        if (timer == null && task == null) {

            timer = new Timer();
            task = new TimerTask() {

                @Override
                public void run() {
                    number = number + 1;
                    if(number%4 == 0){
                        Tracker t = ((UniversityApp) getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
                        // Set screen name.
                        // Where path is a String representing the screen name.
                        t.setScreenName("check schedule service");
                        // Send a screen view.
                        t.send(new HitBuilders.AppViewBuilder().build());
                    }


                    Log.d("BootService = ", "service update!");
                    Date date = new Date();
                    Calendar calendar = GregorianCalendar.getInstance();

                    int currentDay = calendar.get(Calendar.DAY_OF_WEEK)-1 ;
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY) ;
                    int currentMin = calendar.get(Calendar.MINUTE) ;

                    SharedPreferences settings = getSharedPreferences("ID", Context.MODE_PRIVATE);
                    String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
                    try {
                        mCourseJsonArray = new JSONArray(jsonCoursString);
                    } catch (JSONException e) {
                        mCourseJsonArray = new JSONArray();
                    }
                    for (int i = 0; i < mCourseJsonArray.length(); i++) {
                        String courseTimeString = "";
                        try {
                            courseTimeString = ((JSONObject) mCourseJsonArray.get(i)).getString(Data.COURSE_TIME);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Integer index = 0;
                        while (index + 15 <= courseTimeString.length()) {
                            String timeString =courseTimeString.substring(index, index + 15);
                            if(timeString.substring(0, 3).compareTo(weekString.get(currentDay)) == 0){
                                int startMin = Integer.valueOf(timeString.substring(7, 9));
                                int startHR = Integer.valueOf(timeString.substring(4, 6));
                                if((startHR*60 + startMin - currentHour*60 - currentMin) <120 && (startHR*60 + startMin - currentHour*60 - currentMin)> 0){
                                    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                                    if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
                                        if(settings.getInt(Data.VOLUME_AUTO_OFF, 0) == 1){
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                            sendNotification();
                                        }

                                    }

                                }
                            }



                            index = index + 16;
                        }

                    }


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

    private void sendNotification() {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intentNewsViewer = new Intent(this, MainActivity.class);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent contentIntent = PendingIntent.getActivity(this, iUniqueId, intentNewsViewer, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.logon);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(getResources().getString(R.string.volume_auto_off_in_class_title))

                        .setLargeIcon(largeIcon)
                        .setContentText(getResources().getString(R.string.volume_auto_off_in_class_content))
                        .setAutoCancel(true);


        SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
        int type = settings.getInt(Data.NOTIFICATION_TYPE,0);
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
