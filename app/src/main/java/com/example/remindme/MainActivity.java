package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            MainFragment fragment = new MainFragment();
            fm.beginTransaction().add(R.id.main_fragment_container , fragment).commit();

        }
        else {
            LoginFragment fragment = new LoginFragment();
            fm.beginTransaction().add(R.id.login_fragment_container , fragment).commit();
        }




    }
}
