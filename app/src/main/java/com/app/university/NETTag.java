package com.app.university;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matt on 2015/2/11.
 */
public class NETTag {

    public static final String API_CREATE_ACCOUNT = "http://newsapi.nexbbs.com/api/createaccount";
    public static final String API_UPDATE_ACCOUNT = "http://newsapi.nexbbs.com/api/updateaccount";
    public static final String API_LOGIN_ACCOUNT = "http://newsapi.nexbbs.com/api/login";
    public static final String API_SEARCH_COURSE = "http://newsapi.nexbbs.com/api/findcourse";
    public static final String API_UPLOAD_IMAGE = "http://newsapi.nexbbs.com/api/uploadheadimg";
    public static final String API_ADD_COURSE = "http://newsapi.nexbbs.com/api/addcourse";
    public static final String API_DELETE_COURSE = "http://newsapi.nexbbs.com/api/deletecourse";
    public static final String API_GET_MY_COURSE = "http://newsapi.nexbbs.com/api/getcourse";
    public static final String API_POST_COURSE_EVENT = "http://newsapi.nexbbs.com/api/addevent";
    public static final String API_GET_GROUP_EVENT = "http://newsapi.nexbbs.com/api/getevent";
    public static final String API_POST_COMMENT = "http://newsapi.nexbbs.com/api/addcomment";
    public static final String API_GET_COMMENT = "http://newsapi.nexbbs.com/api/getcomment";
    public static final String API_GET_HEADIMAGE = "http://newsapi.nexbbs.com/api/headimage";
    public static final String API_GET_HEADIMAGE_SMALL = "http://newsapi.nexbbs.com/api/headimagem";
    public static final String API_GET_FEEDIMAGE = "http://newsapi.nexbbs.com/api/eventimage";
    public static final String API_GET_FEEDIMAGE_SMALL = "http://newsapi.nexbbs.com/api/eventimagem";
    public static final String API_GET_MY_GROUP = "http://newsapi.nexbbs.com/api/getgroup";
    public static final String API_GET_MY_NOTIFY = "http://newsapi.nexbbs.com/api/getnotify";
    public static final String API_GET_CRASH = "http://newsapi.nexbbs.com/api/crash";
    public static final String CRASH = "crash";
    public static final String EMAIL = "email";
    public static final String GCM = "gcm";
    public static final String PASSWD = "passwd";
    public static final String RESULT = "result";
    public static final String OK = "ok";
    public static final String ERROR_USER_EXIST = "user exist";
    public static final String USER_ID = "userid";
    public static final String TOKEN = "token";
    public static final String UNIVERSITY = "university";
    public static final String DEPARTMENT = "dep";
    public static final String USER = "user";
    public static final String SCHEDULE = "schedule";
    public static final String SEARCH_NAME = "name";
    public static final String SEARCH_NUMBER = "number";
    public static final String SEARCH_START = "start";
    public static final String COURSE_ARRAY = "course";
    public static final String COURSE_NAME = "name";
    public static final String COURSE_TEACHER = "teacher";
    public static final String COURSE_SECTIME = "sectime";
    public static final String COURSE_REALTIME = "time";
    public static final String COURSE_LOCATION = "loc";
    public static final String COURSE_STUDENT_NUMBER = "stunum";
    public static final String COURSE_ID = "id";
    public static final String COURSE_ID2 = "courseid";
    public static final String PRE_COURSE_STRING = "prectring";
    public static final String POST_COURSE_STRING = "postcstring";
    public static final String SELF_ADD = "selfadd";
    public static final String REMOVE_COURSE_ID = "rmcid";
    public static final String MY_COURSE_LIST = "courselist";
    public static final String USER_SEX = "sex";
    public static final String USER_ADMISSION = "admission";
    public static final String USER_NAME = "name";
    public static final String USER_DEPARTMENT = "dep";
    public static final String USER_UNIVERSITY = "univerisity";
    public static final String UPLOAD_FILENAME = "filename";
    public static final String COURSE_EVNET = "data";
    public static final String COURSE_EVNET_GROUPID = "groupid";
    public static final String COURSE_EVNET_IMAGE_LIST = "imagelist";
    public static final String COURSE_EVNET_CONTENT = "content";
    ///////////////////////
    public static final String GROPU_EVNET_INFO ="groupinfo";
    public static final String GROPU_EVNET = "data";
    public static final String GROPU_EVNET_LIST ="event";
    public static final String GROPU_EVNET_EVENTID ="eventid";
    public static final String GROPU_EVNET_USERID ="userid";
    public static final String GROPU_EVNET_NAME ="name";
    public static final String GROPU_EVNET_POSTTIME ="posttime";
    public static final String GROPU_EVNET_CONTENT ="content";
    public static final String GROPU_EVNET_IMAGELIST ="imagelist";
    public static final String GROPU_EVNET_GROUPID ="groupid";
    public static final String GROPU_EVNET_ANONYMOUS ="anonymous";
    public static final String GROPU_EVNET_TIME ="time";
    public static final String GROPU_EVNET_TYPE ="grouptype";
    public static final String GROPU_EVNET_URL ="url";
    public static final String GROPU_EVNET_LIKENUM ="likenum";
    public static final String GROPU_EVNET_COMMENTNUM ="commentnum";
    public static final String GROPU_EVNET_NEXT_START_TIME ="nextstart";
    /////////////////////////////////
    public static final String GET_EVNET_GROUPID ="groupid";
    public static final String GET_EVNET_NUMBER ="number";
    public static final String GET_EVNET_START ="start";
    public static final String GET_EVNET_GROUP_TYPE ="grouptype";
    /////////////////////////////////////////////
    public static final String GET_COMMENT_EVENTID ="eventid";
    public static final String GET_COMMENT_EVENTINFO ="eventinfo";
    public static final String GET_COMMENT_LIST ="comment";
    public static final String POST_COMMENT_DATA ="data";
    public static final String POST_COMMENT_CONTENT ="content";
    public static final String POST_COMMENT_ID ="commentid";
    public static final String POST_COMMENT_USERID ="userid";
    public static final String POST_COMMENT_USER_NAME ="name";
    public static final String POST_COMMENT_POST_TIME ="posttime";
    public static final String POST_COMMENT_ANONMOUS ="anonymous";
    public static final String POST_COMMENT_TYPE ="grouptype";

