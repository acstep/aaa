package com.app.university;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class MainActivity extends FragmentActivity {

    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private GoogleCloudMessaging gcm;

    private Schedule mSchedule;
    private CourseList mCourseList;
    private GroupList mGroupList;
    private Aboutme mAboutMe;
    private DisplayMetrics dm;
    private PagerSlidingTabStrip tabs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);


        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
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

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
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
            return titles.length;
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
