package com.app.university;

import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends FragmentActivity {

    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private GoogleCloudMessaging gcm;
    private RequestQueue mQueue = null;

    private Schedule mSchedule;
    private CourseList mCourseList;
    private GroupList mGroupList;
    private Aboutme mAboutMe;
    private DisplayMetrics dm;
    private PagerSlidingTabStrip tabs;
    public static final String GCM_SENDER_ID = "670661322538";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String PREFS_NAME = "MyPrefsFile";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "MainActivity";



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
        mQueue.add(stringRequest);
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
        mQueue = Volley.newRequestQueue(this);
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

            //if (regId.isEmpty()) {
            registerInBackground();
            //}
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);


        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));

        View[] Tabs = new View[5];

        Tabs[0] = getLayoutInflater().inflate(R.layout.tab_layout, null);
        Tabs[1] = getLayoutInflater().inflate(R.layout.tab_layout, null);
        Tabs[2] = getLayoutInflater().inflate(R.layout.tab_layout, null);
        Tabs[3] = getLayoutInflater().inflate(R.layout.tab_layout, null);
        Tabs[4] = getLayoutInflater().inflate(R.layout.tab_layout, null);


        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),Tabs));
        tabs.setViewPager(pager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                    mCourseList = new CourseList();

                    return mCourseList;
                case 4:

                    mAboutMe = new Aboutme();

                    return mAboutMe;
                default:
                    return null;
            }
        }

    }
}
