package com.example.mihika.expocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Created by briannaifft on 3/30/17.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mVericodeView;
    private final String TAG = "ForgotPasswordActivity";
    private final int EMAIL_NOT_EXIST = 1;
    private final int VERICODE_NOT_EXIST = 2;
    private final int EMAIL_EXISTS = 3;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button mRequestVericodeButton = (Button) findViewById(R.id.request_vericode_button);
        mRequestVericodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRequestVericode();
            }
        });

        Button mNextButton = (Button) findViewById(R.id.change_password_next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEnterVericode();
            }
        });

        mEmailView = (AutoCompleteTextView) findViewById(R.id.forgot_password_email);
        mVericodeView = (EditText) findViewById(R.id.vericode);

        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case EMAIL_NOT_EXIST:
                        Bundle bundle = msg.getData();
                        String warning = bundle.getString("warning");
                        mEmailView.setError(warning);
                        break;
                    case VERICODE_NOT_EXIST:
                        bundle = msg.getData();
                        warning = bundle.getString("warning");
                        mVericodeView.setError(warning);
                        break;
                    case EMAIL_EXISTS:
                        bundle = msg.getData();
                        warning = bundle.getString("success");
                        Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_LONG);

                }
            }
        };
    }

    private void attemptRequestVericode() {
        // Reset errors.
        mEmailView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            Log.d(TAG, "cancel");
            focusView.requestFocus();
        } else {
            requestVericode();
        }
    }

    private void attemptEnterVericode() {
        // Reset errors.
        mVericodeView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        String vericode = mVericodeView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(vericode)) {
            mVericodeView.setError(getString(R.string.error_field_required));
            focusView = mVericodeView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            Log.d(TAG, "cancel");
            focusView.requestFocus();
        } else {
            enterVericode();
        }

    }

    private void enterVericode() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String email = mEmailView.getText().toString();
                String vericode = mVericodeView.getText().toString();

                String url = "http://" + ServerUtil.getEmulatorAddress() + "user/check_vericode";
                String requestString = "email=" + email + "&vericode=" + vericode;
                Log.d(TAG, requestString);

                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                Log.d(TAG, "From server:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("warning")){
                        String warning = jsonObject.getString("warning");
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", warning);
                        if(warning.startsWith("Vericode")){
                            Message msg = new Message();
                            msg.what = VERICODE_NOT_EXIST;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                    else {
                        Intent changePassword = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                        changePassword.putExtra("u_email", email);
                        startActivity(changePassword);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void requestVericode() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String email = mEmailView.getText().toString();

                String url = "http://" + ServerUtil.getEmulatorAddress() + "user/request_vericode";
                String requestString = "email=" + email;
                Log.d(TAG, requestString);

                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                Log.d(TAG, "From server:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("warning")){
                        String warning = jsonObject.getString("warning");
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", warning);
                        if(warning.startsWith("Email")){
                            Message msg = new Message();
                            msg.what = EMAIL_NOT_EXIST;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString("success","Email with verication code has been sent.");

                        Message msg = new Message();
                        msg.what = EMAIL_EXISTS;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
