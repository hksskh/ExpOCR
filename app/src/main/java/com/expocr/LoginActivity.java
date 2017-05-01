package com.expocr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

import com.expocr.util.ServerUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
//Todo: Why does LoginActivity implement LoaderCallbacks<Cursor> ?
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private final int EMAIL_NOT_EXIST = 1;
    private final int PASSWORD_INCORRECT = 2;
    private final int LOGIN_SUCCESS = 3;
    private final int SERVER_ERROR = 4;

    /**
     * A dummy authentication store containing known user names and passwords.
     */
    private List<String> DUMMY_CREDENTIALS = new ArrayList<>();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Handler handler;
    private ArrayAdapter<String> emailAdapter;

    private final String TAG = "LoginActivity";

    private LoginButton loginButton;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();


        setContentView(R.layout.activity_login);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email");
        //Todo: Connect Facebook login to System Login
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("key hash: " + FacebookSdk.getApplicationSignature(getApplicationContext()));
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    doFaceBookLogin(email, name);
                                } catch (JSONException jsonex) {
                                    jsonex.printStackTrace();
                                }
                            }
                        }
                        );
                Bundle bundle = new Bundle();
                bundle.putString("fields", "name, email");
                request.setParameters(bundle);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Facebook login cancelled!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "Errors happen in Facebook Login!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTosignup();
            }
        });

        Button mForgotPasswordButton = (Button) findViewById(R.id.forgot_password);
        mForgotPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case EMAIL_NOT_EXIST:
                        Bundle bundle = msg.getData();
                        String warning = bundle.getString("warning");
                        mEmailView.setError(warning);
                        break;
                    case PASSWORD_INCORRECT:
                        bundle = msg.getData();
                        warning = bundle.getString("warning");
                        mPasswordView.setError(warning);
                        break;
                    case LOGIN_SUCCESS:
                        break;
                    case SERVER_ERROR:
                        Toast.makeText(LoginActivity.this, "Server error occurs when trying to login with FaceBook!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

    }

    /**
     * handle user login through facebook
     * @param email
     * @param name
     */
    private void doFaceBookLogin(final String email, final String name){
        new Thread(new Runnable(){
            @Override
            public void run() {
                String url = "http://" + ServerUtil.getServerAddress() + "user/create_facebook_user";
                String requestString = "email=" + email + "&username=" + name;
                //Log.d(TAG, requestString);
                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                //Log.d(TAG, "From server:" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!(jsonObject.has("warning") || jsonObject.has("email"))){
                        Message msg = new Message();
                        msg.what = SERVER_ERROR;
                        handler.sendMessage(msg);
                        return;
                    }
                    assert jsonObject.has("warning") || (jsonObject.getString("email").equals(email));

                    url = "http://" + ServerUtil.getServerAddress() + "user/login_with_facebook";
                    requestString = "email=" + email;
                    //Log.d(TAG, requestString);
                    response = ServerUtil.sendData(url, requestString, "UTF-8");
                    //Log.d(TAG, "From server:" + response);
                    jsonObject = new JSONObject(response);
                    String u_email = jsonObject.getString("email");
                    addEmailToDummyCredentials(u_email);
                    Intent gotoMain = new Intent(LoginActivity.this, MainActivity.class);
                    gotoMain.putExtra("u_id", jsonObject.getInt("id"));
                    gotoMain.putExtra("u_name", jsonObject.getString("name"));
                    gotoMain.putExtra("u_email", u_email);
                    startActivity(gotoMain);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loginTosignup() {
        Intent iSignup = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(iSignup);
    }

    private void forgotPassword() {
        Intent iRequestVericode = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(iRequestVericode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);

        if(intent.hasExtra("signup")){
            mEmailView.setText(intent.getStringExtra("email"));
            mPasswordView.setText(intent.getStringExtra("password"));
            Toast.makeText(getApplicationContext(), intent.getStringExtra("signup"), Toast.LENGTH_LONG).show();
        }
        else if(intent.hasExtra("forgotPassword")) {
            mEmailView.setText(intent.getStringExtra("u_email"));
            mPasswordView.setText(intent.getStringExtra("u_password"));

        }
    }

    /**
     * set up user email local cache
     */
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        File userListFile = new File(getExternalCacheDir(), "user.list");
        try {
            if(!userListFile.exists()){
                userListFile.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(userListFile));
            String tempLine = null;
            while((tempLine = br.readLine()) != null){
                DUMMY_CREDENTIALS.add(tempLine);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addEmailsToAutoComplete();

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * dynamic request for contact access permission
     * @return
     */
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
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
     * send login request to server. if succeed, jump to MainActivity
     */
    private void login() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                String encrypted = null;
                try {
                    encrypted = encrypt(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                String url = "http://" + ServerUtil.getServerAddress() + "user/login_by_email";
                String requestString = "email=" + email + "&password=" + password;//encrypted;
                //Log.d(TAG, requestString);
                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                //Log.d(TAG, "From server:" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("warning")){
                        String warning = jsonObject.getString("warning");
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", warning);
                        isEmailorPasswordValid(warning, bundle);
                    }
                    else {
                        Message msg = new Message();
                        msg.what = LOGIN_SUCCESS;
                        handler.sendMessage(msg);

                        String u_email = jsonObject.getString("email");
                        addEmailToDummyCredentials(u_email);

                        Intent gotoMain = new Intent(LoginActivity.this, MainActivity.class);
                        gotoMain.putExtra("u_id", jsonObject.getInt("id"));
                        gotoMain.putExtra("u_name", jsonObject.getString("name"));
                        gotoMain.putExtra("u_email", u_email);
                        startActivity(gotoMain);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addEmailToDummyCredentials(String u_email) throws IOException {
        if(!DUMMY_CREDENTIALS.contains(u_email)){
            DUMMY_CREDENTIALS.add(u_email);
            emailAdapter.add(u_email);
            FileWriter fw = new FileWriter(new File(getExternalCacheDir(), "user.list"), true);
            fw.write(u_email + System.getProperty("line.separator"));
            fw.close();
        }
    }

    private void isEmailorPasswordValid(String warning, Bundle bundle) {
        if(warning.startsWith("Email")){
            Message msg = new Message();
            msg.what = EMAIL_NOT_EXIST;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }else{
            Message msg = new Message();
            msg.what = PASSWORD_INCORRECT;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

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
            login();
        }
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    //Todo: what is this function used for ?
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        //addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete() {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        emailAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, DUMMY_CREDENTIALS);

        mEmailView.setAdapter(emailAdapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}
