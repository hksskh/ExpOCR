package com.example.mihika.expocr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by briannaifft on 4/6/17.
 */

public class RecordPaymentActivity extends AppCompatActivity {

    private int u_id;
    private int receiver_id;
    private final String TAG = "RecordPaymentActivity";
    private HashMap<String, String> usernameIdMap = new HashMap<String, String>();
    private List<String> spinnerArray =  new ArrayList<String>();
    private Spinner dropdown1;
    private Spinner dropdown2;
    private Spinner paymentDropdown;
    private EditText paymentAmount;
    private Handler handler;
    private final int SIGNAL_MESSAGE = 0;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_payment);

        u_id = getIntent().getIntExtra("u_id", 0);
        receiver_id = getIntent().getIntExtra("receiver_id", 0);
        dropdown1 = (Spinner) findViewById(R.id.payer_1);
        dropdown2 = (Spinner) findViewById(R.id.payer_2);
        paymentAmount = (EditText) findViewById(R.id.payment_amount);
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);

        paymentDropdown = (Spinner) findViewById(R.id.payment_method);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_otions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentDropdown.setAdapter(adapter);

        getNames();

        Button mRecordPaymentButton = (Button) findViewById(R.id.record_payment);
        mRecordPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRecordPayment();
            }
        });

        handler = new Handler(){
            public void handleMessage(Message message) {
                switch (message.what) {
                    case SIGNAL_MESSAGE:
                        RecordPaymentActivity.this.adapter.notifyDataSetChanged();
                        break;
                }
            }
        };

    }

    private void attemptRecordPayment() {
        // Reset errors.
        paymentAmount.setError(null);

        // Store values at the time of the login attempt.
        String amount = paymentAmount.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(amount)|| !isAmountValid(amount)) {
            paymentAmount.setError(getString(R.string.error_invalid_amount));
            focusView = paymentAmount;
            cancel = true;
            focusView.requestFocus();
        }

        if(dropdown1.getSelectedItem().toString().equals(dropdown2.getSelectedItem().toString())) {
            TextView errorText = (TextView) dropdown1.getSelectedView();
            errorText.setError("test");
            errorText.setTextColor(Color.RED);
            errorText.setText("Payer and receiver cannot match.");
            cancel = true;
        }

        if (!cancel) {
            recordPayment();
        }
    }

    private void recordPayment() {
        final String receiver = dropdown1.getSelectedItem().toString();
        String sender = dropdown2.getSelectedItem().toString();
        final String receiverId = usernameIdMap.get(receiver);
        final String senderId = usernameIdMap.get(sender);


        new Thread(new Runnable(){
            @Override
            public void run() {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String datetime = dateFormat.format(date);

                String url = "http://" + ServerUtil.getServerAddress() + "transaction/create_by_id";
                StringBuilder requestString = new StringBuilder();
                requestString.append("sender_id=").append(senderId)
                        .append("&receiver_id=").append(receiverId)
                        .append("&category=").append("Payment")
                        .append("&memo=").append(paymentDropdown.getSelectedItem().toString())
                        .append("&amount=").append(paymentAmount.getText().toString())
                        .append("&date=").append(datetime);
                Log.d(TAG, requestString.toString());
                String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");
                Log.d(TAG, "From server:" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("warning")){
                        ;
                    }else{
                        Intent gotoIndividualFriend = new Intent(RecordPaymentActivity.this, IndividualFriendActivity.class);
                        gotoIndividualFriend.putExtra("settle_up", true);
                        startActivity(gotoIndividualFriend);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean isAmountValid(String amount) {
        return amount.matches("^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*\\.[0-9]{2}");

    }

    private void getNames() {
        new Thread(new Runnable(){
            @Override
            public void run() {

                String url = "http://" + ServerUtil.getServerAddress() + "user/get_two_users";
                String requestString = "id1=" + u_id + "&id2=" + receiver_id;
                Log.d(TAG, requestString);

                String response = ServerUtil.sendData(url, requestString, "UTF-8");
                Log.d(TAG, "From server:" + response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("pk");
                        String name = jsonObject.getJSONObject("fields").getString("U_Name");
                        String email = jsonObject.getJSONObject("fields").getString("Email");
                        usernameIdMap.put((name + " (" + email + ")"), Integer.toString(id));
                        spinnerArray.add((name + " (" + email + ")"));
                    }
                    Message msg = new Message();
                    msg.what = SIGNAL_MESSAGE;
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
