package com.app.university;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class ImageViewerActivity extends Activity {
    String mRemoteImageID;
    ImageLoader mImageLoader;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Tracker t = ((UniversityApp)getApplication()).getTracker(UniversityApp.TrackerName.APP_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("View Image");
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        mImageView = (ZoomImageView) findViewById(R.id.remote_image_viewer);


        mRemoteImageID =  bundle.getString(Data.REMOTE_IMAGE_VIEWER_ID);
        mImageLoader =  MySingleton.getInstance(getApplicationContext()).getImageLoader();
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mImageView, R.drawable.abc_list_divider_mtrl_alpha, R.drawable.abc_list_divider_mtrl_alpha);
        mImageLoader.get(NETTag.API_GET_FEEDIMAGE+"?id="+ mRemoteImageID, listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
