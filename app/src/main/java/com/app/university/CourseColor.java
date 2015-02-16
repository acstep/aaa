package com.app.university;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by matt on 2015/2/15.
 */
public class CourseColor {
    private JSONArray mCourseJsonArray;
    private Map<String, Boolean> courseColor = new HashMap<String, Boolean>();

    public CourseColor(String jsonCoursString) {
        try {
            mCourseJsonArray = new JSONArray(jsonCoursString);
        } catch (JSONException e) {
            mCourseJsonArray = new JSONArray();
        }
        courseColor.put("1fc868",false);
        courseColor.put("f8de41",false);
        courseColor.put("f452af",false);
        courseColor.put("8d44b6",false);
        courseColor.put("533be1",false);
        courseColor.put("6698ff",false);
        courseColor.put("62b0d6",false);
        courseColor.put("77d6e8",false);
        courseColor.put("b1cd5f",false);
        courseColor.put("427785",false);
        courseColor.put("fc93b4",false);
        courseColor.put("801f1f",false);
        courseColor.put("92deff",false);
        courseColor.put("ec713a",false);

        for (int i=0;i<mCourseJsonArray.length();i++){
            try {
                String color = ((JSONObject)mCourseJsonArray.get(i)).getString(Data.COURSE_COLOR);
                if(courseColor.containsKey(color)){
                    courseColor.put(color,true);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    public String getNewColor(){
        Iterator entries = courseColor.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            if( thisEntry.getValue() == false){
                thisEntry.setValue(true);
                return (String)thisEntry.getKey();
            }
        }
        return "";
    }

    public boolean deleteColor(String colorString){

        if(courseColor.containsKey(colorString)){
            courseColor.put(colorString,false);
        }
        return true;
    }
}
