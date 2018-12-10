package com.example.masteralarm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.masteralarm.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.widget.Toast.makeText("test", "main activity", Toast.LENGTH_SHORT).show();
    }
}
