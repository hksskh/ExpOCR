package com.example.mihika.expocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by briannaifft on 4/13/17.
 */

public class AddGroupTransactionActivity extends AppCompatActivity {
    private Spinner payerSpinner;
    private MultiSelectionSpinner userSpinner;
    private Spinner categorySpinner;
    private AutoCompleteTextView amount_text;
    private AutoCompleteTextView memo_text;
    private Handler handler;
    private ArrayList<Integer> groupMemberArray;
    private final String TAG = "AddGroupTransactionActivity";
    private HashMap<String, Integer> nameIdMap = new HashMap<>();
    private final List<String> userSpinnerNames = new ArrayList<>();

    private final int SET_USER_SPINNER_ENTRIES = 1;
    private String my_name = MainActivity.getU_name();
    private String my_email = MainActivity.getU_email();

    private int g_id;
    private String g_name;

    private static final String[] amount_autos = new String[]{

    };
    private static final String[] memo_autos = new String[]{
            "Movie", "Snack", "Popcorn", "Pizza", "Grocery", "Lunch", "Dinner", "Electricity", "Utility Bills"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_transaction);

        Intent inIntent = getIntent();
        g_id = inIntent.getIntExtra("g_id", 1);
        g_name = inIntent.getStringExtra("g_name");

        payerSpinner = (Spinner) findViewById(R.id.group_transaction_payer_spinner);
        payerSpinner.setPrompt("Select payer of expense:");
        userSpinner = (MultiSelectionSpinner) findViewById(R.id.group_transaction_user_spinner);
        userSpinner.setPrompt("Select group members to include in the transaction:");
        categorySpinner = (Spinner) findViewById(R.id.group_transaction_category_spinner);

        addEntriesForSpinner();

        set_autotext_adapters();

        Button add_transaction_button = (Button) findViewById(R.id.add_group_transaction_button);
        add_transaction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSendData();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case SET_USER_SPINNER_ENTRIES:
                        userSpinner.setItems(userSpinnerNames);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddGroupTransactionActivity.this,
                                android.R.layout.simple_spinner_item, userSpinnerNames);
                        payerSpinner.setAdapter(adapter);
                        break;
                }
            }
        };
    }

    protected void addEntriesForSpinner() {
//        List<String> list = new ArrayList<String>();

        new Thread(new Runnable(){
            @Override
            public void run() {

                String url = "http://" + ServerUtil.getServerAddress() + "group/get_members";
                StringBuilder requestString = new StringBuilder();
                requestString.append("g_id=").append(g_id);

//                Log.d(TAG, requestString.toString());
                String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

                System.out.println("From server:" + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    groupMemberArray = new ArrayList<Integer>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int u_id = jsonObject.getInt("u_id");
                        groupMemberArray.add(u_id);
                    }

                    getNamesFromIds();
                    Message msg = new Message();
                    msg.what = SET_USER_SPINNER_ENTRIES;
                    handler.sendMessage(msg);

                } catch (JSONException jsex){
                    jsex.printStackTrace();
                }

            }
        }).start();

