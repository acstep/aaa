package com.app.university;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.app.university.view.SwipeRefreshAndLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MessageActivity extends Activity implements SwipeRefreshAndLoadLayout.OnRefreshListener{
    String mCourseID;
    private SwipeRefreshAndLoadLayout mSwipeLayout;
    private MessageAdapter mAdapter;
    private List<MessageItem> mMessageList;
    private RequestQueue mQueue = null;
    protected ListView mListView;
    private Context mContext;
    private ImageLoader mImageLoader;
    private int mStart = 0;
    boolean mClear = false;
    boolean mNoMore = false;
    boolean mFlagloading = false;


    public class MessageItem {

        public String title;
        public String eventID;
        public String content;
        public String groupid;
        public String userName;
        public String userID;
        public JSONArray imageNameList;
        public int type;
        public String url;
        public int likenum;
        public int commentnum;
        public int anonymous;
        public long postTime;
        public int time;
        public int eventType;
        private Calendar date;

        public static final int TYPE_TITLE = 0;
        public static final int TYPE_MESSAGE = 1;

        public static final int TYPE_COUNT = 2;

        public MessageItem() {

        }

        public int getType() {
            return TYPE_MESSAGE;
        }

        public String getDateDisplayString(Context context) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(postTime*1000);
            Date dt = date.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
            return sdf.format(dt);
        }

    }



    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }


    private class MessageAdapter extends BaseAdapter {
        private String TAG = "MyCourseAdapter";

        private Context context;
        private LayoutInflater inflater;
        private List<MessageItem> messageList;



        public MessageAdapter(Context context, List<MessageItem> messageList) {
            this.context = context;
            this.messageList = messageList;
        }



        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int location) {
            return messageList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return MessageItem.TYPE_TITLE;
            }
            if (messageList != null && position < messageList.size()) {
                return messageList.get(position).getType();
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return MessageItem.TYPE_COUNT;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);

            switch (type) {
                case MessageItem.TYPE_MESSAGE: {
                    EventViewHolder holder = null;
                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.event_item, null);
                        holder = new EventViewHolder();
                        holder.headImage = (ImageView) convertView.findViewById(R.id.event_item_head);
                        holder.textName = (TextView) convertView.findViewById(R.id.event_item_name);
                        holder.textDate = (TextView) convertView.findViewById(R.id.event_item_date);
                        holder.textContent = (TextView) convertView.findViewById(R.id.event_item_content);
                        holder.contentImage1 = (ImageView) convertView.findViewById(R.id.event_item_image1);
                        holder.contentImage2 = (ImageView) convertView.findViewById(R.id.event_item_image2);
                        holder.contentImage3 = (ImageView) convertView.findViewById(R.id.event_item_image3);
                        holder.contentImageonly = (ImageView) convertView.findViewById(R.id.event_item_image_only);
                        holder.textLikeNum = (TextView) convertView.findViewById(R.id.event_item_like);
                        holder.textCommentNum = (TextView) convertView.findViewById(R.id.event_item_reply);
                        holder.layerImageList = (LinearLayout) convertView.findViewById(R.id.layer_image_list);
                        holder.layerImageOne = (FrameLayout) convertView.findViewById(R.id.layer_image_one);
                        holder.LikeLayer = (FrameLayout) convertView.findViewById(R.id.event_like);
                        holder.CommentLayer = (FrameLayout) convertView.findViewById(R.id.event_comment);
                        holder.imageList.add(holder.contentImage1);
                        holder.imageList.add(holder.contentImage2);
                        holder.imageList.add(holder.contentImage3);
                        convertView.setTag(holder);
                    } else {
                        holder = (EventViewHolder) convertView.getTag();

                    }

                    holder.textName.setText(messageList.get(position).userName);
                    holder.textDate.setText(messageList.get(position).getDateDisplayString(mContext));
                    holder.textContent.setText(messageList.get(position).content);

                    holder.textLikeNum.setText(String.valueOf(messageList.get(position).likenum));
                    holder.textCommentNum.setText(String.valueOf(messageList.get(position).commentnum));

                    ImageLoader.ImageListener headlistener = ImageLoader.getImageListener(holder.headImage, R.drawable.abc_list_divider_mtrl_alpha, R.drawable.abc_list_divider_mtrl_alpha);
                    mImageLoader.get(NETTag.API_GET_HEADIMAGE_SMALL+"?id="+ messageList.get(position).userID+".jpg", headlistener);

                    if(messageList.get(position).imageNameList.length() == 0){
                        holder.layerImageList.setVisibility(View.GONE);
                        holder.layerImageOne.setVisibility(View.GONE);

                    }
                    else if(messageList.get(position).imageNameList.length() == 1){
                        holder.layerImageList.setVisibility(View.GONE);
                        holder.layerImageOne.setVisibility(View.VISIBLE);
                        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.contentImageonly, R.drawable.abc_list_divider_mtrl_alpha, R.drawable.abc_list_divider_mtrl_alpha);
                        try {
                            mImageLoader.get(NETTag.API_GET_FEEDIMAGE_SMALL+"?id="+ messageList.get(position).imageNameList.getString(0), listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        holder.layerImageList.setVisibility(View.VISIBLE);
                        holder.layerImageOne.setVisibility(View.GONE);
                        for(int i=0; i<messageList.get(position).imageNameList.length() && i<3; i++){
                            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.imageList.get(i), R.drawable.abc_list_divider_mtrl_alpha, R.drawable.abc_list_divider_mtrl_alpha);
                            try {
                                mImageLoader.get(NETTag.API_GET_FEEDIMAGE_SMALL+"?id="+ messageList.get(position).imageNameList.getString(i), listener);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    holder.LikeLayer.setOnClickListener(new LikeItem_Click(position));
                    holder.CommentLayer.setOnClickListener(new CommentItem_Click(position));
                    //holder.headImage.setImageURI();
                    //holder.contentImage1.setImageURI()
                    //holder.contentImage2.setImageURI()
                    //holder.contentImage3.setImageURI()

                    break;
                }
                case MessageItem.TYPE_TITLE:{
                    EventViewHolder holder = null;
                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.message_title, null);
                        holder = new EventViewHolder();

                        holder.title = (TextView) convertView.findViewById(R.id.group_title_name);

                        convertView.setTag(holder);
                    } else {
                        holder = (EventViewHolder) convertView.getTag();

                    }

                    holder.title.setText(messageList.get(position).title);

                    break;
                }
                default:
                    break;
            }

            return convertView;
        }


        class LikeItem_Click implements View.OnClickListener {
            private int mposition;



            LikeItem_Click(int pos) {
                mposition = pos;

            }
            public void onClick(View v) {
                Log.d("MessageActivity like click pos = ", String.valueOf(mposition)  );
            }
        }

        class CommentItem_Click implements View.OnClickListener {
            private int mposition;



            CommentItem_Click(int pos) {
                mposition = pos;

            }
            public void onClick(View v) {
                Log.d("MessageActivity comment click pos = ", String.valueOf(mposition)  );
            }
        }




        public class EventViewHolder {
            public TextView title;
            public ImageView headImage;
            public TextView  textName;
            public TextView  textDate;
            public TextView  textContent;
            public ImageView contentImage1;
            public ImageView contentImage2;
            public ImageView contentImage3;
            public ImageView contentImageonly;
            public TextView  textLikeNum;
            public TextView  textCommentNum;
            public LinearLayout  layerImageList;
            public FrameLayout  layerImageOne;
            public FrameLayout  LikeLayer;
            public FrameLayout  CommentLayer;
            ArrayList<ImageView> imageList = new ArrayList<ImageView>();


        }


    }

    public void GetMessage(int start){
        mStart = start;
        mFlagloading = true;
        mSwipeLayout.setRefreshing(true);
        if(start == 0){
            mClear = true;
        }
        GetEventRequest stringRequest = new GetEventRequest(this, listener, errorListener, mCourseID, start, Data.GET_MESSAGE_NUMBER);
        mQueue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mContext = this;
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mCourseID =  bundle.getString(Data.COURSE_ID);
        Log.d("MessageActivity course id = ", mCourseID);
        mQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());

        mMessageList = new ArrayList<MessageItem>();
        mAdapter = new MessageAdapter(this, mMessageList);
        mListView = (ListView) findViewById(R.id.message_list);
        mListView.setAdapter(mAdapter);


        mQueue = Volley.newRequestQueue(this);

        mSwipeLayout = (com.app.university.view.SwipeRefreshAndLoadLayout) findViewById(R.id.message_list_swipe);
        mSwipeLayout.setOnRefreshListener(MessageActivity.this);
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
                        && mFlagloading == false
                        && mNoMore == false) {
                    Log.d("AddCourseActivity nexttime = ", String.valueOf(mStart) );

                    GetMessage(mStart);
                }
                return;
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(NETTag.GROPU_EVNET_EVENTID, mMessageList.get(position).eventID);
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        GetMessage(0);

    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("MessageActivity  response = ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                mFlagloading = false;
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    if(mClear == true){
                        mMessageList.clear();
                        mClear = false;
                        MessageItem messageItem = new MessageItem();
                        messageItem.title = jsonObject.getJSONObject(NETTag.GROPU_EVNET_INFO).getString(NETTag.COURSE_NAME);

                        mMessageList.add(messageItem);
                    }
                    mStart = Integer.valueOf(jsonObject.getString(NETTag.GROPU_EVNET_NEXT_START_TIME));
                    JSONArray jsonCourseList= new JSONArray(jsonObject.getString(NETTag.GROPU_EVNET_LIST));
                    mNoMore = false;
                    if(jsonCourseList.length() < Data.GET_MESSAGE_NUMBER){
                        mNoMore = true;
                    }
                    for (int i = 0; i < jsonCourseList.length(); i++) {
                        JSONObject jsonMessageItem = jsonCourseList.optJSONObject(i);


                        MessageItem messageItem = new MessageItem();

                                messageItem.eventID = jsonMessageItem.getString(NETTag.GROPU_EVNET_EVENTID);
                                messageItem.content = jsonMessageItem.getString(NETTag.GROPU_EVNET_CONTENT);
                                messageItem.groupid =     jsonMessageItem.getString(NETTag.GROPU_EVNET_GROUPID);
                                messageItem.userName = jsonMessageItem.getString(NETTag.GROPU_EVNET_NAME);
                                messageItem.userID =jsonMessageItem.getString(NETTag.GROPU_EVNET_USERID);
                                messageItem.imageNameList =jsonMessageItem.getJSONArray(NETTag.GROPU_EVNET_IMAGELIST);
                                messageItem.type =jsonMessageItem.getInt(NETTag.GROPU_EVNET_TYPE);
                                messageItem.url =jsonMessageItem.getString(NETTag.GROPU_EVNET_URL);
                                messageItem.likenum =jsonMessageItem.getInt(NETTag.GROPU_EVNET_LIKENUM);
                                messageItem.commentnum =jsonMessageItem.getInt(NETTag.GROPU_EVNET_COMMENTNUM);
                                messageItem.anonymous =jsonMessageItem.getInt(NETTag.GROPU_EVNET_ANONYMOUS);
                                messageItem.postTime =jsonMessageItem.getInt(NETTag.GROPU_EVNET_POSTTIME);
                                messageItem.time =jsonMessageItem.getInt(NETTag.GROPU_EVNET_TIME);
                        mMessageList.add(messageItem);


                        mSwipeLayout.setRefreshing(false);
                    }
                }
                else{
                    mSwipeLayout.setRefreshing(false);
                    mFlagloading = false;
                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                mSwipeLayout.setRefreshing(false);
                mFlagloading = false;
                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            } finally {
                mSwipeLayout.setRefreshing(false);
                mFlagloading = false;
                if (mClear) {
                    mAdapter.notifyDataSetInvalidated ();
                    mListView.setSelectionAfterHeaderView();
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mSwipeLayout.setRefreshing(false);
            Log.e("MessageActivity", error.getMessage(), error);
            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };


    @Override
    public void onRefresh() {

        mNoMore = false;
        GetMessage(0);
        Log.d("MessageActivity  = ", "onRefresh");
        return;
    }
    @Override
    public void onLoadMore() {
        mSwipeLayout.setRefreshing(false);
        Log.d("MessageActivity  = ", "onLoadMore");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_message) {
            Bundle bundle = new Bundle();
            bundle.putString(Data.COURSE_ID, mCourseID);
            Intent intent = new Intent(this, NewMessageActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
