package com.app.university;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.university.view.SwipeRefreshAndLoadLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 2015/2/6.
 */
public class CourseList extends Fragment implements SwipeRefreshAndLoadLayout.OnRefreshListener {


    private SwipeRefreshAndLoadLayout mSwipeLayout;
    private MyCourseAdapter mAdapter;
    private List<CourseItem> courseList;

    protected ListView mListView;

    public class CourseItem {
        private String name = "";
        private String teacher = "";
        private String time = "";
        private String loc = "";
        private String id = "";
        private String stunum = "";


        public CourseItem(String id, String name, String time, String loc, String stunum) {
            this.name = name;

            this.time = time;
            this.loc = loc;
            this.id = id;
            this.stunum = stunum;

        }

        public String getName() { return name;}
        public String stunum() { return stunum; }
        public String getTime() { return time; }

        public String getLoc() { return loc;}
        public String getId() { return id;}


    }

    private class MyCourseAdapter extends BaseAdapter {
        private String TAG = "MyCourseAdapter";

        private Context context;
        private LayoutInflater inflater;
        private List<CourseItem> courseList;



        public MyCourseAdapter(Context context, List<CourseItem> courseList) {
            this.context = context;
            this.courseList = courseList;
        }



        @Override
        public int getCount() {
            return courseList.size();
        }

        @Override
        public Object getItem(int location) {
            return courseList.get(location);
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
                convertView = inflater.inflate(R.layout.my_course_item, null);
            }
            final View mview = convertView;

            TextView txName = (TextView) convertView.findViewById(R.id.course_name);
            TextView txLoc = (TextView) convertView.findViewById(R.id.course_loc);

            // getting movie data for the row
            CourseItem m = courseList.get(position);
            String courseName = m.getName();
            int englishIndex = courseName.indexOf("-");
            if(englishIndex != -1){
                courseName = courseName.substring(0,courseName.indexOf("-"));
            }
            txName.setText(courseName);
            if(m.loc.compareTo("") == 0 || m.loc.compareTo(" ") == 0){
                txLoc.setVisibility(View.GONE);
            }
            else{
                txLoc.setText(m.loc);
            }


            LinearLayout itemLayout = (LinearLayout)convertView.findViewById(R.id.my_course_item);
            itemLayout.setOnClickListener(new ItemButton_Click(position));
            itemLayout.setOnLongClickListener(new ItemButton_LongClick(position));


