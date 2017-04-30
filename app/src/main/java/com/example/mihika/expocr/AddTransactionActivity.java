package com.example.mihika.expocr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Locale;
import java.util.Vector;

import android.widget.Button;
import android.widget.Toast;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.VISIBLE;

/**
 * This activity adds a transaction between two users.
 */
public class AddTransactionActivity extends AppCompatActivity{

    private final int FRIEND_EMAIL_NOT_EXIST = 1;

    private Spinner categorySpinner;
    private Spinner split_owe_owed;
    private AutoCompleteTextView email_text;
    private AutoCompleteTextView amount_text;
    private AutoCompleteTextView memo_text;
    Button add_transaction_from_receipt_btn;
    private Handler handler;
    private final String TAG = "AddTransactionActivity";
    private String fromActivity;

    private JSONArray receipt_list = null;
    private final List<String> friend_autos = new ArrayList<>();
    private List<String> group_autos = new ArrayList<>();
    private static final String[] amount_autos = new String[]{
            "10", "20"
    };
    private static final String[] memo_autos = new String[]{
            "Movie", "Snack", "Popcorn", "Pizza", "Grocery", "Lunch", "Dinner", "Electricity", "Utility Bills"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Intent inIntent = getIntent();
        if (inIntent.hasExtra("receipt_list")){
            String receipt_list_string = inIntent.getStringExtra("receipt_list");
            try {
                receipt_list = new JSONArray(receipt_list_string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        friend_autos.addAll(FriendAdapter.get_friend_email_list());
        if (inIntent.hasExtra("from")) {
            fromActivity = inIntent.getStringExtra("from");
        } else {
            fromActivity = "Main";
        }

        categorySpinner = (Spinner) findViewById(R.id.transaction_category_spinner);
        split_owe_owed = (Spinner) findViewById(R.id.split_spinner);
        addListenerOnSpinnerItemSelection();
        set_autotext_adapters();

        Button add_transaction_button = (Button) findViewById(R.id.add_transaction_button);
        add_transaction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                sendData();
            }
        });

        add_transaction_from_receipt_btn = (Button) findViewById(R.id.add_transaction_from_receipt);
        if (receipt_list != null && receipt_list.length() > 0){
            add_transaction_from_receipt_btn.setText("Split Receipt");
        }
        add_transaction_from_receipt_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (receipt_list == null || receipt_list.length() == 0) {
                    Intent intent = new Intent(AddTransactionActivity.this, PhotoCaptureActivity.class);
                    startActivity(intent);
                } else {
                    if (email_text.getText() == null || email_text.getText().length() == 0) {
                        email_text.setError("Empty Email!");
                        return;
                    }
                    Intent intent = new Intent(AddTransactionActivity.this, AddTransactionReceiptItemListActivity.class);
                    intent.putExtra("receipt_list", receipt_list.toString());
                    intent.putExtra("friend_list", new String[]{MainActivity.getU_email(), email_text.getText().toString()});
                    startActivity(intent);
                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case FRIEND_EMAIL_NOT_EXIST:
                        Bundle bundle = msg.getData();
                        email_text.setError(bundle.getString("warning"));
                        break;
                }
            }
        };
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.hasExtra("amount")) {
            amount_text.setText(String.valueOf(intent.getDoubleExtra("amount", 0.0)));
        } else if (intent.hasExtra("receipt_list")) {
            String receipt_list_string = intent.getStringExtra("receipt_list");
            try {
                receipt_list = new JSONArray(receipt_list_string);
                if (receipt_list.length() > 0) {
                    add_transaction_from_receipt_btn.setText("Split Receipt");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Splits amount of transaction based on user preference and then sends all necessary data to
     * the server to add the transaction.
     */
    public void sendData() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String datetime = dateFormat.format(date);

                String url = "http://" + ServerUtil.getServerAddress() + "transaction/create_by_email";
                StringBuilder requestString = new StringBuilder();

                Double amt =  Math.abs(Double.parseDouble(amount_text.getText().toString()));
                Double amt_half = amt/2;
                System.out.print(amt);
                System.out.print(amt_half);

                String category = categorySpinner.getSelectedItem().toString();

                if(categorySpinner.getSelectedItem().toString().equals(getResources().getString(R.string.select_category)))
                    category = getResources().getString(R.string.category_general);

                if(split_owe_owed.getSelectedItem().toString().equals(getResources().getString(R.string.owe_full))) {

                    requestString.append("sender_id=").append(MainActivity.getU_id())
                            .append("&receiver_email=").append(email_text.getText())
                            .append("&category=").append(category)
                            .append("&memo=").append(memo_text.getText())
                            .append("&am_I_sender=").append("yes")
                            .append("&amount=").append(amt);
                }
                else if (split_owe_owed.getSelectedItem().toString().equals(getResources().getString(R.string.owe_share))){
                    requestString.append("sender_id=").append(MainActivity.getU_id())
                            .append("&receiver_email=").append(email_text.getText())
                            .append("&category=").append(category)
                            .append("&memo=").append(memo_text.getText())
                            .append("&am_I_sender=").append("yes")
                            .append("&amount=").append(amt_half);
                }
                else if (split_owe_owed.getSelectedItem().toString().equals(getResources().getString(R.string.owed_full))) {
                        requestString.append("sender_id=").append(MainActivity.getU_id())
                                .append("&receiver_email=").append(email_text.getText())
                                .append("&category=").append(category)
                                .append("&memo=").append(memo_text.getText())
                                .append("&am_I_sender=").append("no")
                                .append("&amount=").append(amt);
                    }
                else {  requestString.append("sender_id=").append(MainActivity.getU_id())
                                   .append("&receiver_email=").append(email_text.getText())
                                   .append("&category=").append(category)
                                   .append("&memo=").append(memo_text.getText())
                                   .append("&am_I_sender=").append("no")
                                   .append("&amount=").append(amt_half);

                }

                requestString.append("&date=").append(datetime);
                        //.append(Math.abs(Double.parseDouble(amount_text.getText().toString())))

                Log.d(TAG, requestString.toString());
                String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

                Log.d(TAG, "From server:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("warning")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", jsonObject.getString("warning"));
                        Message msg = new Message();
                        msg.what = FRIEND_EMAIL_NOT_EXIST;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        Intent intent = new Intent();
                        if (fromActivity.equals("IndividualFriend")) {
                            intent.setClass(AddTransactionActivity.this, IndividualFriendActivity.class);
                        } else if (fromActivity.equals("Main")) {
                            intent.setClass(AddTransactionActivity.this, MainActivity.class);
                        }
                        intent.putExtra("addTransaction", true);
                        startActivity(intent);
                    }
                } catch (JSONException jsex){
                    jsex.printStackTrace();
                }

            }
        }).start();
    }

    private void set_autotext_adapters(){
        //adapters for AutoCompleteTextViews
        ArrayAdapter<String> friend_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, friend_autos);
        email_text = (AutoCompleteTextView)
                findViewById(R.id.add_transaction_name);
        email_text.setAdapter(friend_adapter);
        ArrayAdapter<String> amount_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, amount_autos);
        amount_text = (AutoCompleteTextView)
                findViewById(R.id.add_transaction_amount);
        amount_text.setAdapter(amount_adapter);
        ArrayAdapter<String> memo_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, memo_autos);
        memo_text = (AutoCompleteTextView)
                findViewById(R.id.add_transaction_memo);
        memo_text.setAdapter(memo_adapter);
    }

    protected void addEntriesForSpinner() {
//        List<String> list = new ArrayList<String>();
//        //We use fake list here for demo
//        list.add("Me");
//        list.add("User 1");
//        list.add("User 2");
//        list.add("User 3");
//        List<String> list = getUsersFromServer();
//        userSpinner.setItems(list);
//
//        List<String> list2 = new ArrayList<String>();
//        //We use fake list here for demo
//        list2.add("Clothing");
//
//        list2.add("General");
//        list2.addGeneralusing");
//        list2.add("Salary");
//        List<String> list = getCatergoriesFromServer();
//
//        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
//        categorySpinner.setAdapter(dataAdapter2);
//
//        List<String> list3 = new ArrayList<>();
//        list3.add("For Individual");
//        list3.add("For Group");
//        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list3);
//        transactionKindSpinner.setAdapter(dataAdapter3);
    }

    protected String getUsersFromServer() {
        //TODO Implement download data from server
        return null;
    }

    public void addListenerOnSpinnerItemSelection() {
//        transactionKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getItemAtPosition(position).toString().equals("For group")) {
//                   // userSpinner.setVisibility(View.VISIBLE);
//                    email_text.setVisibility(View.GONE);
//                }
//                else {
//                    //userSpinner.setVisibility(View.INVISIBLE);
//                    email_text.setVisibility(View.VISIBLE);
//                }
//            }

//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }
}

