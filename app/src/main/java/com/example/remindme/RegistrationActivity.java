package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TypeWriter tw = findViewById(R.id.title_typewriter);
        tw.setText("");
        tw.setCharacterDelay(500);
        tw.animateText("Registration");
    }
}