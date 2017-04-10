package com.example.mihika.expocr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
    private ArrayAdapter<String> adapter;
    private Spinner dropdown1;
    private Spinner dropdown2;
    private Spinner paymentDropdown;
    private EditText paymentAmount;

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
        final String sender = dropdown1.getSelectedItem().toString();
        String receiver = dropdown2.getSelectedItem().toString();
        final String senderId = usernameIdMap.get(sender);
        final String receiverId = usernameIdMap.get(receiver);


        new Thread(new Runnable(){
            @Override
            public void run() {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String datetime = dateFormat.format(date);

                String url = "http://10.0.2.2:8000/transaction/create_by_id";
                StringBuilder requestString = new StringBuilder();
                requestString.append("sender_id=").append(senderId)
                        .append("&receiver_id=").append(receiverId)
                        .append("&category=").append("Payment")
                        .append("&memo=").append(paymentDropdown.getSelectedItem().toString())
                        .append("&amount=").append(paymentAmount.getText().toString())
                        .append("&date=").append(datetime);
                Log.d(TAG, requestString.toString());
                try {
                    URL wsurl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) wsurl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(requestString.toString().getBytes("UTF-8"));
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
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("warning")){
                        ;
                    }else{
                        Intent gotoMain = new Intent(RecordPaymentActivity.this, MainActivity.class);
                        gotoMain.putExtra("addTransaction", true);
                        startActivity(gotoMain);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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

                String url = "http://10.0.2.2:8000/user/get_two_users";
                String requestString = "id1=" + u_id + "&id2=" + receiver_id;
                Log.d(TAG, requestString);

                try {
                    URL wsurl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) wsurl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(requestString.getBytes("UTF-8"));
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
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("pk");
                        String name = jsonObject.getJSONObject("fields").getString("U_Name");
                        usernameIdMap.put(name, Integer.toString(id));
                        spinnerArray.add(name);
                    }
                    adapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
