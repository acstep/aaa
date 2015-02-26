package com.app.university;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{

    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private GoogleCloudMessaging gcm;


    private Schedule mSchedule;
    private CourseList mCourseList;
    private GroupList mGroupList;
    private NotifyList mNotifyList;
    private Aboutme mAboutMe;
    private DisplayMetrics dm;
    private PagerSlidingTabStrip tabs;
    public static final String GCM_SENDER_ID = "670661322538";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String PREFS_NAME = "MyPrefsFile";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "MainActivity";
    private static MainActivityBroadcastReceiver mGCMReceiver;
    private View[] mTabs;

    private Map<Integer, Integer> tabicon = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> tabiconb = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> mtitleSTring = new HashMap<Integer, Integer>();


    public class MainActivityBroadcastReceiver extends BroadcastReceiver {

        public MainActivityBroadcastReceiver(Activity mainActivity) {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast toast = Toast.makeText(context, "hola", Toast.LENGTH_LONG);
            //toast.show();
            //abortBroadcast();
            //TextView mmm = (TextView)Tabs[0].findViewById(R.id.tab_text);
            //mmm.setText("bbb");
        }

    }

    @Override
    protected void onPause() {
        unregisterReceiver(mGCMReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mGCMReceiver = new MainActivityBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
        filter.setPriority(1);
        registerReceiver(mGCMReceiver, filter);
        super.onResume();
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Data.GCM_ID_NEED_UPDATE,false);
                    editor.commit();
                    Log.d("MainActivity", "GCM update finished");
                }
                else{

                    return;
                }
            } catch (JSONException e) {

                e.printStackTrace();
                return;
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("MainActivity", error.getMessage(), error);

            return;
        }
    };

    private void updateGCM(String gid){
        userData udata = new userData("", "","","","","",gid);
        UpdateAccountRequest stringRequest = new UpdateAccountRequest(this, listener, errorListener, udata);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... Params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    String regId = gcm.register(GCM_SENDER_ID);
                    SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                    String gcmid = settings.getString(Data.GCM_ID, "");
                    boolean gcmUpdate = settings.getBoolean(Data.GCM_ID_NEED_UPDATE, true);

                    if(gcmid.compareTo(regId) == 0 && gcmUpdate == false){
                        Log.d("MainActivity", "No need to update GCM");

                    }
                    else{
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(Data.GCM_ID_NEED_UPDATE,true);
                        editor.putString(Data.GCM_ID, regId);
                        editor.commit();
                        updateGCM(regId);
                    }

                    // send the registration ID to server over HTTP,


                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

            //if (regId.isEmpty()) {
            registerInBackground();
            //}
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        tabiconb.put(0,R.mipmap.ic_tableb );
        tabiconb.put(1,R.mipmap.ic_courseb );
        tabiconb.put(2,R.mipmap.ic_groupb );
        tabiconb.put(3,R.mipmap.ic_eventb );
        tabiconb.put(4,R.mipmap.ic_actionb );

        tabicon.put(0,R.mipmap.ic_table );
        tabicon.put(1,R.mipmap.ic_course );
        tabicon.put(2,R.mipmap.ic_group );
        tabicon.put(3,R.mipmap.ic_event );
        tabicon.put(4,R.mipmap.ic_action );

        mtitleSTring.put(0, R.string.title_class_table);
        mtitleSTring.put(1, R.string.title_course_list);
        mtitleSTring.put(2, R.string.title_group);
        mtitleSTring.put(3, R.string.title_event);
        mtitleSTring.put(4, R.string.title_about);

        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setOnPageChangeListener(this);


        tabs.setIndicatorColor(Color.parseColor("#eeeeee"));
        tabs.setBackgroundColor(Color.parseColor("#eeeeee"));

        mTabs = new View[5];

        for(int i=0 ; i<mTabs.length ; i++){
            mTabs[i] = getLayoutInflater().inflate(R.layout.tab_layout, null);
            ImageView image = (ImageView)mTabs[i].findViewById(R.id.tab_image);
            image.setImageResource(tabiconb.get(i));
        }
        ImageView image = (ImageView)mTabs[0].findViewById(R.id.tab_image);
        image.setImageResource(tabicon.get(0));


        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),mTabs));
        tabs.setViewPager(pager);

        getActionBar().setTitle(mtitleSTring.get(0));

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getActionBar().setTitle(mtitleSTring.get(position));
        for(int i=0 ; i<mTabs.length ;i++){
            if(i == position){
                ImageView image = (ImageView)mTabs[i].findViewById(R.id.tab_image);
                image.setImageResource(tabicon.get(i));
            }
            else{
                ImageView image = (ImageView)mTabs[i].findViewById(R.id.tab_image);
                image.setImageResource(tabiconb.get(i));
            }

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.ViewTabProvider  {
        private View[] TABS;

        public MyPagerAdapter(FragmentManager fm ,View[] tabs) {
            super(fm);
            TABS = tabs;
        }

        private final String[] titles = { getString(R.string.schedule),
                                          getString(R.string.course),
                                          getString(R.string.group),
                                          getString(R.string.message),
                                          getString(R.string.me)};





        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return TABS.length;
        }

        public View getPageView(int position) {
            return TABS[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    mSchedule = new Schedule();

                    return mSchedule;
                case 1:

                    mCourseList = new CourseList();

                    return mCourseList;
                case 2:

                    mGroupList = new GroupList();

                    return mGroupList;
                case 3:

                    mNotifyList = new NotifyList();

                    return mNotifyList;
                case 4:

                    mAboutMe = new Aboutme();

                    return mAboutMe;
                default:
                    return null;
            }
        }

    }
}
