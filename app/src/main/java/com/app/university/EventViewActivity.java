package com.app.university;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class EventViewActivity extends Activity {
    private JSONArray mScheduleJsonArray;
    private List<EventInfo> mEventList = new ArrayList<EventInfo>();
    private ListView mEventListView;
    private MyEventAdapter mAdapter;
    private Context mContext;

    class EventInfo{
        String name = "";
        String courseid = "";
        String content = "";
        long time = 0;
        String loc = "";
        int type = 0;
    }


    private class MyEventAdapter extends BaseAdapter {
        private String TAG = "MyCourseAdapter";

        private Context context;
        private LayoutInflater inflater;

        public MyEventAdapter(Context context) {
            this.context = context;

        }



        @Override
        public int getCount() {
            return mEventList.size();
        }

        @Override
        public Object getItem(int location) {
            return mEventList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.schedule_event_item, null);
            }


            TextView txName = (TextView) convertView.findViewById(R.id.event_name);
            TextView txLoc = (TextView) convertView.findViewById(R.id.event_loc);
            TextView txtime = (TextView) convertView.findViewById(R.id.event_time);
            TextView txcontent = (TextView) convertView.findViewById(R.id.event_content);
            TextView txType = (TextView) convertView.findViewById(R.id.event_type);

            // getting movie data for the row

            switch(mEventList.get(position).type){
                case 0:
                    txType.setBackground(getResources().getDrawable(R.drawable.schedule_type_homework));
                    txType.setText(R.string.homework);

                    break;
                case 1:
                    txType.setBackground(getResources().getDrawable(R.drawable.schedule_type_exam));
                    txType.setText(R.string.exam);
                    break;
                case 2:
                    txType.setBackground(getResources().getDrawable(R.drawable.schedule_type_others));
                    txType.setText(R.string.other);
                    break;
                default:
                    txType.setBackground(getResources().getDrawable(R.drawable.schedule_type_others));
                    txType.setText(R.string.other);
            }

            String courseName = mEventList.get(position).name;
            int englishIndex = courseName.indexOf("-");
            if(englishIndex != -1){
                courseName = courseName.substring(0,courseName.indexOf("-"));
            }
            txName.setText(courseName);
            txLoc.setText(mEventList.get(position).loc);
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(mEventList.get(position).time*1000);
            Date dt = date.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");

            txtime.setText(sdf.format(dt));
            txcontent.setText(mEventList.get(position).content);

            LinearLayout itemLayout = (LinearLayout)convertView.findViewById(R.id.schedule_item);

            itemLayout.setOnLongClickListener(new ItemButton_LongClick(position));

            return convertView;
        }
    }

    class ItemButton_LongClick implements View.OnLongClickListener{
        private int mposition;

        ItemButton_LongClick(int pos) {
            mposition = pos;

        }
        public boolean  onLongClick(View v) {
            final CharSequence courseOption[] = { getString( R.string.delete) };

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle(getString(R.string.delete));
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.setSingleChoiceItems(courseOption, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == 0) {
                        String postCourseString = "";
                        for (int i = 0; i < mScheduleJsonArray.length(); i++) {
                            try {
                                if (((JSONObject) (mScheduleJsonArray.get(i))).getString(NETTag.MY_SCHEDULE_NAME).compareTo(mEventList.get(mposition).name) == 0 &&
                                        ((JSONObject) (mScheduleJsonArray.get(i))).getString(NETTag.MY_SCHEDULE_COURSEID).compareTo(mEventList.get(mposition).courseid) == 0 &&
                                        ((JSONObject) (mScheduleJsonArray.get(i))).getLong(NETTag.MY_SCHEDULE_TIME)== mEventList.get(mposition).time &&
                                        ((JSONObject) (mScheduleJsonArray.get(i))).getString(NETTag.MY_SCHEDULE_CONTENT).compareTo(mEventList.get(mposition).content) == 0) {


                                    JSONArray postCourseArray = new JSONArray();
                                    try {
                                        postCourseArray = new JSONArray(mScheduleJsonArray.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    postCourseArray = CommonUtil.RemoveJSONArray(postCourseArray, i);
                                    //postCourseArray.remove(i);
                                    postCourseString = postCourseArray.toString();



                                }

                            } catch (JSONException e) {
                                dialog.cancel();
                                e.printStackTrace();
                            }
                        }
                        userData udata = new userData("","","","","","","");
                        udata.myEvent = postCourseString;
                        UpdateAccountRequest stringRequest = new UpdateAccountRequest(mContext, Longlistener, errorLongListener, udata);
                        MySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(stringRequest);
                        dialog.cancel();

                    } else {
                        dialog.cancel();
                    }
                }
            });
            alert.show();
            return true;
        }
    }

    Response.ErrorListener errorLongListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("EventViewActivity", error.getMessage(), error);
            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    Response.Listener<String> Longlistener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.d("EventViewActivity", response);
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    JSONObject userJsonObject = new JSONObject(jsonObject.getString(NETTag.USER));
                    String jsonSTringSchedule = "";
                    if(userJsonObject.has(NETTag.MY_SCHEDULE_EVENT)){
                        jsonSTringSchedule = userJsonObject.getString(NETTag.MY_SCHEDULE_EVENT);
                        try {
                            mScheduleJsonArray = new JSONArray(jsonSTringSchedule);
                            SharedPreferences settings = mContext.getSharedPreferences ("ID", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(NETTag.MY_SCHEDULE_EVENT, jsonSTringSchedule);
                            editor.commit();
                            getEventList();
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    else{
                        Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        Tracker t = ((UniversityApp) getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("View schedule event");
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        mContext = this;
        getEventList();
        mAdapter = new MyEventAdapter(this);
        mEventListView = (ListView) findViewById(R.id.my_event_list);
        mEventListView.setAdapter(mAdapter);


    }

    private void getEventList(){
        SharedPreferences settings =getSharedPreferences("ID", Context.MODE_PRIVATE);
        String jsonEventString = settings.getString(NETTag.MY_SCHEDULE_EVENT, "[]");
        try {
            mScheduleJsonArray = new JSONArray(jsonEventString);
        } catch (JSONException e) {
            mScheduleJsonArray = new JSONArray();
        }
        mEventList.clear();
        for (int i=0;i<mScheduleJsonArray.length();i++) {
            try {
                EventInfo info = new EventInfo();
                info.name = ((JSONObject)mScheduleJsonArray.get(i)).getString(NETTag.MY_SCHEDULE_NAME);
                info.courseid = ((JSONObject)mScheduleJsonArray.get(i)).getString(NETTag.MY_SCHEDULE_COURSEID);
                info.content = ((JSONObject)mScheduleJsonArray.get(i)).getString(NETTag.MY_SCHEDULE_CONTENT);
                info.time = ((JSONObject)mScheduleJsonArray.get(i)).getLong(NETTag.MY_SCHEDULE_TIME);
                info.loc = ((JSONObject)mScheduleJsonArray.get(i)).getString(NETTag.MY_SCHEDULE_LOC);
                info.type = ((JSONObject)mScheduleJsonArray.get(i)).getInt(NETTag.MY_SCHEDULE_TYPE);
                Calendar date = Calendar.getInstance();
                Calendar nowdate = Calendar.getInstance();
                date.setTimeInMillis(info.time*1000);
                if(date.after(nowdate)){
                    mEventList.add(info);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(mEventList,
                new Comparator<EventInfo>() {
                    public int compare(EventInfo o1, EventInfo o2) {
                        return (int)(o1.time-o2.time);
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_view, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getEventList();
        mAdapter.notifyDataSetChanged();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_new_event) {

            Bundle bundle = new Bundle();
            //bundle.putString(Data.GROUP_ID, mGroupID);
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
