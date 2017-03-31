package com.example.mihika.expocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button mRequestVericodeButton = (Button) findViewById(R.id.request_vericode_button);
        mRequestVericodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                attemptRequestVericode();
            }
        });

        mEmailView = (AutoCompleteTextView) findViewById(R.id.forgot_password_email);
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
            focusView.requestFocus();
        } else {
            requestVericode();
        }
    }

    private void requestVericode() {
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                String email = mEmailView.getText().toString();
//
//                String url = "http://10.0.2.2:8000/user/login_by_email";
//                String requestString = "email=" + email + "&password=" + password;//encrypted;
//                Log.d(TAG, requestString);
//
//                try {
//                    URL wsurl = new URL(url);
//                    HttpURLConnection conn = (HttpURLConnection) wsurl.openConnection();
//                    conn.setDoInput(true);
//                    conn.setDoOutput(true);
//                    conn.setRequestMethod("POST");
//                    OutputStream os = new BufferedOutputStream(conn.getOutputStream());
//                    os.write(requestString.getBytes("UTF-8"));
//                    os.close();
//                    InputStream is = new BufferedInputStream(conn.getInputStream());
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    String response = "";
//                    while ((length = is.read(buffer)) != -1)
//                    {
//                        String temp = new String(buffer, 0, length, "UTF-8");
//                        response += temp;
//                        System.out.println(temp);
//                    }
//                    is.close();
//                    conn.disconnect();
//                    Log.d(TAG, "From server:" + response);
//                    JSONObject jsonObject = new JSONObject(response);
//                    if(jsonObject.has("warning")){
//                        String warning = jsonObject.getString("warning");
//                        Bundle bundle = new Bundle();
//                        bundle.putString("warning", warning);
//                        if(warning.startsWith("Email")){
//                            Message msg = new Message();
//                            msg.what = EMAIL_NOT_EXIST;
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//                        }else{
//                            Message msg = new Message();
//                            msg.what = PASSWORD_INCORRECT;
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//                        }
//                    }
//                    else {
//                        Intent gotoMain = new Intent(LoginActivity.this, MainActivity.class);
//                        gotoMain.putExtra("u_id", jsonObject.getInt("id"));
//                        gotoMain.putExtra("u_name", jsonObject.getString("name"));
//                        gotoMain.putExtra("u_email", jsonObject.getString("email"));
//                        startActivity(gotoMain);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
