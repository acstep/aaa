package com.app.university;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.university.view.SwipeRefreshAndLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddCourseActivity extends Activity implements SwipeRefreshAndLoadLayout.OnRefreshListener{

    private Context mContext;
    protected ListView mListView;
    private SwipeRefreshAndLoadLayout mSwipeLayout;
    private SearchCourseAdapter mAdapter;
    private ImageLoader.ImageCache mImageCache = null;
    private List<CourseItem> courseList;
    private boolean mFlagloading = false;
    private RequestQueue mQueue = null;
    private Integer startIndex = 0;
    private Integer number = 20;
    private Integer editTextNum = 0;
    private String currentSearchText = "";
    private JSONArray mCourseJsonArray;
    private boolean hasMoreData = true;
    private CourseColor courseColor;
    private boolean mModifySchedule = false;


    public class CourseItem {
        private String name;
        private String teacher;
        private String time;
        private String realtime;
        private String loc;
        private String id;
        private boolean done;

        public CourseItem(String name, String teacher, String time, String realtime, String loc, String id) {
            this.name = name;
            this.teacher = teacher;
            this.time = time;
            this.realtime = realtime;
            this.loc = loc;
            this.id = id;
            this.done = false;
        }

        public String getName() {
            return name;
        }
        public String getTeacher() {
            return teacher;
        }
        public String getTime() {return time;}
        public String getRealtime() {
            return realtime;
        }
        public String getLoc() {
            return loc;
        }
        public String getId() {
            return id;
        }
        public boolean getDone() {
            return done;
        }
        public void setDone(boolean press) {
            done = press;
        }

    }

    private class SearchCourseAdapter extends BaseAdapter {
        private String TAG = "SearchCourseAdapter";

        private Context context;
        private LayoutInflater inflater;
        private List<CourseItem> courseList;



        public SearchCourseAdapter(Context context, List<CourseItem> courseList) {
            this.context = context;
            this.courseList = courseList;
        }

        public Boolean isCourseExist(String id){
            for (int i=0;i<mCourseJsonArray.length();i++){
                try {
                    if(((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_ID).compareTo(id) == 0){
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
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
                convertView = inflater.inflate(R.layout.search_course, null);
            }


            TextView txName = (TextView) convertView.findViewById(R.id.course_name);
            TextView txTeacher = (TextView) convertView.findViewById(R.id.teacher_name);
            TextView txSectime = (TextView) convertView.findViewById(R.id.course_time);


            // getting movie data for the row
            CourseItem courseItem = courseList.get(position);
            txName.setText(courseItem.getName());
            txTeacher.setText(courseItem.getTeacher());
            txSectime.setText(courseItem.getTime());

            Button addCourseButton = (Button) convertView.findViewById(R.id.btn_add_course);
            if(isCourseExist(courseItem.getId())){
                courseItem.setDone(true);
            }

            if(courseItem.getDone() == true ){
                addCourseButton.setText(R.string.delete);
                addCourseButton.setBackgroundResource(R.drawable.btn_red);
            }
            else{
                addCourseButton.setText(R.string.add);
                addCourseButton.setBackgroundResource(R.drawable.btn_blue);
            }
            addCourseButton.setOnClickListener(new ItemButton_Click(addCourseButton, position));

            return convertView;
        }
    }

    class ItemButton_Click implements View.OnClickListener {
        private int mposition;
        private Button mbutton;


        ItemButton_Click(Button button, int pos) {
            mposition = pos;
            mbutton = button;
        }
        public void onClick(View v) {
            final CourseItem tmpcourseItem = courseList.get(mposition);
            final String preCourseString = mCourseJsonArray.toString();
            String postCourseString = "";
            JSONArray postCourseArray  = new JSONArray();
            try {
                postCourseArray = new JSONArray(mCourseJsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(tmpcourseItem.getDone() == false){
                if(mCourseJsonArray.length() > Data.MAX_JOIN_COURSE){
                    Toast.makeText(mContext, R.string.max_join_course, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                            if(tmpcourseItem.getDone() == true){

                                mbutton.setText(R.string.add);
                                mbutton.setBackgroundResource(R.drawable.btn_blue);
                                tmpcourseItem.setDone(false);
                            }
                            else{
                                mbutton.setText(R.string.delete);
                                mbutton.setBackgroundResource(R.drawable.btn_red);
                                tmpcourseItem.setDone(true);
                            }
                            String schedule = jsonObject.getString(NETTag.POST_COURSE_STRING);
                            SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(Data.CURRENTCOURSE, schedule);
                            editor.putBoolean(Data.MODIFY_DIRTY, true);
                            editor.commit();
                            courseColor = new CourseColor(schedule);
                            mCourseJsonArray = CommonUtil.getCourseJsonArray(mContext);
                            //Log.d("AddCourseActivity mCourseJsonArray add course = ", mCourseJsonArray.toString() );

                        }
                        else{

                            //Toast.makeText(mContext, R.string.login_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }
                }
            };


            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("AddCourseActivity", error.getMessage(), error);
                    SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                    String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
                    try {
                        mCourseJsonArray = new JSONArray(jsonCoursString);
                    } catch (JSONException e) {
                        mCourseJsonArray = new JSONArray();
                    }
                    Log.d("AddCourseActivity mCourseJsonArray = ", mCourseJsonArray.toString() );

                    courseColor = new CourseColor(jsonCoursString);
                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            };

            if(tmpcourseItem.getDone() == true){

                for (int i=0;i<mCourseJsonArray.length();i++){
                    try {
                        if(((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_ID).compareTo(tmpcourseItem.getId()) == 0){

                            postCourseArray.remove(i);
                            postCourseString = postCourseArray.toString();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            else{

                JSONObject courseItem = new JSONObject();

                try {
                    courseItem.put(Data.COURSE_NAME ,tmpcourseItem.getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    courseItem.put(Data.COURSE_TIME ,tmpcourseItem.getRealtime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    courseItem.put(Data.COURSE_TEACHER ,tmpcourseItem.getTeacher());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String color = courseColor.getNewColor();
                    if(color.length() == 0){
                        color = "6698ff";
                    }
                    courseItem.put(Data.COURSE_COLOR  ,color);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    courseItem.put(Data.COURSE_ID ,tmpcourseItem.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    courseItem.put(Data.COURSE_TIMESEC ,tmpcourseItem.getTime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                postCourseArray.put(courseItem);
                postCourseString = postCourseArray.toString();


            }

            String url = "";
            if(tmpcourseItem.getDone() == true){
                url = NETTag.API_DELETE_COURSE;

            }
            else{
                url = NETTag.API_ADD_COURSE;
            }
            AddCourseRequest stringRequest = new AddCourseRequest(mContext, url, tmpcourseItem.getId(), listener, errorListener,preCourseString,postCourseString, "");

            mQueue.add(stringRequest);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.setResult(Activity.RESULT_OK);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        mContext = getApplicationContext();
        courseList = new ArrayList<CourseItem>();
        mAdapter = new SearchCourseAdapter(this, courseList);
        mListView = (ListView) findViewById(R.id.search_course_list);
        mListView.setAdapter(mAdapter);
        mQueue = Volley.newRequestQueue(this);



        SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
        String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
        try {
            mCourseJsonArray = new JSONArray(jsonCoursString);
        } catch (JSONException e) {
            mCourseJsonArray = new JSONArray();
        }
        Log.d("AddCourseActivity mCourseJsonArray = ", mCourseJsonArray.toString() );

        courseColor = new CourseColor(jsonCoursString);

        mSwipeLayout = (com.app.university.view.SwipeRefreshAndLoadLayout) findViewById(R.id.search_course_list_swipe);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setmMode(SwipeRefreshAndLoadLayout.Mode.DISABLED);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Implement here
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.d("AddCourseActivity", "cccccc " + mFlagloading +  "1" + hasMoreData + "2" + firstVisibleItem + visibleItemCount);
                if (firstVisibleItem + visibleItemCount > totalItemCount -3
                        && totalItemCount > 0
                        && mFlagloading == false
                        && hasMoreData == true) {
                    mFlagloading = true;
                    EditText edittext = (EditText) findViewById(R.id.edit_search);

                    Log.d("AddCourseActivity", "bbbbbb " );
                    mSwipeLayout.setRefreshing(true);
                    searchCourse(edittext.getText().toString(), false);


                    //getNews(mRequestNextUrl, false);
                }
            }
        });


        Button doneButton = (Button)findViewById(R.id.btn_create_course_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Data.MODIFY_DIRTY,true);
                editor.commit();

                AddCourseActivity.this.setResult(Activity.RESULT_OK);
                AddCourseActivity.this.finish();
            }
        });

        Button clearButton = (Button)findViewById(R.id.btn_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText editSearch = (EditText)findViewById(R.id.edit_search);
                editSearch.setText("");
                editTextNum = 0;
            }
        });

        final EditText edittext = (EditText) findViewById(R.id.edit_search);
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(currentSearchText.compareTo(edittext.getText().toString()) != 0 && edittext.getText().toString().compareTo("") != 0){
                    startIndex = 0;
                    mFlagloading = true;
                    hasMoreData = true;
                    currentSearchText = edittext.getText().toString();
                    Log.d("AddCourseActivity", "aaaaaa " );
                    searchCourse(edittext.getText().toString(), true);
                }
                else{
                    mFlagloading = false;
                }

            }

        });

    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
        return;
    }
    @Override
    public void onLoadMore() {

    }

    private void searchCourse(final String courseName, final boolean bClear) {

        if (bClear) {
            mSwipeLayout.setRefreshing(true);
        }
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, NETTag.API_SEARCH_COURSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("AddCourseActivity", "response -> " + response);
                        try {
                            if (bClear) {
                                courseList.clear();
                            }

                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                                JSONArray jsonCourseList= new JSONArray(jsonObject.getString(NETTag.COURSE_ARRAY));
                                for (int i = 0; i < jsonCourseList.length(); i++) {
                                    JSONObject jsonCourseItem = jsonCourseList.optJSONObject(i);
                                    if (jsonCourseItem == null) continue;

                                    CourseItem courseItem = new CourseItem(
                                            jsonCourseItem.getString(NETTag.COURSE_NAME),
                                            jsonCourseItem.getString(NETTag.COURSE_TEACHER),
                                            jsonCourseItem.getString(NETTag.COURSE_SECTIME),
                                            jsonCourseItem.getString(NETTag.COURSE_REALTIME),
                                            jsonCourseItem.getString(NETTag.COURSE_LOCATION),
                                            jsonCourseItem.getString(NETTag.COURSE_ID));
                                    courseList.add(courseItem);
                                }
                                startIndex = startIndex +  jsonCourseList.length();
                                if(jsonCourseList.length() < number){
                                    Log.d("AddCourseActivity", "no more data !!!! ");
                                    hasMoreData = false;
                                }
                                else{
                                    hasMoreData = true;
                                }
                            }
                            else{

                                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            mSwipeLayout.setRefreshing(false);
                            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            return;
                        } finally {

                            mFlagloading = false;
                            mSwipeLayout.setRefreshing(false);

                            if (bClear) {
                                mAdapter.notifyDataSetInvalidated();
                                mListView.setSelectionAfterHeaderView();
                            }
                            else {
                                mAdapter.notifyDataSetChanged();
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mSwipeLayout.setRefreshing(false);
                        Log.e("AddCourseActivity", error.getMessage(), error);
                        Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();

                map.put(NETTag.SEARCH_NAME, courseName);
                map.put(NETTag.SEARCH_NUMBER, String.valueOf(number));
                map.put(NETTag.SEARCH_START, String.valueOf(startIndex));

                //SharedPreferences prefs = getSharedPreferences(mContext);

                return map;
            }
        };
        stringRequest.setTag("AddCourseActivity");
        mQueue.add(stringRequest);
    }

}