    /////////////////////////////////////
    public static final String GROPU_NAME = "name";
    public static final String COURSE_MEMBER = "member";
    public static final String GROUP_ID = "id";
    public static final String MY_GROUP_LIST = "grouplist";
    ////////////////////////////////////
    public static final String NOTIFY_TIME = "time";
    public static final String NOTIFY_FROMID = "fromid";
    public static final String NOTIFY_USERID = "userid";
    public static final String NOTIFY_TITLE = "title";
    public static final String NOTIFY_CONTENT = "content";
    public static final String NOTIFY_TYPE = "type";
    public static final String NOTIFY_URL = "url";
    public static final String NOTIFY_EVENTID = "eventid";
    public static final String NOTIFY_FROMNAME = "fromname";
    public static final String NOTIFY_ID = "id";
    public static final String MY_NOTIFY_LIST = "notify";
    public static final String NOTIFY_EVNET_NEXT_START_TIME ="nextstart";
    //////////////////////////////////////////
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "content";
}


class AddCourseRequest extends StringRequest {
    private Context mCcontext;
    private String mCourseID;
    private String mPreCourseString;
    private String mPostCourseString;
    private String mSelfAdd;
    private String mUrl;


    public AddCourseRequest(Context context,String url, String courseID,
                         Response.Listener<String> listener,
                         Response.ErrorListener errorListener,String PreCourseString, String PostCourseString, String selfAdd) {
        super(Request.Method.POST, url, listener, errorListener);
        mCcontext = context;
        mCourseID = courseID;
        mPreCourseString = PreCourseString;
        mPostCourseString = PostCourseString;
        mSelfAdd = selfAdd;
        mUrl = url;
    }



    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        map.put(NETTag.COURSE_ID2, mCourseID);
        map.put(NETTag.PRE_COURSE_STRING,mPreCourseString);
        map.put(NETTag.POST_COURSE_STRING,mPostCourseString);
        if(mUrl.compareTo(NETTag.API_ADD_COURSE) == 0){
            map.put(NETTag.SELF_ADD,mSelfAdd);
        }

