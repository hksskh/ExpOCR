package com.example.mihika.expocr;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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
        mFirstNameView.setText("Lanxiao");
        mLastNameView = (TextView) findViewById(R.id.Last_name);
        mLastNameView.setText("Bai");
        mEmailView = (TextView) findViewById(R.id.signup_email);
        mEmailView.setText("hermitebai@outlook.com");
        mPasswordView = (TextView) findViewById(R.id.signup_password);
        mPasswordView.setText("970530blX!");
        mPasswordReEnterView = (TextView) findViewById(R.id.reenter_password);
        mPasswordReEnterView.setText("970530blX!");

    }

    private final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private String encrypt(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    private final void sendData(String username, String email, String encryptedPasswd) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String name = mFirstNameView.getText().toString() + " " + mLastNameView.getText().toString();
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                String encrypted = null;
                try {
                    encrypted = encrypt(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                String url = "http://10.0.2.2:8080/add";
                String requestString = "funcname=createUser&username=" + name + "&email=" + email + "&password=" + encrypted;
                Log.d(TAG, requestString);

                try {
                    URL wsurl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) wsurl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(requestString.getBytes());
                    os.close();
                    InputStream is = new BufferedInputStream(conn.getInputStream());
                    byte[] buffer = new byte[1024];
                    int length;
                    String response = "";
                    while ((length = is.read(buffer)) != -1)
                    {
                        String temp = new String(buffer, 0, length, "UTF-8");
                        response += temp;
                        System.out.println(temp);
                    }
                    is.close();
                    conn.disconnect();
                    Log.d(TAG, "From server:" + response);
                    if (response.equals("true")) {
                        Intent gotoMain = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(gotoMain);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private final void signup() {
        //TODO: If the email already exists, ask the user to change it
        //TODO: Else: Send data to server database to create a user

        String name = mFirstNameView.getText().toString() + " " + mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String encrypted = null;
        try {
            encrypted = encrypt(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.d(TAG, name);
        Log.d(TAG, password);
        Log.d(TAG, encrypted);
        sendData(name, email, encrypted);
    }

    protected void attempt_signup(View view) throws NoSuchAlgorithmException {
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
            /*
            if (!success) {
                Toast.makeText(getApplicationContext(), "Email or user name already used", Toast.LENGTH_SHORT).show();
            } else {
                Intent gotoMain = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(gotoMain);
            }
            */
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

