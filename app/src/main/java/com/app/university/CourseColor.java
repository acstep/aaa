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
        courseColor.put("7B68EE",false);
        courseColor.put("0000FF",false);
        courseColor.put("778899",false);
        courseColor.put("32CD32",false);
        courseColor.put("FF1493",false);
        courseColor.put("008080",false);
        courseColor.put("FF7F50",false);
        courseColor.put("6A5ACD",false);
        courseColor.put("FF00FF",false);
        courseColor.put("32CD32",false);
        courseColor.put("FF8C00",false);
        courseColor.put("8A2BE2",false);
        courseColor.put("D2B48C",false);
        courseColor.put("4169E1",false);

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
