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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    private static Context mContext;
    private static RequestQueue mVolleyRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        mContext = getApplicationContext();
        mVolleyRequestQueue = Volley.newRequestQueue(mContext);

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
                                        editor.putString(Data.USER_ID,user.getString(NETTag.USER_ID));
                                        editor.putString(Data.EMAIL,user.getString(NETTag.EMAIL));
                                        editor.putString(Data.TOKEN,user.getString(NETTag.TOKEN));
                                        editor.putString(Data.UNIVERSITY,user.getString(NETTag.UNIVERSITY));
                                        editor.putString(Data.COURSE_SCHEDULE,user.getString(NETTag.SCHEDULE));
                                        editor.putString(Data.DEPARTMENT,user.getString(NETTag.DEPARTMENT));
                                        editor.putBoolean(Data.COURSE_SCHEDULE_SET, true);
                                        editor.apply();

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
                mVolleyRequestQueue.add(stringRequest);


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
