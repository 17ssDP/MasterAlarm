package com.example.masteralarm.utils;

import android.graphics.drawable.Icon;
import android.widget.ImageView;

import com.example.masteralarm.R;

public class ImageUtils {
    public static void setBackgroundImage(ImageView view){
        final String imageURL = "";
        final int imageSrc = R.mipmap.ic_launcher;
        if (view != null){
            view.setImageResource(imageSrc);
        }
    }
}
