package com.rohmanhakim.androidreactivedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMultipleClickDemo = (Button) findViewById(R.id.btn_multiple_click_demo);
        btnMultipleClickDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MultipleClickDemoActivity.class));
            }
        });

        Button btnApiRequestDemo = (Button) findViewById(R.id.btn_api_request_demo);
        btnApiRequestDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ApiRequestDemo.class));
            }
        });
    }
}
