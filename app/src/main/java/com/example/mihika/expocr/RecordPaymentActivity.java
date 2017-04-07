package com.example.mihika.expocr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by briannaifft on 4/6/17.
 */

public class RecordPaymentActivity extends AppCompatActivity {

    private int u_id;
    private int receiver_id;
    private final String TAG = "RecordPaymentActivity";
    private Map usernameIdMap = new HashMap();
    private List<String> spinnerArray =  new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Spinner dropdown1;
    private Spinner dropdown2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_payment);

        u_id = getIntent().getIntExtra("u_id", 0);
        receiver_id = getIntent().getIntExtra("receiver_id", 0);
        dropdown1 = (Spinner) findViewById(R.id.payer_1);
        dropdown2 = (Spinner) findViewById(R.id.payer_2);

        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);

        getNames();

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
                        usernameIdMap.put(name, id);
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