//        userSpinner.setItems(list);

    }

    private void getNamesFromIds() {
        //new Thread(new Runnable(){
            //@Override
            //public void run() {

        nameIdMap.put(my_name + " (" + my_email + ")", MainActivity.getU_id());

        userSpinnerNames.add(my_name + " (" + my_email + ")");


                for(int i =0; i< groupMemberArray.size(); i++) {
                    if(!(groupMemberArray.get(i) == MainActivity.getU_id())) {
                        String url = "http://" + ServerUtil.getServerAddress() + "user/get_user_by_id";
                        StringBuilder requestString = new StringBuilder();
                        requestString.append("id=").append(groupMemberArray.get(i));
                        String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String name = jsonObject.getJSONObject("fields").getString("U_Name");
                            String email = jsonObject.getJSONObject("fields").getString("Email");
                            nameIdMap.put(name + " (" + email + ")", groupMemberArray.get(i));
                            userSpinnerNames.add(name + " (" + email + ")");

                        } catch (JSONException jsex) {
                            jsex.printStackTrace();
                        }
                    }
                }
            //}
        //}).start();
    }

    private void set_autotext_adapters(){
        //adapters for AutoCompleteTextViews
        ArrayAdapter<String> amount_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, amount_autos);
        amount_text = (AutoCompleteTextView)
                findViewById(R.id.add_group_transaction_amount);
        amount_text.setAdapter(amount_adapter);
        ArrayAdapter<String> memo_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, memo_autos);
        memo_text = (AutoCompleteTextView)
                findViewById(R.id.add_group_transaction_memo);
        memo_text.setAdapter(memo_adapter);
    }

    public void attemptSendData() {
        boolean cancel = false;
        View focusView = null;
        String amount = amount_text.getText().toString();
        // Check for a valid password, if the user entered one.
        if(userSpinner.getSelectedIndicies().size() == 0) {
            Toast.makeText(getApplicationContext(), "Please select at least one person to split between.", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (TextUtils.isEmpty(amount)|| !isAmountValid(amount)) {
            amount_text.setError(getString(R.string.error_invalid_amount));
            focusView = amount_text;
            cancel = true;
            focusView.requestFocus();
        }

        if (!cancel) {
            sendData();
        }
    }

    public void sendData() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String datetime = dateFormat.format(date);


                List<String> selectedNames = userSpinner.getSelectedStrings();
                String payer = payerSpinner.getSelectedItem().toString();

                boolean payerIncluded = selectedNames.contains(payer);
                double amount = Double.parseDouble(amount_text.getText().toString());
                double individualAmount  = ((amount/selectedNames.size())*(-100));
                individualAmount = Math.round(individualAmount);
                individualAmount /= 100;
                double payerAmount;

                if (!payerIncluded) {
                    payerAmount = individualAmount * selectedNames.size() * (-1);
                } else {
                    payerAmount = individualAmount * (selectedNames.size()-1) * (-1);
                }

                for (int i = 0; i < selectedNames.size(); i++) {

                    int userId = nameIdMap.get(selectedNames.get(i));

                    if (!(payerIncluded && (userId == nameIdMap.get(payer)))) {
                        String url = "http://" + ServerUtil.getServerAddress() + "group/add_transaction";
                        StringBuilder requestString = new StringBuilder();
                        requestString.append("receiver_id=").append(userId)
                                .append("&group_id=").append(g_id)
                                .append("&category=").append(categorySpinner.getSelectedItem().toString())
                                .append("&memo=").append(memo_text.getText())
                                .append("&amount=")
                                .append(individualAmount)
                                .append("&date=").append(datetime);
                        System.out.println(requestString.toString());
                        String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("warning")) {
                                // handle it
                            }
                        } catch (JSONException jsex) {
                            jsex.printStackTrace();
                        }
                    }
                }


                String url = "http://" + ServerUtil.getServerAddress() + "group/add_transaction";
                StringBuilder requestString = new StringBuilder();
                requestString.append("receiver_id=").append(nameIdMap.get(payer))
                        .append("&group_id=").append(g_id)
                        .append("&category=").append(categorySpinner.getSelectedItem().toString())
                        .append("&memo=").append(memo_text.getText())
                        .append("&amount=")
                        .append(payerAmount)
                        .append("&date=").append(datetime);
                System.out.println(requestString.toString());
                String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

                System.out.println(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("warning")) {
                        // handle it
                    } else {
                        Intent gotoIndividualActivity = new Intent(AddGroupTransactionActivity.this, IndividualGroupActivity.class);
                        gotoIndividualActivity.putExtra("group_id", g_id);
                        gotoIndividualActivity.putExtra("group_name", g_name);
                        gotoIndividualActivity.putExtra("addTransaction", true);
                        startActivity(gotoIndividualActivity);
                    }
                } catch (JSONException jsex) {
                    jsex.printStackTrace();
                }


            }
        }).start();
    }

    private boolean isAmountValid(String amount) {
        return amount.matches("^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*\\.[0-9]{2}");

    }

}
