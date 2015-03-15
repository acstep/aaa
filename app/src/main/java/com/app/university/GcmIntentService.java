package com.app.university;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by matt on 2015/2/24.
 */
public class GcmIntentService extends IntentService {


    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {



        Bundle extras = intent.getExtras();
        if(extras == null){
            return;
        }
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of un-parcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(
                        extras.getString(NETTag.NOTIFICATION_TITLE),
                        extras.getString(NETTag.NOTIFICATION_CONTENT)
                );

            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String content) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intentNewsViewer = new Intent(this, MainActivity.class);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent contentIntent = PendingIntent.getActivity(this, iUniqueId, intentNewsViewer, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logon);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setLargeIcon(largeIcon)
                        .setContentText(content)
                        .setAutoCancel(true);


        SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
        int type = settings.getInt(Data.NOTIFICATION_TYPE,0);
        if(type == 0){
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        }
        else if(type == 1){
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS);
        }
        else{
            mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS);
        }


        SharedPreferences.Editor editor = settings.edit();
        int notifyNum = settings.getInt(Data.NOTIFICATION_NUM,0);
        notifyNum = notifyNum + 1;
        editor.putInt(Data.NOTIFICATION_NUM,notifyNum);
        editor.commit();

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}
