package com.example.mihika.expocr;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mihika.expocr.util.LoadingDialog;
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
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SignupActivity extends AppCompatActivity {

    private final int EMAIL_EXIST = 1;
    private final int USERNAME_EXIST = 2;
    private final int FAIL_TO_SEND_ACTIVATION = 3;
    private final int FINISH_LOADING = 4;

    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mEmailView;
    private TextView mPasswordView;
    private TextView mPasswordReEnterView;
    private final String TAG = "SignupActivity";
    private Handler handler;
    private Dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirstNameView = (TextView) findViewById(R.id.First_name);
        mLastNameView = (TextView) findViewById(R.id.Last_name);
        mEmailView = (TextView) findViewById(R.id.signup_email);
        mPasswordView = (TextView) findViewById(R.id.signup_password);
        mPasswordReEnterView = (TextView) findViewById(R.id.reenter_password);

        Button SignupBtn = (Button) findViewById(R.id.sign_up_button);
        SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempt_signup();
            }
        });

        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case EMAIL_EXIST:
                        Bundle bundle = msg.getData();
                        String warning = bundle.getString("warning");
                        mEmailView.setError(warning);
                        break;
                    case USERNAME_EXIST:
                        bundle = msg.getData();
                        warning = bundle.getString("warning");
                        mLastNameView.setError(warning);
                        break;
                    case FAIL_TO_SEND_ACTIVATION:
                        Toast.makeText(SignupActivity.this.getApplicationContext(), "Fail to send activation email. Please try again!", Toast.LENGTH_LONG).show();
                        break;
                    case FINISH_LOADING:
                        LoadingDialog.closeDialog(loading_dialog);
                        break;
                }
            }
        };

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
                String url = "http://" + ServerUtil.getEmulatorAddress() + "user/try_create";
                String requestString = "username=" + name + "&email=" + email + "&password=" + password;//encrypted;
                Log.d(TAG, requestString);

                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                Message msg = new Message();
                msg.what = FINISH_LOADING;
                handler.sendMessage(msg);
                Log.d(TAG, "From server:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("warning")){
                        String warning = jsonObject.getString("warning");
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", warning);
                        if(warning.startsWith("Email")){
                            msg = new Message();
                            msg.what = EMAIL_EXIST;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }else{
                            msg = new Message();
                            msg.what = USERNAME_EXIST;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                    else {
                        if(jsonObject.getInt("email_sending_status") == 0){
                            msg = new Message();
                            msg.what = FAIL_TO_SEND_ACTIVATION;
                            handler.sendMessage(msg);
                        }else{
                            Intent gotoActivate = new Intent(SignupActivity.this, LoginActivity.class);
                            gotoActivate.putExtra("signup", jsonObject.getString("activate"));
                            gotoActivate.putExtra("email", email);
                            gotoActivate.putExtra("password", password);
                            startActivity(gotoActivate);
                        }
                    }
                } catch (JSONException e) {
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
        loading_dialog = LoadingDialog.showDialog(SignupActivity.this, "Signing Up...");
        sendData(name, email, password);//encrypted);
    }

    private void attempt_signup() {
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

