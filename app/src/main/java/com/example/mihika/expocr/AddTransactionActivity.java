package com.example.mihika.expocr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import android.widget.Button;

import static android.view.View.VISIBLE;

public class AddTransactionActivity extends AppCompatActivity {

    private Spinner transactionKindSpinner;
    private MultiSelectionSpinner userSpinner;
    private Spinner catergorySpinner;
    private Spinner incomeOrExpenseSpinner;
    private final String TAG = "AddTransactionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        transactionKindSpinner = (Spinner)findViewById(R.id.transaction_kind_spinner);
        userSpinner = (MultiSelectionSpinner) findViewById(R.id.user_spinner);
        catergorySpinner = (Spinner) findViewById(R.id.transaction_catergory_spinner);
        incomeOrExpenseSpinner = (Spinner) findViewById(R.id.income_or_expense_spinner);
        addEntriesForSpinner();
        addListenerOnSpinnerItemSelection();

        Button add_transaction_button = (Button) findViewById(R.id.add_transaction_button);
        add_transaction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
    }

    public void sendData() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                //fake data for demo purpose
                int sender = 0;
                int receiver = 1;
                String category = "Food";
                String memo = "Chicken Dinner";
                double amount = 11.57;

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String datetime = dateFormat.format(date);

                String url = "http://10.0.2.2:8080/add";
                String requestString = "funcname=addTransaction&sender=" + sender + "&receiver=" + receiver + "&category=" + category + "&memo=" + memo + "&amount=" + amount + "&date=" + date;
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
                        Intent gotoMain = new Intent(AddTransactionActivity.this, MainActivity.class);
                        startActivity(gotoMain);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void addEntriesForSpinner() {
        List<String> list = new ArrayList<String>();
        //We use fake list here for demo
        list.add("Me");
        list.add("User 1");
        list.add("User 2");
        list.add("User 3");
        //List<String> list = getUsersFromServer();
        userSpinner.setItems(list);

        List<String> list2 = new ArrayList<String>();
        //We use fake list here for demo
        list2.add("Clothing");
        list2.add("Food");
        list2.add("Housing");
        list2.add("Salary");
        //List<String> list = getCatergoriesFromServer();
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        catergorySpinner.setAdapter(dataAdapter2);
    }

    protected String getUsersFromServer() {
        //TODO Implement download data from server
        return null;
    }

    public void addListenerOnSpinnerItemSelection() {
        transactionKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("For group")) {
                    userSpinner.setVisibility(View.VISIBLE);
                }
                else {
                    userSpinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}

