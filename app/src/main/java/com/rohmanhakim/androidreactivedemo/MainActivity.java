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

        Button btnMultipleTapDemo = (Button) findViewById(R.id.btn_multiple_tap_demo);
        btnMultipleTapDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MultipleTapDemoActivity.class));
            }
        });

        Button btnRegistrationDemo = (Button) findViewById(R.id.btn_registration_demo);
        btnRegistrationDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationDemoActivity.class));
            }
        });
    }
}
