package com.example.mihika.expocr;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by briannaifft on 4/3/17.
 */

public class ChangePasswordActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mReenterPasswordView;
    private final String TAG = "ChangePasswordActivity";
    private Handler handler;
    private final int CHANGE_PWD_SUCCESS = 1;
    private String userEmail;
    private Dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button mChangePasswordButton = (Button) findViewById(R.id.change_password_button);
        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading_dialog = LoadingDialog.showDialog(ChangePasswordActivity.this, "Changing Password...");
                attemptChangePassword();
            }
        });

        mEmailView = (AutoCompleteTextView) findViewById(R.id.change_password_email);
        mPasswordView = (EditText) findViewById(R.id.change_password_password);
        mReenterPasswordView = (EditText) findViewById(R.id.change_password_reenter_password);

        userEmail = getIntent().getStringExtra("u_email");

        mEmailView.setText(userEmail);

        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case CHANGE_PWD_SUCCESS:
                        LoadingDialog.closeDialog(loading_dialog);
                        break;
                }
            }
        };

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

    private void attemptChangePassword() {
        Log.d(TAG, "Called attempt_change_password");

        boolean isEmailValid = mEmailView.getText().toString().equals(userEmail.toString());

        String password = mPasswordView.getText().toString();
        boolean hasAtLeast8 = password.length() >= 8;
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasSpecial   = !password.matches("[A-Za-z0-9 ]*");

        boolean isPasswordValid = hasAtLeast8 && hasUppercase && hasLowercase && hasSpecial;
        boolean canReEnterPasswordMatch = mReenterPasswordView.getText().toString().equals(password);

        mEmailView.setError(null);
        mPasswordView.setError(null);
        mReenterPasswordView.setError(null);

        if (isEmailValid && isPasswordValid && canReEnterPasswordMatch) {
            changePassword();
        } else {

            LoadingDialog.closeDialog(loading_dialog);//do not forget
            if (!isEmailValid) {
                mEmailView.setError(getString(R.string.error_incorrect_email));
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
                mReenterPasswordView.setError(getString(R.string.error_cannot_match_password));
            }
        }
    }


    private void changePassword() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                String url = "http://" + ServerUtil.getServerAddress() + "user/change_password";
                String requestString = "email=" + email + "&password=" + password;
                Log.d(TAG, requestString);

                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                Log.d(TAG, "From server:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("updated rows")){
                        int rowsUpdated = jsonObject.getInt("updated rows");
                        if(rowsUpdated == 1) {
                            Message msg = new Message();
                            msg.what = CHANGE_PWD_SUCCESS;
                            handler.sendMessage(msg);
                            //Toast.makeText(getApplicationContext(), "Password changed successfully. Re-login!", Toast.LENGTH_LONG).show();

                            Intent gotoLogin = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            gotoLogin.putExtra("u_email", email);
                            gotoLogin.putExtra("u_password", password);
                            gotoLogin.putExtra("forgotPassword", "");
                            startActivity(gotoLogin);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
