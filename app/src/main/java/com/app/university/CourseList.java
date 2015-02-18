package com.app.university;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.app.university.view.SwipeRefreshAndLoadLayout;

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
    private RequestQueue mQueue = null;
    protected ListView mListView;

    public class CourseItem {
        private String name;
        private String teacher;
        private String time;
        private String loc;
        private String id;
        private String stunum;


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


            // getting movie data for the row
            CourseItem m = courseList.get(position);
            String courseName = m.getName();
            int englishIndex = courseName.indexOf("-");
            if(englishIndex != -1){
                courseName = courseName.substring(0,courseName.indexOf("-"));
            }
            txName.setText(courseName);



            return convertView;
        }
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    JSONArray jsonCourseList= new JSONArray(jsonObject.getString(NETTag.MY_COURSE_LIST));
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
                    mSwipeLayout.setRefreshing(false);
                }
                else{
                    mSwipeLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                mSwipeLayout.setRefreshing(false);
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
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
            Log.e("CourseList", error.getMessage(), error);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

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
        mQueue = Volley.newRequestQueue(getActivity());

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
        mQueue.add(stringRequest);
        return view;
    }
}