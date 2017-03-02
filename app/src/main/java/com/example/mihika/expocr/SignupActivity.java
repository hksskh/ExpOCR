package com.example.mihika.expocr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {


    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mEmailView;
    private TextView mPasswordView;
    private TextView mPasswordReEnterView;
    private final String TAG = "SignupActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirstNameView = (TextView) findViewById(R.id.First_name);
        mLastNameView = (TextView) findViewById(R.id.Last_name);
        mEmailView = (TextView) findViewById(R.id.signup_email);
        mPasswordView = (TextView) findViewById(R.id.signup_password);
        mPasswordReEnterView = (TextView) findViewById(R.id.reenter_password);

    }

    private final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private final void signup() {
        //TODO: If the email already exists, ask the user to change it
        //TODO: Else: Send data to server database to create a user
    }

    protected void attempt_signup(View view) {
        Log.d(TAG, "Called attempt_signup");

        boolean isEmailValid = isValidEmail(mEmailView.getText());

        String password = mPasswordView.getText().toString();
        boolean hasAtLeast8 = password.length() >= 8;
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasSpecial   = !password.matches("[A-Za-z0-9 ]*");

        boolean isPasswordValid = hasAtLeast8 && hasUppercase && hasLowercase && hasSpecial;
        boolean canReEnterPasswordMatch = mPasswordReEnterView.getText().toString().equals(password);

        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordReEnterView.setError(null);

        if (isEmailValid && isPasswordValid && canReEnterPasswordMatch) {
            signup();
        } else {
            
            if (!isEmailValid) {
                mEmailView.setError(getString(R.string.error_invalid_email));
            }

            if (!isPasswordValid) {
                if (!hasAtLeast8) {
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                }
                if (!hasLowercase) {
                    mPasswordView.setError(getString(R.string.error_invalid_password_no_lower));
                }
                if (!hasUppercase) {
                    mPasswordView.setError(getString(R.string.error_invalid_password_no_upper));
                }
                if (!hasSpecial) {
                    mPasswordView.setError(getString(R.string.error_invalid_password_no_special));
                }
            }

            if (!canReEnterPasswordMatch) {
                mPasswordReEnterView.setError(getString(R.string.error_cannot_match_password));
            }
        }
    }
}