        Log.e("AddCourseActivity", myid + mytoken);
        return map;
    }


}


class GetMyCourseRequest extends StringRequest {
    private Context mCcontext;
    private String mCourseID;

    public GetMyCourseRequest(Context context,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Request.Method.POST, NETTag.API_GET_MY_COURSE, listener, errorListener);
        mCcontext = context;

    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        Log.d("AddCourseActivity", myid + mytoken);
        return map;
    }


}


class GetMyGroupRequest extends StringRequest {
    private Context mCcontext;
    private String mCourseID;

    public GetMyGroupRequest(Context context,
                              Response.Listener<String> listener,
                              Response.ErrorListener errorListener) {
        super(Request.Method.POST, NETTag.API_GET_MY_GROUP, listener, errorListener);
        mCcontext = context;

    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        Log.d("GetMyGroupRequest", myid + mytoken);
        return map;
    }


}


class userData{
    String name = "";
    String university = "";
    String dep = "";
    String adimission = "";
    String sex = "";
    String schedule = "";
    String gcm = "";

    public userData(String name, String university, String dep, String adimission, String sex, String schedule, String gcm) {
        this.name = name;
        this.university = university;
        this.dep = dep;
        this.adimission = adimission;
        this.sex = sex;
        this.schedule = schedule;
        this.gcm = gcm;
    }
}




class UpdateAccountRequest extends StringRequest {
    private Context mCcontext;
    private String mCourseID;
    private userData mUserData;



    public UpdateAccountRequest(Context context,
                              Response.Listener<String> listener,
                              Response.ErrorListener errorListener,
                              userData udata) {
        super(Request.Method.POST, NETTag.API_UPDATE_ACCOUNT, listener, errorListener);
        mCcontext = context;
        mUserData = udata;
    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        if(!mUserData.name.isEmpty()){
            map.put(NETTag.USER_NAME, mUserData.name);
        }
        if(!mUserData.university.isEmpty()){
            map.put(NETTag.USER_UNIVERSITY, mUserData.university);
        }
        if(!mUserData.dep.isEmpty()){
            map.put(NETTag.USER_DEPARTMENT, mUserData.dep);
        }
        if(!mUserData.adimission.isEmpty()){
            map.put(NETTag.USER_ADMISSION, mUserData.adimission);
        }
        if(!mUserData.sex.isEmpty()){
            map.put(NETTag.USER_SEX, mUserData.sex);
        }
        if(!mUserData.schedule.isEmpty()){
            map.put(NETTag.SCHEDULE, mUserData.schedule);
        }
        if(!mUserData.gcm.isEmpty()){
            map.put(NETTag.GCM, mUserData.gcm);
        }
        Log.e("AddCourseActivity", myid + mytoken);
        return map;
    }


}




class PostEventRequest extends StringRequest {
    private Context mCcontext;
    private String mEvent;



    public PostEventRequest(Context context,
                                Response.Listener<String> listener,
                                Response.ErrorListener errorListener,
                                String event) {
        super(Request.Method.POST, NETTag.API_POST_COURSE_EVENT, listener, errorListener);
        mCcontext = context;
        mEvent = event;
    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        map.put(NETTag.GROPU_EVNET, mEvent);


        Log.e("AddCourseActivity", myid + mytoken);
        return map;
    }


}


class GetEventRequest extends StringRequest {
    private Context mCcontext;
    private int mStart;
    private int mNumber;
    private String mGroupid;
    private int mGroupType;


    public GetEventRequest(Context context,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener,
                                  String groupid, int grouptype, int start, int number) {
        super(Request.Method.POST, NETTag.API_GET_GROUP_EVENT, listener, errorListener);
        mCcontext = context;
        mStart = start;
        mNumber = number;
        mGroupid = groupid;
        mGroupType = grouptype;
    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        map.put(NETTag.GET_EVNET_GROUPID, mGroupid);
        map.put(NETTag.GET_EVNET_GROUP_TYPE, String.valueOf(mGroupType));
        if(mNumber != 0) {
            map.put(NETTag.GET_EVNET_NUMBER, String.valueOf(mNumber) );
        }
        if(mStart != 0){
            map.put(NETTag.GET_EVNET_START, String.valueOf(mStart));
        }


        Log.e("GetEventRequest", myid +' ' + mytoken +' ' +mGroupid +' ' +String.valueOf(mNumber));
        return map;
    }


}


