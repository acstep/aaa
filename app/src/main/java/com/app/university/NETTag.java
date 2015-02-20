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
    public static final String EMAIL = "email";
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
        Log.e("AddCourseActivity", myid + mytoken);
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

    public userData(String name, String university, String dep, String adimission, String sex, String schedule) {
        this.name = name;
        this.university = university;
        this.dep = dep;
        this.adimission = adimission;
        this.sex = sex;
        this.schedule = schedule;
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
        Log.e("AddCourseActivity", myid + mytoken);
        return map;
    }


}