package com.app.university;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matt on 2015/2/6.
 */
public class Schedule extends Fragment {

    private FrameLayout timeFrame;
    private JSONArray mCourseJsonArray;
    private View viewSchedule;
    private FrameLayout monView;
    private FrameLayout tueView;
    private FrameLayout wedView;
    private FrameLayout thuView;
    private FrameLayout friView;
    private int posY = 0;
    private boolean mFirstLaunch = true;
    private RequestQueue mQueue = null;
    private CourseColor courseColor;


    class CourseInfo{
        String id = "";
        String name = "";
        String teacher = "";
        String color = "";
        String timeString = "";
        String time = "";
        String courseBlockTime = "";
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFirstLaunch){
            mFirstLaunch = false;
            SharedPreferences settings = getActivity().getSharedPreferences ("ID", Context.MODE_PRIVATE);
            if(settings.getBoolean(Data.COURSE_SCHEDULE_SET,false) == false){
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivityForResult(intent, 0);
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !mFirstLaunch) {
            try {
                Log.d("Schedule  ", "redraw");
                drawCourse();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void drawCourse() throws JSONException {
        String id = "";
        String name = "";
        String teacher = "";
        String color = "";
        String timeString = "";
        String time = "";

        monView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_mon);
        tueView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_tue);
        wedView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_wed);
        thuView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_thu);
        friView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_fri);

        monView.removeAllViews();
        tueView.removeAllViews();
        wedView.removeAllViews();
        thuView.removeAllViews();
        friView.removeAllViews();

        SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
        String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
        try {
            mCourseJsonArray = new JSONArray(jsonCoursString);
        } catch (JSONException e) {
            mCourseJsonArray = new JSONArray();
        }
        Log.d("Schedule mCourseJsonArray = ", mCourseJsonArray.toString());

        for (int i=0;i<mCourseJsonArray.length();i++){
            String courseTimeString = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_TIME);
            Integer index = 0;
            while(index+15 <= courseTimeString.length()){
                CourseInfo courseInfo = new CourseInfo();
                courseInfo.id = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_ID);
                courseInfo.name = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_NAME);
                courseInfo.teacher = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_TEACHER);
                courseInfo.color = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_COLOR);
                courseInfo.timeString = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_TIME);

                FrameLayout dayFrame;

                DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                int timeStartTop = (int) (50 * displayMetrics.density);
                float dpHeight = displayMetrics.heightPixels;
                int timeBlockHeight = (int) (dpHeight/14);
                time = courseInfo.timeString.substring(index,index+15);
                courseInfo.courseBlockTime = time;
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                String day = time.substring(0,3);
                int startHR = Integer.valueOf(time.substring(4, 6));
                int startMin = Integer.valueOf(time.substring(7, 9));
                int endHR = Integer.valueOf(time.substring(10, 12));
                int endMin = Integer.valueOf(time.substring(13, 15));
                int startPos = timeStartTop+(startHR-8)*timeBlockHeight + (int)(((float)startMin/60)*timeBlockHeight);
                int endPos = timeStartTop+(endHR-8)*timeBlockHeight + (int)(((float)endMin/60)*timeBlockHeight);
                Button btn = new Button(getActivity());
                int englishIndex = courseInfo.name.indexOf("-");
                if(englishIndex != -1){
                    courseInfo.name = courseInfo.name.substring(0,courseInfo.name.indexOf("-"));
                }
                btn.setText(courseInfo.name);
                params1.setMargins(0, startPos, 0, 0);
                btn.setTextSize(14);
                btn.setTextColor(0xffffffff);
                params1.height = endPos-startPos;
                btn.setLayoutParams(params1);
                btn.setPadding(3, 3, 3, 3);
                btn.setMaxLines(4);
                btn.setHorizontallyScrolling(false);
                btn.setEllipsize(TextUtils.TruncateAt.END);

                btn.setBackgroundResource(R.drawable.btn_radus);
                Log.d("AddCourseActivity color = ",  String.valueOf(Integer.parseInt(courseInfo.color, 16)));
                Integer iColor = Integer.parseInt(courseInfo.color, 16);
                int backgroundColor = iColor.intValue() + 0xff000000;
                ((GradientDrawable)btn.getBackground()).setColor(backgroundColor);
                switch (day){
                    case "MON":
                        monView.addView(btn);
                        break;
                    case "TUE":
                        tueView.addView(btn);
                        break;
                    case "WED":
                        wedView.addView(btn);
                        break;
                    case "THU":
                        thuView.addView(btn);
                        break;
                    case "FRI":
                        friView.addView(btn);
                        break;
                    default:
                        break;
                }

                btn.setOnClickListener(new courseButton_Click(btn, courseInfo));
                index = index + 16;
            }

        }



    }


    class newCourse_Click implements View.OnClickListener, View.OnTouchListener{

        private FrameLayout mFrame;
        private String mWeekDay ;

        newCourse_Click(FrameLayout frame, String weekDay) {
            mFrame = frame;
            mWeekDay = weekDay;
        }

        public void onClick(View v) {
            final CharSequence courseOption[] = { getString(R.string.course_add)};

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
                    if (which == 0) {
                        dialog.cancel();
                        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                        int timeStartTop = (int) (40 * displayMetrics.density);
                        float dpHeight = displayMetrics.heightPixels;
                        int timeBlockHeight = (int) (dpHeight/14);
                        int startHR = (int)((posY - timeStartTop)/timeBlockHeight) + 8;
                        String timeString = mWeekDay + "-"+ String.format("%02d", startHR) + ":" + String.format("%02d", 0) + "~" + String.format("%02d", startHR+1) + ":" + String.format("%02d", 0);
                        Bundle bundle = new Bundle();
                        bundle.putString(Data.COURSE_NAME, "");
                        bundle.putString(Data.COURSE_TIME, timeString);
                        bundle.putString(Data.COURSE_ID, "");
                        Intent intent = new Intent(getActivity(), CourseTimeActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    } else {
                        dialog.cancel();
                    }
                }
            });
            alert.show();
        }

        public boolean onTouch(View v, MotionEvent event) {
            posY = (int)event.getY();
            Log.d("Schedule mCourseJsonArray = onTouch", String.valueOf(event.getY()) );
            return false;
        }
    }

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("AddCourseActivity", error.getMessage(), error);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    Response.Listener<String> listener = new Response.Listener<String>() {
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
                    courseColor = new CourseColor(schedule);
                    mCourseJsonArray = CommonUtil.getCourseJsonArray(getActivity());
                    drawCourse();
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


    class courseButton_Click implements View.OnClickListener {

        private Button mbutton;
        private CourseInfo mCourseInfo;


        courseButton_Click(Button button, CourseInfo courseInfo) {

            mbutton = button;
            mCourseInfo = courseInfo;
        }
        public void onClick(View v) {
            final CharSequence courseOption[] = { getString(R.string.course_edit),getString( R.string.course_delete) };

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
                    if (which == 0) {
                        dialog.cancel();
                        Bundle bundle = new Bundle();
                        bundle.putString(Data.COURSE_NAME, mCourseInfo.name);
                        bundle.putString(Data.COURSE_TIME, mCourseInfo.courseBlockTime);
                        bundle.putString(Data.COURSE_ID, mCourseInfo.id);
                        Intent intent = new Intent(getActivity(), CourseTimeActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);

                        Log.d("Schedule mCourseJsonArray = ", mCourseInfo.name);

                    } else if(which == 1){
                        for (int i=0;i<mCourseJsonArray.length();i++){
                            try {
                                if( ((JSONObject)(mCourseJsonArray.get(i))).getString(Data.COURSE_ID).compareTo(mCourseInfo.id) == 0){

                                    String preCourseString = mCourseJsonArray.toString();
                                    JSONArray postCourseArray  = new JSONArray();
                                    try {
                                        postCourseArray = new JSONArray(mCourseJsonArray.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    postCourseArray.remove(i);
                                    String postCourseString = postCourseArray.toString();

                                    AddCourseRequest stringRequest = new AddCourseRequest(getActivity(), NETTag.API_DELETE_COURSE, mCourseInfo.id, listener, errorListener,preCourseString,postCourseString,"");
                                    mQueue.add(stringRequest);
                                    dialog.cancel();
                                }
                            } catch (JSONException e) {
                                dialog.cancel();
                                e.printStackTrace();
                            }
                        }

                    }else {
                        dialog.cancel();
                    }
                }
            });
            alert.show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        try {
            drawCourse();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mQueue = Volley.newRequestQueue(getActivity());
        viewSchedule = inflater.inflate(R.layout.schedule, container, false);

        Tracker t = ((UniversityApp) getActivity().getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("Schedule");
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels;
        float dpWidth = displayMetrics.widthPixels;
        int timeBlockHeight = (int) (dpHeight/14);
        timeFrame = (FrameLayout) viewSchedule.findViewById(R.id.timeframe);
        int timeStartTop = (int) (40 * displayMetrics.density);
        int timeStart  = 8;


        for (int i = 1; i <= 14; i++) {

            Button btn = new Button(getActivity());

            btn.setText(Integer.toString(timeStart) + ":00");
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params1.setMargins(0,timeStartTop,0,0);

            params1.height = timeBlockHeight;

            btn.setLayoutParams(params1);
            btn.setTextSize(12);
            btn.setPadding(2,2,2,2);
            btn.setGravity(Gravity.TOP|Gravity.CENTER);
            btn.setBackgroundColor(Color.rgb(255, 255, 255));
            timeStart = timeStart + 1;

            timeStartTop = timeStartTop + timeBlockHeight;
            timeFrame.addView(btn);

        }

        try {
            drawCourse();
            monView.setOnClickListener(new newCourse_Click(monView,"MON"));
            monView.setOnTouchListener(new newCourse_Click(monView,"MON"));
            tueView.setOnClickListener(new newCourse_Click(tueView,"TUE"));
            monView.setOnTouchListener(new newCourse_Click(monView,"TUE"));
            wedView.setOnClickListener(new newCourse_Click(wedView,"WED"));
            monView.setOnTouchListener(new newCourse_Click(monView,"WED"));
            thuView.setOnClickListener(new newCourse_Click(thuView,"THU"));
            monView.setOnTouchListener(new newCourse_Click(monView,"THU"));
            friView.setOnClickListener(new newCourse_Click(friView,"FRI"));
            monView.setOnTouchListener(new newCourse_Click(monView,"FRI"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return viewSchedule;
    }
}