class GetCommentRequest extends StringRequest {
    private Context mCcontext;
    private int mStart;
    private int mNumber;
    private String mEventID;


    public GetCommentRequest(Context context,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener,
                           String eventid, int start, int number) {
        super(Request.Method.POST, NETTag.API_GET_COMMENT, listener, errorListener);
        mCcontext = context;
        mStart = start;
        mNumber = number;
        mEventID = eventid;
    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        map.put(NETTag.GET_COMMENT_EVENTID, mEventID);
        if(mNumber != 0) {
            map.put(NETTag.GET_EVNET_NUMBER, String.valueOf(mNumber) );
        }
        if(mStart != 0){
            map.put(NETTag.GET_EVNET_START, String.valueOf(mStart));
        }


        Log.e("GetEventRequest", myid +' ' + mytoken +' ' +mEventID +' ' +String.valueOf(mNumber));
        return map;
    }

}


class PostCommentRequest extends StringRequest {
    private Context mCcontext;
    private String mComment;



    public PostCommentRequest(Context context,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener,
                                  String comment) {
        super(Request.Method.POST, NETTag.API_POST_COMMENT, listener, errorListener);
        mCcontext = context;
        mComment = comment;
    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);
        map.put(NETTag.POST_COMMENT_DATA, mComment);


        Log.e("PostCommentRequest", myid + mytoken);
        return map;
    }


}



class SearchCourseRequest extends StringRequest {
    private Context mCcontext;
    private String mCourseName;
    private int mNumber;
    private int mStartIndex;



    public SearchCourseRequest(Context context,
                              Response.Listener<String> listener,
                              Response.ErrorListener errorListener,
                              String courseName, int number, int startIndex) {
        super(Request.Method.POST, NETTag.API_SEARCH_COURSE, listener, errorListener);
        this.mCcontext = context;
        this.mCourseName = courseName;
        this.mNumber = number;
        this.mStartIndex = startIndex;
    }


    @Override
    protected Map<String, String> getParams() {

        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.SEARCH_NAME, mCourseName);
        map.put(NETTag.SEARCH_NUMBER, String.valueOf(mNumber));
        map.put(NETTag.SEARCH_START, String.valueOf(mStartIndex));


        return map;
    }


}




class SendCrashRequest extends StringRequest {
    private Context mCcontext;
    private String mCrash;

    public SendCrashRequest(Context context,
                               Response.Listener<String> listener,
                               Response.ErrorListener errorListener,
                               String crash) {
        super(Request.Method.POST, NETTag.API_GET_CRASH, listener, errorListener);
        this.mCcontext = context;
        this.mCrash = crash;

    }


    @Override
    protected Map<String, String> getParams() {

        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.CRASH, mCrash);
        return map;
    }


}


class GetNotifyRequest extends StringRequest {
    private Context mCcontext;
    private int mStart;
    private int mNumber;
    private String mEventID;


    public GetNotifyRequest(Context context,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener,
                             int start, int number) {
        super(Request.Method.POST, NETTag.API_GET_MY_NOTIFY, listener, errorListener);
        mCcontext = context;
        mStart = start;
        mNumber = number;

    }


    @Override
    protected Map<String, String> getParams() {
        SharedPreferences shareId = mCcontext.getSharedPreferences("ID", Context.MODE_PRIVATE);
        final String myid = shareId.getString(Data.USER_ID, null);
        final String mytoken = shareId.getString(Data.TOKEN, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put(NETTag.USER_ID,myid);
        map.put(NETTag.TOKEN, mytoken);

        if(mNumber != 0) {
            map.put(NETTag.GET_EVNET_NUMBER, String.valueOf(mNumber) );
        }
        if(mStart != 0){
            map.put(NETTag.GET_EVNET_START, String.valueOf(mStart));
        }

        Log.e("GetNotifyRequest", myid +' ' + mytoken +' ' +mEventID +' ' +String.valueOf(mNumber));
        return map;
    }

}