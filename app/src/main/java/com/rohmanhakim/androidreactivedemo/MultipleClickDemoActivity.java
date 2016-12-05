package com.rohmanhakim.androidreactivedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MultipleClickDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_click_demo);
        getSupportActionBar().setTitle("Multiple Click Demo");
    }
}
