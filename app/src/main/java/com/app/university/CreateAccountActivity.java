package com.app.university;

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


public class CreateAccountActivity extends Activity {

    private static Context mContext;
    private static RequestQueue mVolleyRequestQueue;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mContext = getApplicationContext();
        mVolleyRequestQueue = Volley.newRequestQueue(mContext);
        Button createButton = (Button)findViewById(R.id.btn_createaccount);


        createButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText edEmail = (EditText)findViewById(R.id.editEmail);
                EditText edPasswd = (EditText)findViewById(R.id.editPasswd);
                EditText edPasswdConf = (EditText)findViewById(R.id.editPasswdConfirm);
                final String emailString = edEmail.getText().toString().toLowerCase();
                final String passwdString = edPasswd.getText().toString().toLowerCase();
                String passwdConfString = edPasswdConf.getText().toString().toLowerCase();

                if(emailString.isEmpty() || passwdString.isEmpty() || passwdConfString.isEmpty()){
                    return;
                }

                if(passwdString.compareTo(passwdConfString) != 0){
                    Toast.makeText(mContext, R.string.passwd_not_match, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(emailString.endsWith("ntu.edu.tw") == false){
                    Toast.makeText(mContext, R.string.email_incorrent, Toast.LENGTH_SHORT).show();
                    return;
                }

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, NETTag.API_CREATE_ACCOUNT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("CreateAccountActivity", "response -> " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                                    SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString(Data.USER_ID,jsonObject.getString(NETTag.USER_ID));
                                    editor.putString(Data.EMAIL,jsonObject.getString(NETTag.EMAIL));
                                    editor.putString(Data.TOKEN,jsonObject.getString(NETTag.TOKEN));
                                    editor.putString(Data.UNIVERSITY,jsonObject.getString(NETTag.UNIVERSITY));
                                    editor.putString(Data.COURSE_SCHEDULE,"");
                                    editor.putBoolean(Data.COURSE_SCHEDULE_SET, false);
                                    editor.apply();

                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.ERROR_USER_EXIST) == 0){
                                        Toast.makeText(mContext, R.string.user_exist, Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

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

                        map.put(NETTag.EMAIL,emailString);
                        map.put(NETTag.PASSWD, passwdString);

                        //SharedPreferences prefs = getSharedPreferences(mContext);

                        return map;
                    }
                };
                stringRequest.setTag("CreateAccountActivity");
                mVolleyRequestQueue.add(stringRequest);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
