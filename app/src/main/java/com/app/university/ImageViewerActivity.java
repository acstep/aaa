package com.app.university;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;


public class ImageViewerActivity extends Activity {
    String mRemoteImageID;
    ImageLoader mImageLoader;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mImageView = (ZoomImageView) findViewById(R.id.remote_image_viewer);

        mRemoteImageID =  bundle.getString(Data.REMOTE_IMAGE_VIEWER_ID);
        mImageLoader =  MySingleton.getInstance(getApplicationContext()).getImageLoader();
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mImageView, R.drawable.abc_list_divider_mtrl_alpha, R.drawable.abc_list_divider_mtrl_alpha);
        mImageLoader.get(NETTag.API_GET_FEEDIMAGE+"?id="+ mRemoteImageID, listener);
    }



}