            return convertView;
        }
    }


    class ItemButton_Click implements View.OnClickListener {
        private int mposition;

        ItemButton_Click(int pos) {
            mposition = pos;

        }
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString(Data.GROUP_ID, courseList.get(mposition).getId());
            bundle.putInt(Data.GROUP_TYPE, Data.GROUP_TYPE_COURSE);
            Intent intent = new Intent(getActivity(), MessageActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }
    }


    Response.ErrorListener errorLongListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("AddCourseActivity", error.getMessage(), error);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    Response.Listener<String> Longlistener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    String schedule = jsonObject.getString(NETTag.POST_COURSE_STRING);
                    SharedPreferences settings = getActivity().getSharedPreferences ("ID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Data.CURRENTCOURSE, schedule);
                    editor.commit();
                    mSwipeLayout.setRefreshing(true);
                    GetMyCourseRequest stringRequest = new GetMyCourseRequest(getActivity(), listener, errorListener);
                    MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);

                }
                else{
                    Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        }
    };

    class ItemButton_LongClick implements View.OnLongClickListener{
        private int mposition;

        ItemButton_LongClick(int pos) {
            mposition = pos;

        }
        public boolean  onLongClick(View v) {
            final CharSequence courseOption[] = { getString( R.string.course_delete) };

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(getString(R.string.course_action));
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.setSingleChoiceItems(courseOption, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
                    String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
                    JSONArray mCourseJsonArray;
                    try {
                        mCourseJsonArray = new JSONArray(jsonCoursString);
                    } catch (JSONException e) {
                        mCourseJsonArray = new JSONArray();
                    }
                    if(which == 0){
                        for (int i=0;i<mCourseJsonArray.length();i++){
                            try {
                                if( ((JSONObject)(mCourseJsonArray.get(i))).getString(Data.COURSE_ID).compareTo(courseList.get(mposition).getId()) == 0){

                                    String preCourseString = mCourseJsonArray.toString();
                                    JSONArray postCourseArray  = new JSONArray();
                                    try {
                                        postCourseArray = new JSONArray(mCourseJsonArray.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    postCourseArray = CommonUtil.RemoveJSONArray(postCourseArray,i);
                                    //postCourseArray.remove(i);
                                    String postCourseString = postCourseArray.toString();

                                    AddCourseRequest stringRequest = new AddCourseRequest(getActivity(), NETTag.API_DELETE_COURSE, courseList.get(mposition).getId(), Longlistener, errorLongListener,preCourseString,postCourseString,"");
                                    MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
                                    dialog.cancel();
                                    return;
                                }

                            } catch (JSONException e) {
                                dialog.cancel();
                                e.printStackTrace();
                            }
                        }
                        AddCourseRequest stringRequest = new AddCourseRequest(getActivity(), NETTag.API_DELETE_COURSE, courseList.get(mposition).getId(), Longlistener, errorLongListener,jsonCoursString,jsonCoursString,"");
                        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
                        dialog.cancel();

                    }else {
                        dialog.cancel();
                    }
                }
            });
            alert.show();
            return true;
        }
    }



    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    SharedPreferences settings= null;
                    SharedPreferences.Editor editor = null;

                    JSONArray jsonCourseList= new JSONArray(jsonObject.getString(NETTag.MY_COURSE_LIST));
                    courseList.clear();
                    if(getActivity() != null && jsonCourseList.length() != 0) {
                        settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
                        editor = settings.edit();
                        // this tag only used for network fail mode. Don't use this tag other place
                        editor.putString(Data.CURRENTCOURSELIST, response);
                        editor.commit();
                    }
                    for (int i = 0; i < jsonCourseList.length(); i++) {
                        JSONObject jsonCourseItem = jsonCourseList.optJSONObject(i);
                        if (jsonCourseItem == null) continue;

                        CourseItem courseItem = new CourseItem(
                                jsonCourseItem.getString(NETTag.COURSE_ID),
                                jsonCourseItem.getString(NETTag.COURSE_NAME),
                                jsonCourseItem.getString(NETTag.COURSE_REALTIME),
                                jsonCourseItem.getString(NETTag.COURSE_LOCATION),
                                jsonCourseItem.getString(NETTag.COURSE_STUDENT_NUMBER));
                        if(editor != null){
                            editor.putString(jsonCourseItem.getString(NETTag.COURSE_ID),jsonCourseItem.getString(NETTag.COURSE_LOCATION));
                            editor.commit();
                        }
                        courseList.add(courseItem);
                    }
                    mSwipeLayout.setRefreshing(false);
                }
                else{
                    mSwipeLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                //mSwipeLayout.setRefreshing(false);
                //Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            } finally {
                mSwipeLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            mSwipeLayout.setRefreshing(false);
            if(getActivity() != null) {
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
                // this tag only used for network fail mode. Don't use this tag other place
                String response = settings.getString(Data.CURRENTCOURSELIST, "[]");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray jsonCourseList = new JSONArray(jsonObject.getString(NETTag.MY_COURSE_LIST));
                    courseList.clear();
                    for (int i = 0; i < jsonCourseList.length(); i++) {
                        JSONObject jsonCourseItem = jsonCourseList.optJSONObject(i);
                        if (jsonCourseItem == null) continue;

                        CourseItem courseItem = new CourseItem(
                                jsonCourseItem.getString(NETTag.COURSE_ID),
                                jsonCourseItem.getString(NETTag.COURSE_NAME),
                                jsonCourseItem.getString(NETTag.COURSE_REALTIME),
                                jsonCourseItem.getString(NETTag.COURSE_LOCATION),
                                jsonCourseItem.getString(NETTag.COURSE_STUDENT_NUMBER));
                        courseList.add(courseItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
            mSwipeLayout.setRefreshing(false);

            Log.e("CourseList", error.getMessage(), error);

            return;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CourseList  = ", "onResume");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("CourseList  = ", "setUserVisibleHint");
        if(mSwipeLayout != null){
            mSwipeLayout.setRefreshing(true);
            GetMyCourseRequest stringRequest = new GetMyCourseRequest(getActivity(), listener, errorListener);
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
        }


    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
        Log.d("CourseList  = ", "onRefresh");
        return;
    }
    @Override
    public void onLoadMore() {
        mSwipeLayout.setRefreshing(false);
        Log.d("CourseList  = ", "onLoadMore");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.my_course_list, container, false);
        courseList = new ArrayList<CourseItem>();
        mAdapter = new MyCourseAdapter(getActivity(), courseList);
        mListView = (ListView) view.findViewById(R.id.my_course_list);
        mListView.setAdapter(mAdapter);

        Tracker t = ((UniversityApp)getActivity().getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("View Course List");
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        mSwipeLayout = (com.app.university.view.SwipeRefreshAndLoadLayout) view.findViewById(R.id.my_course_list_swipe);
        mSwipeLayout.setOnRefreshListener(CourseList.this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setmMode(SwipeRefreshAndLoadLayout.Mode.DISABLED);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.d("CourseList  = ", "scroll");
                return;
            }

        });

        mSwipeLayout.setRefreshing(true);
        GetMyCourseRequest stringRequest = new GetMyCourseRequest(getActivity(), listener, errorListener);
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
        return view;
    }
}