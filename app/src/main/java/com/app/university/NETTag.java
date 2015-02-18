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
    public static final String REMOVE_COURSE_ID = "rmcid";
    public static final String MY_COURSE_LIST = "courselist";
}

class AddCourseRequest extends StringRequest {
    private Context mCcontext;
    private String mCourseID;

    public AddCourseRequest(Context context,String url, String courseID,
                         Response.Listener<String> listener,
                         Response.ErrorListener errorListener) {
        super(Request.Method.POST, url, listener, errorListener);
        mCcontext = context;
        mCourseID = courseID;
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