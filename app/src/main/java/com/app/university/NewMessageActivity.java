package com.app.university;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class NewMessageActivity extends Activity implements FileUploadTask.AsyncResponse {

    private Context mContext;
    private LayoutInflater mServiceContext;
    private ImageView imgAddFromCamera;
    private ImageView imgAddFromGallery;
    private final int IMAGE_REQUEST_CODE = 0;
    private final int CAMERA_REQUEST_CODE = 1;
    private ArrayList<String> imageNameList = new ArrayList<String>();
    private ArrayList<String> imageUploadNameList = new ArrayList<String>();
    private String currentFileName = "";
    private final String tmpFileName = "newImage.jpg";
    private LinearLayout mListUploadImage;
    private int mCurrentUploadIndex = 0;
    private boolean uploading = false;
    private ProgressBar mProgressBar;
    private String mGroupID;
    private int mGroupType = 0;
    private boolean mSending = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mContext = this;

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mGroupID =  bundle.getString(Data.GROUP_ID);
        mGroupType =  bundle.getInt(Data.GROUP_TYPE);

        Tracker t = ((UniversityApp)getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("Add New Message");
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        setContentView(R.layout.activity_new_message);
        mServiceContext = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListUploadImage = (LinearLayout)findViewById(R.id.list_upload_image);
        imgAddFromCamera = (ImageView)findViewById(R.id.imgbtn_add_camera);
        mProgressBar = (ProgressBar) findViewById(R.id.upload_progressBar);
        imgAddFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                SharedPreferences settings = getSharedPreferences("ID", Context.MODE_PRIVATE);
                String myid = settings.getString(Data.USER_ID, null);
                java.util.Date date= new java.util.Date();
                currentFileName = CommonUtil.getMD5(myid+ String.valueOf(date.getTime())) + ".jpg";
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory()+ "/" + Data.FOLDER, tmpFileName)));
                startActivityForResult(intentFromCapture,CAMERA_REQUEST_CODE);
            }
        });

        imgAddFromGallery = (ImageView)findViewById(R.id.imgbtn_add_gallery);
        imgAddFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences settings = getSharedPreferences("ID", Context.MODE_PRIVATE);
                String myid = settings.getString(Data.USER_ID, null);
                java.util.Date date= new java.util.Date();
                currentFileName = CommonUtil.getMD5(myid+ String.valueOf(date.getTime())) + ".jpg";
                Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                pickImageIntent.setType("image/jpeg");
                startActivityForResult(pickImageIntent, IMAGE_REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == IMAGE_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE ) {

                Bitmap originalBitmap;
                if(requestCode == IMAGE_REQUEST_CODE){
                    String selectedPath = CommonUtil.getImagePath(this, data.getData());
                    String selectedPath1 = CommonUtil.getRealPathFromURI(this,data.getData());

                    if(selectedPath == null){
                        originalBitmap = BitmapFactory.decodeFile(selectedPath1);
                    }
                    else{
                        originalBitmap = BitmapFactory.decodeFile(selectedPath);
                    }
                }
                else{
                    originalBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + Data.FOLDER + "/" + tmpFileName);
                }

                int width = originalBitmap.getWidth();
                int height = originalBitmap.getHeight();
                Matrix scaleMatrix = new Matrix();
                float scale = 1;
                if(width > height){
                    scale = (float)1000/width;
                }
                else{
                    scale = (float)1000/height;
                }

                scaleMatrix.postScale(scale, scale);

                Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, scaleMatrix, true);

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(Environment.getExternalStorageDirectory()  + "/" + Data.FOLDER + "/" + currentFileName);
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                    fos.close();
                    originalBitmap.recycle();
                    originalBitmap = null;
                    resizedBitmap.recycle();
                    resizedBitmap = null;
                    View tmpUploadImgView =  mServiceContext.inflate(R.layout.imageview_item, null);
                    ImageView tmpUploadImg =  (ImageView)tmpUploadImgView.findViewById(R.id.upload_image_item);
                    tmpUploadImg.setImageURI(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + Data.FOLDER, currentFileName)));
                    mListUploadImage.addView(tmpUploadImgView);
                    ImageView tmpDelUploadImg =  (ImageView)tmpUploadImgView.findViewById(R.id.btn_delete_upload_image_item);
                    imageNameList.add(currentFileName);
                    tmpDelUploadImg.setOnClickListener(new deleteImage_Click(tmpUploadImgView,currentFileName));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class deleteImage_Click implements View.OnClickListener {

        private View mImgView;
        private String mFileid;

        deleteImage_Click(View imgView, String fileid) {
            mImgView = imgView;
            mFileid = fileid;
        }

        public void onClick(View v) {
            final CharSequence courseOption[] = {getString(R.string.course_add)};

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle(getString(R.string.delete));
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imageNameList.remove(mFileid);
                    mListUploadImage.removeView(mImgView);
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alert.show();
        }
    }

    public void processFinish(String output){
        Log.d("NewMessageActivity = ", output);

        try {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0) {
                String filename = jsonObject.getString(NETTag.UPLOAD_FILENAME);
                imageUploadNameList.add(filename);
            }
            mCurrentUploadIndex = mCurrentUploadIndex + 1;
            if(imageNameList.size() > mCurrentUploadIndex){
                FileUploadTask uploadHead = (FileUploadTask) new FileUploadTask(mContext,mProgressBar,null,"", imageNameList.size(), mCurrentUploadIndex, this)
                        .execute(Environment.getExternalStorageDirectory()  + "/" + Data.FOLDER + "/" + imageNameList.get(mCurrentUploadIndex),"");
            }
            else{
                postEvent();
            }
        } catch (JSONException e) {
            mSending = false;
            e.printStackTrace();
        }

        //this you will received result fired from async class of onPostExecute(result) method.
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString(NETTag.RESULT).compareTo(NETTag.OK) == 0){
                    Log.d("NewMessageActivity", "success");
                    Toast.makeText(mContext, R.string.post_event_success, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    mSending = false;
                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                mSending = false;
                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            } finally {

            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mSending = false;
            Log.e("NewMessageActivity", error.getMessage(), error);
            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    };

    public void postEvent() throws JSONException {
        EditText editContent = (EditText)findViewById(R.id.edit_event_content);
        EditText editURL = (EditText)findViewById(R.id.edit_event_url);
        if(editContent.length() == 0 && imageUploadNameList.isEmpty()){
            mSending = false;
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NETTag.COURSE_EVNET_GROUPID, mGroupID);
        jsonObject.put(NETTag.GROPU_EVNET_TYPE, mGroupType);
        JSONArray imageArray = new JSONArray(imageUploadNameList);
        jsonObject.put(NETTag.COURSE_EVNET_IMAGE_LIST, imageArray);

        jsonObject.put(NETTag.COURSE_EVNET_CONTENT, editContent.getText().toString());
        jsonObject.put(NETTag.GROPU_EVNET_URL, editURL.getText().toString());

        Log.d("NewMessageActivity post string = ", jsonObject.toString());
        PostEventRequest stringRequest = new PostEventRequest(this, listener, errorListener, jsonObject.toString());
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_message_done) {
            Log.d("NewMessageActivity post string = ", "posting");
            if(mSending){
                return super.onOptionsItemSelected(item);
            }
            mSending = true;
            if(imageNameList.size() != 0){
                mCurrentUploadIndex = 0;
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                FileUploadTask uploadHead = (FileUploadTask) new FileUploadTask(mContext,mProgressBar,null,"", imageNameList.size(), 0, this)
                        .execute(Environment.getExternalStorageDirectory()  + "/" + Data.FOLDER + "/" + imageNameList.get(mCurrentUploadIndex),"");
            }
            else{
                try {
                    postEvent();
                } catch (JSONException e) {
                    mSending = false;
                    e.printStackTrace();
                }
            }

            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
