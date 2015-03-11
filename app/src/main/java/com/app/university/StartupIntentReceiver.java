package com.app.university;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by matt on 2015/3/10.
 */
public class StartupIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BootService.class);
        context.startService(serviceIntent);
    }
}
