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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.app.university.view.SwipeRefreshAndLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 2015/2/6.
 */
public class NotifyList extends Fragment implements SwipeRefreshAndLoadLayout.OnRefreshListener {


    private SwipeRefreshAndLoadLayout mSwipeLayout;
    private MyNotifyAdapter mAdapter;
    private List<NofityItem> notifyList;
    private Integer mStartIndex = 0;
    private  ImageLoader mImageLoader;
    private boolean hasMoreData;
    private boolean mLoading;


    protected ListView mListView;

    public class NofityItem {
        public String eventid;
        public String fromname;
        public String id;
        public String title;
        public String userid;
        public String content;
        public String fromid;
        public String url;
        public long time;
        public int type;

        public static final int TYPE_EVENT_NITIFY = 1;
        public static final int TYPE_URL = 2;
        public static final int TYPE_COUNT = 3;

        public NofityItem() {

        }

        public int getType() {
            return this.type;
        }


    }

    private class MyNotifyAdapter extends BaseAdapter {
        private String TAG = "MyNotifyAdapter";

        private Context context;
        private LayoutInflater inflater;
        private List<NofityItem> notifyList;



        public MyNotifyAdapter(Context context, List<NofityItem> notifyList) {
            this.context = context;
            this.notifyList = notifyList;
        }

        @Override
        public int getItemViewType(int position) {
            return this.notifyList.get(position).type;
            //return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return NofityItem.TYPE_COUNT;
        }

        @Override
        public int getCount() {
            return notifyList.size();
        }

        @Override
        public Object getItem(int location) {
            return notifyList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int type = getItemViewType(position);

            switch (type) {
                case NofityItem.TYPE_EVENT_NITIFY: {
                    EventViewHolder holder = null;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.notify_item, null);

                        holder = new EventViewHolder();
                        holder.logoImage = (ImageView) convertView.findViewById(R.id.image_notify_logo);
                        holder.textTitle = (TextView) convertView.findViewById(R.id.text_notify_title);
                        holder.textContent = (TextView) convertView.findViewById(R.id.text_notify_content);

                        convertView.setTag(holder);
                    } else {
                        holder = (EventViewHolder) convertView.getTag();

                    }

                    holder.textTitle.setText(notifyList.get(position).title);

                    holder.textContent.setText(notifyList.get(position).content);

                    ImageLoader.ImageListener headlistener = ImageLoader.getImageListener(holder.logoImage,R.mipmap.headphoto, R.mipmap.headphoto);

                    mImageLoader.get(NETTag.API_GET_HEADIMAGE_SMALL+"?id="+ notifyList.get(position).fromid+".jpg", headlistener);
                    //holder.LikeLayer.setOnClickListener(new LikeItem_Click(position));
                    //holder.CommentLayer.setOnClickListener(new CommentItem_Click(position));
                    //holder.headImage.setImageURI();
                    //holder.contentImage1.setImageURI()
                    //holder.contentImage2.setImageURI()
                    //holder.contentImage3.setImageURI()
                    break;
                }
                case NofityItem.TYPE_URL:{
                    EventViewHolder holder = null;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.notify_item, null);

                        holder = new EventViewHolder();
                        holder.logoImage = (ImageView) convertView.findViewById(R.id.image_notify_logo);
                        holder.textTitle = (TextView) convertView.findViewById(R.id.text_notify_title);
                        holder.textContent = (TextView) convertView.findViewById(R.id.text_notify_content);

                        convertView.setTag(holder);
                    } else {
                        holder = (EventViewHolder) convertView.getTag();

                    }

                    holder.textTitle.setText(notifyList.get(position).title);

                    holder.textContent.setText(notifyList.get(position).content);
                    ImageLoader.ImageListener headlistener = ImageLoader.getImageListener(holder.logoImage, R.mipmap.headphoto, R.mipmap.headphoto);

                    mImageLoader.get(NETTag.API_GET_HEADIMAGE_SMALL+"?id="+ notifyList.get(position).fromid+".jpg", headlistener);

