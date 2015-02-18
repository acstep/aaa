package com.app.university;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by matt on 2015/2/17.
 */
public class Aboutme extends Fragment {
    private LinearLayout mAboutme;
    private LinearLayout mAddCourse;
    private String f;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.about_me, container, false);

        mAboutme = (LinearLayout)mView.findViewById(R.id.about_me);
        mAboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult( Intent.createChooser(intent, ""),  1);
                Log.d("Aboutme ", "pressed");
            }
        });

        mAddCourse = (LinearLayout)mView.findViewById(R.id.add_course);
        mAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivity(intent);
                Log.d("Aboutme ", "add_course pressed");
            }
        });

        return mView;
    }

    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        String selectedImagePath = "";
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == 1) {
                String selectedPath = getRealPathFromURI(getActivity(), data.getData());
                f = selectedPath;
                //FileUploadTask uploadHead = (FileUploadTask) new FileUploadTask(getActivity(),(ProgressBar)mView.findViewById(R.id.progressBar))
                //         .execute(selectedPath, "");
            }
        }
    }

    private String getRealPathFromURI(Context context,Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }






}
