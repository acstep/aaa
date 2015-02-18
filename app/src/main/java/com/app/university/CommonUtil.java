package com.app.university;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by matt on 2015/2/11.
 */
public class CommonUtil {

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static JSONArray getCourseJsonArray(Context context){
        JSONArray mCourseJsonArray;
        SharedPreferences settings = context.getSharedPreferences ("ID", Context.MODE_PRIVATE);
        String jsonCoursString = settings.getString(Data.CURRENTCOURSE, "[]");
        try {
            mCourseJsonArray = new JSONArray(jsonCoursString);
        } catch (JSONException e) {
            mCourseJsonArray = new JSONArray();
        }
        return mCourseJsonArray;
    }

}
