package com.app.university;

import android.content.Intent;
import android.os.Bundle;
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
    private static final String IMAGE_FILE_NAME = "face.jpg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.about_me, container, false);

        mAboutme = (LinearLayout)mView.findViewById(R.id.about_me);
        mAboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //pickImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                //pickImageIntent.setType("image/jpeg");

                //pickImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriWhereToStore);

                //startActivityForResult(pickImageIntent, 1);
                //Intent intent = new Intent();
                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult( Intent.createChooser(intent, ""),  1);
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
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

    }





}
