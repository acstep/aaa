package com.app.university;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        mContext = getApplicationContext();


        Button loginButton = (Button)findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                EditText edEmail = (EditText)findViewById(R.id.edit_email);
                EditText edPasswd = (EditText)findViewById(R.id.edit_passwd);

                final String emailString = edEmail.getText().toString();
                final String passwsStrint = edPasswd.getText().toString();
                if(emailString.isEmpty() || passwsStrint.isEmpty()){
                    return;
                }

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, NETTag.API_LOGIN_ACCOUNT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("CreateAccountActivity", "response -> " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                                        SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                                        JSONObject user = jsonObject.getJSONObject(NETTag.USER);
                                        SharedPreferences.Editor editor = settings.edit();
                                        if(user.has(NETTag.USER_ID)){
                                            editor.putString(Data.USER_ID,user.getString(NETTag.USER_ID));
                                        }
                                        if(user.has(NETTag.EMAIL)){
                                            editor.putString(Data.USER_EMAIL,user.getString(NETTag.EMAIL));
                                        }
                                        if(user.has(NETTag.TOKEN)){
                                            editor.putString(Data.TOKEN,user.getString(NETTag.TOKEN));
                                        }
                                        if(user.has(NETTag.USER_UNIVERSITY)){
                                            editor.putString(Data.USER_UNIVERSITY,user.getString(NETTag.USER_UNIVERSITY));
                                        }
                                        if(user.has(NETTag.SCHEDULE)){
                                            editor.putString(Data.CURRENTCOURSE,user.getString(NETTag.SCHEDULE));
                                        }
                                        if(user.has(NETTag.USER_DEPARTMENT)){
                                            editor.putString(Data.USER_DEPARTMENT,user.getString(NETTag.USER_DEPARTMENT));
                                        }
                                        if(user.has(NETTag.USER_ADMISSION)){
                                            editor.putString(Data.USER_ADMISSION,user.getString(NETTag.USER_ADMISSION));
                                        }
                                        if(user.has(NETTag.USER_SEX)){
                                            editor.putString(Data.USER_SEX,user.getString(NETTag.USER_SEX));
                                        }
                                        if(user.has(NETTag.USER_NAME)){
                                            editor.putString(Data.USER_NAME,user.getString(NETTag.USER_NAME));
                                        }

                                        editor.putBoolean(Data.COURSE_SCHEDULE_SET, true);
                                        editor.commit();

                                        Intent intent = new Intent(mContext, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                       Toast.makeText(mContext, R.string.login_error, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                    return;
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("CreateAccountActivity", error.getMessage(), error);
                                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                )
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<String, String>();
                        String token = CommonUtil.getMD5(passwsStrint);
                        map.put(NETTag.EMAIL,emailString);
                        map.put(NETTag.TOKEN, token);
                        return map;
                    }
                };
                stringRequest.setTag("LoginActivity");
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


            }
        });

        Button createButton = (Button)findViewById(R.id.btn_create_account);
        createButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



}
