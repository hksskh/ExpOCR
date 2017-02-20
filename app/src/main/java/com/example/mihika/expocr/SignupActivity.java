package com.example.mihika.expocr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {


    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mEmailView;
    private TextView mPasswordView;
    private TextView mPasswordReEnterView;
    private Button mSignup_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirstNameView = (TextView) findViewById(R.id.First_name);
        mLastNameView = (TextView) findViewById(R.id.Last_name);
        mEmailView = (TextView) findViewById(R.id.signup_email);
        mPasswordView = (TextView) findViewById(R.id.signup_password);
        mFirstNameView = (TextView) findViewById(R.id.reenter_password);
        mSignup_btn = (Button) findViewById(R.id.sign_up_button);

        setContentView(R.layout.activity_signup);
    }

    protected void attempt_signup(View view) {

    }
}
