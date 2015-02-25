package com.app.university;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 2015/2/6.
 */
public class GroupList extends Fragment implements SwipeRefreshAndLoadLayout.OnRefreshListener {


    private SwipeRefreshAndLoadLayout mSwipeLayout;
    private MyGroupAdapter mAdapter;
    private List<GroupItem> groupList;

    protected ListView mListView;

    public class GroupItem {
        private String name;
        private String id;
        private String member;


        public GroupItem(String id, String name, String member) {
            this.name = name;

            this.member = member;

            this.id = id;


        }

        public String getName() { return name;}
        public String member() { return member; }
        public String getId() { return id;}


    }

    private class MyGroupAdapter extends BaseAdapter {
        private String TAG = "MyGroupAdapter";

        private Context context;
        private LayoutInflater inflater;
        private List<GroupItem> groupList;



        public MyGroupAdapter(Context context, List<GroupItem> groupList) {
            this.context = context;
            this.groupList = groupList;
        }



        @Override
        public int getCount() {
            return groupList.size();
        }

        @Override
        public Object getItem(int location) {
            return groupList.get(location);
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
            GroupItem m = groupList.get(position);
            String groupName = m.getName();

            txName.setText(groupName);


            LinearLayout groupItem = (LinearLayout)convertView.findViewById(R.id.my_course_item);
            groupItem.setOnClickListener(new ItemButton_Click(position));


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
            bundle.putString(Data.GROUP_ID, groupList.get(mposition).getId());
            bundle.putInt(Data.GROUP_TYPE, Data.GROUP_TYPE_SOCIAL);
            Intent intent = new Intent(getActivity(), MessageActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){

                    JSONArray jsonCourseList= new JSONArray(jsonObject.getString(NETTag.MY_GROUP_LIST));
                    groupList.clear();
                    if(getActivity() != null && jsonCourseList.length() != 0) {
                        SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Data.CURRENTGROUPLIST, response);
                        editor.commit();
                    }
                    for (int i = 0; i < jsonCourseList.length(); i++) {
                        JSONObject jsonCourseItem = jsonCourseList.optJSONObject(i);
                        if (jsonCourseItem == null) continue;

                        GroupItem courseItem = new GroupItem(
                                jsonCourseItem.getString(NETTag.GROUP_ID),
                                jsonCourseItem.getString(NETTag.GROPU_NAME),
                                jsonCourseItem.getString(NETTag.COURSE_MEMBER));
                        groupList.add(courseItem);
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
            mSwipeLayout.setRefreshing(false);
            if(getActivity() != null) {
                SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
                String response = settings.getString(Data.CURRENTGROUPLIST, "[]");

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray jsonCourseList = new JSONArray(jsonObject.getString(NETTag.MY_COURSE_LIST));
                    groupList.clear();
                    for (int i = 0; i < jsonCourseList.length(); i++) {
                        JSONObject jsonCourseItem = jsonCourseList.optJSONObject(i);
                        if (jsonCourseItem == null) continue;

                        GroupItem groupItem = new GroupItem(
                                jsonCourseItem.getString(NETTag.GROUP_ID),
                                jsonCourseItem.getString(NETTag.GROPU_NAME),
                                jsonCourseItem.getString(NETTag.COURSE_MEMBER));
                        groupList.add(groupItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
            mSwipeLayout.setRefreshing(false);

            Log.e("GroupList", error.getMessage(), error);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GroupList  = ", "onResume");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("GroupList  = ", "setUserVisibleHint");
        if(mSwipeLayout != null){
            mSwipeLayout.setRefreshing(true);
            GetMyGroupRequest stringRequest = new GetMyGroupRequest(getActivity(), listener, errorListener);
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
        groupList = new ArrayList<GroupItem>();
        mAdapter = new MyGroupAdapter(getActivity(), groupList);
        mListView = (ListView) view.findViewById(R.id.my_course_list);
        mListView.setAdapter(mAdapter);


        mSwipeLayout = (com.app.university.view.SwipeRefreshAndLoadLayout) view.findViewById(R.id.my_course_list_swipe);
        mSwipeLayout.setOnRefreshListener(GroupList.this);
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

                return;
            }

        });

        mSwipeLayout.setRefreshing(true);
        GetMyGroupRequest stringRequest = new GetMyGroupRequest(getActivity(), listener, errorListener);
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        return view;
    }
}