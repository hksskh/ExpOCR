package com.expocr;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.expocr.util.LoadingDialog;
import com.expocr.util.ServerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This activity is used to create accounts. It requires users to enter their first and last names,
 * email, and a password. The password when sent to the server is encrypted, and the account is only
 * created when all criteria for an account has successfully been met.
 */
public class SignupActivity extends AppCompatActivity {

    private final int EMAIL_EXIST = 1;
    private final int USERNAME_EXIST = 2;
    private final int FAIL_TO_SEND_ACTIVATION = 3;
    private final int SIGNUP_SUCCESS = 4;

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
                loading_dialog = LoadingDialog.showDialog(SignupActivity.this, "Signing up...");
                attempt_signup();
            }
        });

        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case EMAIL_EXIST:
                        LoadingDialog.closeDialog(loading_dialog);
                        Bundle bundle = msg.getData();
                        String warning = bundle.getString("warning");
                        mEmailView.setError(warning);
                        break;
                    case USERNAME_EXIST:
                        LoadingDialog.closeDialog(loading_dialog);
                        bundle = msg.getData();
                        warning = bundle.getString("warning");
                        mLastNameView.setError(warning);
                        break;
                    case FAIL_TO_SEND_ACTIVATION:
                        LoadingDialog.closeDialog(loading_dialog);
                        Toast.makeText(SignupActivity.this.getApplicationContext(), "Fail to send activation email. Please try again!", Toast.LENGTH_LONG).show();
                        break;
                    case SIGNUP_SUCCESS:
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

    /**
     * send signup request to server and jump to login page
     * @param username
     * @param email
     * @param encryptedPasswd
     */
    private final void sendData(final String username, final String email, final String password, final String encryptedPasswd) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String url = "http://" + ServerUtil.getServerAddress() + "user/try_create";
                String requestString = "username=" + username + "&email=" + email + "&password=" + encryptedPasswd;
                //Log.d(TAG, requestString);

                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                //Log.d(TAG, "From server:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("warning")){
                        String warning = jsonObject.getString("warning");
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", warning);
                        if(warning.startsWith("Email")){
                            Message msg = new Message();
                            msg.what = EMAIL_EXIST;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.what = USERNAME_EXIST;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                    else {
                        if(jsonObject.getInt("email_sending_status") == 0){
                            Message msg = new Message();
                            msg.what = FAIL_TO_SEND_ACTIVATION;
                            handler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.what = SIGNUP_SUCCESS;
                            handler.sendMessage(msg);
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
        //Log.d(TAG, name);
        //Log.d(TAG, password);
        //Log.d(TAG, encrypted);
        sendData(name, email, password, encrypted);
    }

    /**
     * check name, email and password view in signup page. if valid, proceed to send signup request to server
     */
    private void attempt_signup() {
        //Log.d(TAG, "Called attempt_signup");

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

            LoadingDialog.closeDialog(loading_dialog);//do not forget
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