                    break;
                }
                default:
                    break;
            }

            LinearLayout itemLayout = (LinearLayout)convertView.findViewById(R.id.layout_notify_item);
            itemLayout.setOnClickListener(new ItemButton_Click(position));

            return convertView;
        }


        public class EventViewHolder {
            public TextView title;
            public ImageView logoImage;
            public TextView  textTitle;
            public TextView  textDate;
            public TextView  textContent;
        }
    }


    class ItemButton_Click implements View.OnClickListener {
        private int mposition;

        ItemButton_Click(int pos) {
            mposition = pos;

        }
        public void onClick(View v) {

            if(notifyList.get(mposition).type==NofityItem.TYPE_EVENT_NITIFY){
                Bundle bundle = new Bundle();
                bundle.putString(NETTag.GROPU_EVNET_EVENTID, notifyList.get(mposition).eventid);
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
                Log.d("NotifyList  click pos = ", String.valueOf(mposition)  );
            }
            if(notifyList.get(mposition).type==NofityItem.TYPE_URL){
                Bundle bundle = new Bundle();
                bundle.putString(Data.NOTIFY_URL, notifyList.get(mposition).url);
                Intent intent = new Intent(getActivity(), WebViewerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                Log.d("NotifyList  click pos = ", String.valueOf(mposition)  );
            }
            else{
                return;
            }

        }
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.d("NotifyList Response = ", response);
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){

                    JSONArray jsonDataList= new JSONArray(jsonObject.getString(NETTag.MY_NOTIFY_LIST));
                    if (mStartIndex == 0) {
                        notifyList.clear();
                    }
                    if(jsonDataList.length() < Data.GET_MESSAGE_NUMBER){
                        hasMoreData = false;
                        Log.d("NotifyList hasMoreData = ", "false");
                    }
                    mStartIndex = Integer.valueOf(jsonObject.getString(NETTag.NOTIFY_EVNET_NEXT_START_TIME));
                    Log.d("NotifyList  jsonCourseList.length() = ", String.valueOf(jsonDataList.length()));
                    if(getActivity() != null && jsonDataList.length() != 0 && notifyList.isEmpty()) {
                        SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Data.CURRENTNOTIGYLIST, response);
                        editor.commit();
                    }
                    for (int i = 0; i < jsonDataList.length(); i++) {
                        JSONObject jsonDataItem = jsonDataList.optJSONObject(i);
                        if (jsonDataItem == null) continue;

                        NofityItem notifyItem = new NofityItem();
                        notifyItem.time = jsonDataItem.getInt(NETTag.NOTIFY_TIME);
                        notifyItem.fromid = jsonDataItem.getString(NETTag.NOTIFY_FROMID);
                        notifyItem.userid = jsonDataItem.getString(NETTag.NOTIFY_USERID);
                        notifyItem.title = jsonDataItem.getString(NETTag.NOTIFY_TITLE);
                        notifyItem.content = jsonDataItem.getString(NETTag.NOTIFY_CONTENT);
                        notifyItem.type = jsonDataItem.getInt(NETTag.NOTIFY_TYPE);
                        notifyItem.url = jsonDataItem.getString(NETTag.NOTIFY_URL);
                        notifyItem.eventid = jsonDataItem.getString(NETTag.NOTIFY_EVENTID);
                        notifyItem.fromname = jsonDataItem.getString(NETTag.NOTIFY_FROMNAME);
                        notifyItem.id = jsonDataItem.getString(NETTag.NOTIFY_ID);
                        notifyList.add(notifyItem);
                    }
                    mSwipeLayout.setRefreshing(false);
                    Log.d("GetNotifyRequest", "set mLoading false" );
                    mLoading = false;
                }
                else{
                    mLoading = false;
                    mSwipeLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                mSwipeLayout.setRefreshing(false);
                mLoading = false;
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            } finally {
                mSwipeLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
                mLoading = false;
                Log.d("GetNotifyRequest", "set finally mLoading false" );
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mSwipeLayout.setRefreshing(false);
            if(getActivity() != null) {
                SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
                String response = settings.getString(Data.CURRENTNOTIGYLIST, "[]");

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray jsonDataList = new JSONArray(jsonObject.getString(NETTag.MY_NOTIFY_LIST));
                    notifyList.clear();


                    for (int i = 0; i < jsonDataList.length(); i++) {

                        JSONObject jsonDataItem = jsonDataList.optJSONObject(i);
                        if (jsonDataItem == null) continue;

                        NofityItem notifyItem = new NofityItem();
                        notifyItem.time = jsonDataItem.getInt(NETTag.NOTIFY_TIME);
                        notifyItem.fromid = jsonDataItem.getString(NETTag.NOTIFY_FROMID);
                        notifyItem.userid = jsonDataItem.getString(NETTag.NOTIFY_USERID);
                        notifyItem.title = jsonDataItem.getString(NETTag.NOTIFY_TITLE);
                        notifyItem.content = jsonDataItem.getString(NETTag.NOTIFY_CONTENT);
                        notifyItem.type = jsonDataItem.getInt(NETTag.NOTIFY_TYPE);
                        notifyItem.url = jsonDataItem.getString(NETTag.NOTIFY_URL);
                        notifyItem.eventid = jsonDataItem.getString(NETTag.NOTIFY_EVENTID);
                        notifyItem.fromname = jsonDataItem.getString(NETTag.NOTIFY_FROMNAME);
                        notifyItem.id = jsonDataItem.getString(NETTag.NOTIFY_ID);
                        notifyList.add(notifyItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
            mSwipeLayout.setRefreshing(false);

            Log.e("NotifyList", error.getMessage(), error);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };





    @Override
    public void onRefresh() {
        if (mLoading == true){
            return;
        }
        mSwipeLayout.setRefreshing(true);
        mStartIndex = 0;
        hasMoreData = true;
        mLoading = true;

        GetNotifyRequest stringRequest = new GetNotifyRequest(getActivity(), listener, errorListener,mStartIndex,Data.GET_MESSAGE_NUMBER);
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        Log.d("NotifyList  = ", "onRefresh");
        return;
    }
    @Override
    public void onLoadMore() {
        mSwipeLayout.setRefreshing(false);
        Log.d("NotifyList  = ", "onLoadMore");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.notify_list, container, false);
        notifyList = new ArrayList<NofityItem>();
        mAdapter = new MyNotifyAdapter(getActivity(), notifyList);
        mListView = (ListView) view.findViewById(R.id.notify_list);
        mListView.setAdapter(mAdapter);
        mImageLoader =  MySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader();
        mStartIndex = 0;
        hasMoreData = false;
        mLoading = false;

        mSwipeLayout = (com.app.university.view.SwipeRefreshAndLoadLayout) view.findViewById(R.id.notify_list_swipe);
        mSwipeLayout.setOnRefreshListener(NotifyList.this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setmMode(SwipeRefreshAndLoadLayout.Mode.PULL_FROM_START);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount > totalItemCount -3
                        && totalItemCount > 0
                        && hasMoreData == true) {

                    if (mLoading == true){
                        Log.d("GetNotifyRequest", "set mLoading true return" );
                        return;
                    }

                    Log.d("GetNotifyRequest", "set mLoading true " );
                    mLoading = true;
                    mSwipeLayout.setRefreshing(true);
                    GetNotifyRequest stringRequest = new GetNotifyRequest(getActivity(), listener, errorListener,mStartIndex,Data.GET_MESSAGE_NUMBER);
                    MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);


                    //getNews(mRequestNextUrl, false);
                }

                return;
            }

        });

        mSwipeLayout.setRefreshing(true);
        mLoading = true;
        mStartIndex = 0;
        hasMoreData = true;
        GetNotifyRequest stringRequest = new GetNotifyRequest(getActivity(), listener, errorListener,mStartIndex,Data.GET_MESSAGE_NUMBER);
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        return view;
    }
}