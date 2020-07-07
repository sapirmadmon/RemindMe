package com.example.remindme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String email, password;
    private EditText etEmail, etPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        TypeWriter tw = view.findViewById(R.id.tv);
        tw.setText("");
        tw.setCharacterDelay(250);
        tw.animateText("RemindMe");

        mAuth = FirebaseAuth.getInstance();

        etEmail = view.findViewById(R.id.email_text_view);
        etPassword = view.findViewById(R.id.password_text_view);

        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if(TextUtils.isEmpty(email)) {
                    //TODO error message
                }
                if(TextUtils.isEmpty(password)) {
                    //TODO error message
                }
                if(password.length() < 6) {
                    //TODO error message
                }

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d("LOGIN", "login successful");
                            Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
                            startActivity(mainActivityIntent);
                        }
                        else {
                            Log.d("LOGIN", "login failed");
                        }
                    }
                });
            }
        });

        TextView registerButton = view.findViewById(R.id.registration_button_link);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrationIntent = new Intent(getActivity(), RegistrationActivity.class);
                startActivity(registrationIntent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}