package com.app.university;

import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

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

    class CourseInfo{
        String id = "";
        String name = "";
        String teacher = "";
        String color = "";
        String timeString = "";
        String time = "";
    }

    public void drawCourse() throws JSONException {
        String id = "";
        String name = "";
        String teacher = "";
        String color = "";
        String timeString = "";
        String time = "";



        for (int i=0;i<mCourseJsonArray.length();i++){
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.id = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_ID);
            courseInfo.name = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_NAME);
            courseInfo.teacher = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_TEACHER);
            courseInfo.color = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_COLOR);
            courseInfo.timeString = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_TIME);
            Integer index = 0;
            FrameLayout dayFrame;

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            int timeStartTop = (int) (40 * displayMetrics.density);
            float dpHeight = displayMetrics.heightPixels;
            int timeBlockHeight = (int) (dpHeight/14);

            while(index+15 <= courseInfo.timeString.length()){
                time = courseInfo.timeString.substring(index);
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                String day = time.substring(0,3);
                int startHR = Integer.valueOf(time.substring(4, 6));
                int startMin = Integer.valueOf(time.substring(7, 9));
                int endHR = Integer.valueOf(time.substring(10, 12));
                int endMin = Integer.valueOf(time.substring(13, 15));
                int startPos = timeStartTop+(startHR-8)*timeBlockHeight + (int)((float)(startMin/60)*timeBlockHeight);
                int endPos = timeStartTop+(endHR-8)*timeBlockHeight + (int)((float)(endMin/60)*timeBlockHeight);
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


    class courseButton_Click implements View.OnClickListener {

        private Button mbutton;
        private CourseInfo mCourseInf;


        courseButton_Click(Button button, CourseInfo courseInfo) {

            mbutton = button;
            mCourseInf = courseInfo;
        }
        public void onClick(View v) {
            Log.d("Schedule mCourseJsonArray = ", mCourseInf.name);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        SharedPreferences settings = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
        String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
        try {
            mCourseJsonArray = new JSONArray(jsonCoursString);
        } catch (JSONException e) {
            mCourseJsonArray = new JSONArray();
        }
        Log.d("Schedule mCourseJsonArray = ", mCourseJsonArray.toString());

        viewSchedule = inflater.inflate(R.layout.schedule, container, false);

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
        monView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_mon);
        tueView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_tue);
        wedView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_wed);
        thuView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_thu);
        friView =  (FrameLayout)viewSchedule.findViewById(R.id.frame_fri);
        try {
            drawCourse();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return viewSchedule;
    }
}