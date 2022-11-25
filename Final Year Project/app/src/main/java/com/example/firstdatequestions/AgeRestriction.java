package com.example.firstdatequestions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
/*
This activity only loads when a profile with an age of less than 18 is trying to use the application
 */
public class AgeRestriction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_restriction);
    }
}