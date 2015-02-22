package com.app.university;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;


public class ProfileActivity extends Activity {


    private Context mContext;
    private final int IMAGE_REQUEST_CODE = 0;
    private final int CAMERA_REQUEST_CODE = 1;
    private final int RESULT_REQUEST_CODE = 2;
    private TextView myName;
    private TextView myUniversion;
    private TextView myDep;
    private TextView myAdmission;
    private TextView mySex;
    private SharedPreferences.Editor editor;
    private RequestQueue mQueue = null;


    public String getYearString(int year){
        if(year == 0){
            return "";
        }
        else{
            return String.valueOf(year);
        }
    }

    public String getSexString(int sex){
        if(sex == 1){
            return getString(R.string.profile_sex_boy);
        }
        if(sex == 2){
            return getString(R.string.profile_sex_girl);

        }
        return "";
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
                String myid = settings.getString(Data.USER_ID, null);
                editor = settings.edit();
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    JSONObject userJsonObject = new JSONObject(jsonObject.getString(NETTag.USER));
                    if(userJsonObject.has(NETTag.USER_NAME)){
                        myName.setText(userJsonObject.getString(NETTag.USER_NAME));
                        editor.putString(Data.USER_NAME,userJsonObject.getString(NETTag.USER_NAME));
                        editor.putLong(Data.MODIFY_TIME, new Date().getTime());
                        editor.putBoolean(Data.MODIFY_DIRTY, false);
                        editor.commit();
                    }
                    if(userJsonObject.has(NETTag.USER_DEPARTMENT)){
                        myDep.setText(userJsonObject.getString(NETTag.USER_DEPARTMENT));
                        editor.putString(Data.USER_DEPARTMENT,userJsonObject.getString(NETTag.USER_DEPARTMENT));
                        editor.putLong(Data.MODIFY_TIME, new Date().getTime());
                        editor.putBoolean(Data.MODIFY_DIRTY, false);
                        editor.commit();
                    }
                    if(userJsonObject.has(NETTag.USER_ADMISSION)){
                        myAdmission.setText(String.valueOf(userJsonObject.getInt(NETTag.USER_ADMISSION)));
                        editor.putString(Data.USER_ADMISSION,userJsonObject.getString(NETTag.USER_ADMISSION));
                        editor.putLong(Data.MODIFY_TIME, new Date().getTime());
                        editor.putBoolean(Data.MODIFY_DIRTY, false);
                        editor.commit();
                    }
                    if(userJsonObject.has(NETTag.USER_SEX)){
                        mySex.setText(getSexString(userJsonObject.getInt(NETTag.USER_SEX)));
                        editor.putString(Data.USER_SEX, userJsonObject.getString(NETTag.USER_SEX));
                        editor.putLong(Data.MODIFY_TIME, new Date().getTime());
                        editor.putBoolean(Data.MODIFY_DIRTY,false);
                        editor.commit();
                    }
                }
                else{
                    //Toast.makeText(mContext, R.string.login_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("AddCourseActivity", error.getMessage(), error);
            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mContext = this;
        mQueue = Volley.newRequestQueue(this);

        SharedPreferences settings = getSharedPreferences ("ID", Context.MODE_PRIVATE);
        String myid = settings.getString(Data.USER_ID, null);
        editor = settings.edit();

        myName = (TextView)findViewById(R.id.text_name);
        myName.setText(settings.getString(Data.USER_NAME, ""));

        myUniversion = (TextView)findViewById(R.id.text_university);
        myUniversion.setText(settings.getString(Data.USER_UNIVERSITY, ""));

        myDep = (TextView)findViewById(R.id.text_department);
        myDep.setText(settings.getString(Data.USER_DEPARTMENT, ""));

        myAdmission = (TextView)findViewById(R.id.text_admission);
        myAdmission.setText(getYearString(Integer.valueOf(settings.getString(Data.USER_ADMISSION, "0"))));

        mySex = (TextView)findViewById(R.id.text_sex);
        mySex.setText(getSexString(Integer.valueOf(settings.getString(Data.USER_SEX,"0"))));


        LinearLayout lMyName = (LinearLayout)findViewById(R.id.layer_name);
        lMyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(ProfileActivity.this);
                editDialog.setTitle(R.string.profile_name);

                final EditText editText = new EditText(ProfileActivity.this);
                editText.setPadding(10,30,10,30);
                editText.setText(myName.getText());
                editDialog.setView(editText);

                editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        userData udata = new userData(editText.getText().toString(), "","","","","");
                        UpdateAccountRequest stringRequest = new UpdateAccountRequest(mContext, listener, errorListener, udata);
                        mQueue.add(stringRequest);
                    }
                });
                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();

            };
        });


        LinearLayout lMyUniversion = (LinearLayout)findViewById(R.id.layer_university);
        lMyUniversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

        LinearLayout lMyDep = (LinearLayout)findViewById(R.id.layer_dep);
        lMyDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(ProfileActivity.this);
                editDialog.setTitle(R.string.profile_dep);

                final EditText editText = new EditText(ProfileActivity.this);
                editText.setPadding(10,30,10,30);
                editText.setText(myDep.getText());
                editDialog.setView(editText);

                editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        userData udata = new userData("", "",editText.getText().toString(),"","","");
                        UpdateAccountRequest stringRequest = new UpdateAccountRequest(mContext, listener, errorListener, udata);
                        mQueue.add(stringRequest);
                    }
                });
                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();

            };
        });

        LinearLayout lMyAdmission = (LinearLayout)findViewById(R.id.layer_admission);
        lMyAdmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(ProfileActivity.this);
                editDialog.setTitle(R.string.profile_admission);

                final NumberPicker yearPicker = new NumberPicker(ProfileActivity.this);
                yearPicker.setPadding(10,30,10,30);
                yearPicker.setMinValue(2010);
                yearPicker.setMaxValue(2020);
                editDialog.setView(yearPicker);

                editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        userData udata = new userData("", "","",String.valueOf(yearPicker.getValue()),"","");
                        UpdateAccountRequest stringRequest = new UpdateAccountRequest(mContext, listener, errorListener, udata);
                        mQueue.add(stringRequest);

                    }
                });
                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        });

        LinearLayout lMySex = (LinearLayout)findViewById(R.id.layer_sex);
        lMySex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String[] items = new String[] { getString(R.string.profile_sex_boy),  getString(R.string.profile_sex_girl) };
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.profile_sex))
                        .setCancelable(true)
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                userData udata = new userData("", "","","",String.valueOf(which+1),"");
                                UpdateAccountRequest stringRequest = new UpdateAccountRequest(mContext, listener, errorListener, udata);
                                mQueue.add(stringRequest);


                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });




        File folder = new File(Environment.getExternalStorageDirectory()+"/"+Data.FOLDER);
        if(!folder.exists()){
            folder.mkdirs();
        }

        ImageView imHeadPhoto = (ImageView)findViewById(R.id.head_image);
        imHeadPhoto.setImageURI(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + Data.FOLDER, Data.FINAL_FACE_FILE_NAME)));


        imHeadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               // pickImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                //pickImageIntent.setType("image/jpeg");
                //startActivityForResult(pickImageIntent, 1);
                String[] items = new String[] { getString(R.string.from_gallery),  getString(R.string.from_camera) };
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.image_from))
                        .setCancelable(true)
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File folder = new File(Environment.getExternalStorageDirectory()+ "/" + Data.FOLDER);
                                if(!folder.exists()){
                                    folder.mkdirs();
                                }
                                switch (which) {
                                    case 0:// Local Image
                                        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        pickImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                        pickImageIntent.setType("image/jpeg");
                                        startActivityForResult(pickImageIntent, IMAGE_REQUEST_CODE);
                                        break;
                                    case 1:
                                        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory()+ "/" + Data.FOLDER,Data.CROP_IMAGE_FILE_NAME)));
                                        startActivityForResult(intentFromCapture,CAMERA_REQUEST_CODE);
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

            }


        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String selectedImagePath = "";
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == IMAGE_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE ) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                if(requestCode == IMAGE_REQUEST_CODE){
                    String selectedPath = CommonUtil.getImagePath(this, data.getData());
                    String selectedPath1 = CommonUtil.getRealPathFromURI(this,data.getData());

                    if(selectedPath == null){
                        intent.setDataAndType(Uri.fromFile(new File(selectedPath1)), "image/*");
                    }
                    else{
                        intent.setDataAndType(Uri.fromFile(new File(selectedPath)), "image/*");
                    }
                }
                else{
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + Data.FOLDER, Data.CROP_IMAGE_FILE_NAME)), "image/*");;
                }


                intent.putExtra("output",Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/"+Data.FOLDER,Data.IMAGE_FILE_NAME)));
                intent.putExtra("crop", "true");

                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("return-data", true);
                intent.putExtra("noFaceDetection", true);
                startActivityForResult(intent, RESULT_REQUEST_CODE);
                //FileUploadTask uploadHead = (FileUploadTask) new FileUploadTask(getActivity(),(ProgressBar)mView.findViewById(R.id.progressBar))
                //         .execute(selectedPath, "");
            }
            if (requestCode == RESULT_REQUEST_CODE) {
                ImageView imHeadPhoto = (ImageView)findViewById(R.id.head_image);
                FileUploadTask uploadHead = (FileUploadTask) new FileUploadTask(mContext,(ProgressBar)null,imHeadPhoto,"", 1, 0,null)
                         .execute(Environment.getExternalStorageDirectory()+"/"+Data.FOLDER+"/"+Data.IMAGE_FILE_NAME,"face");


            }
        }
    }



}
