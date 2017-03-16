package com.example.mihika.expocr;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class IndividualFriendActivity extends AppCompatActivity {

    private ListView mListView;
    private int receiver_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_friend);

        Intent inIntent = getIntent();
        receiver_id = Integer.parseInt(inIntent.getStringExtra("receiver_id"));
        ((TextView)findViewById(R.id.friend_receiver_name)).setText(inIntent.getStringExtra("receiver_name"));
        ((TextView)findViewById(R.id.friend_receiver_email)).setText(inIntent.getStringExtra("receiver_email"));
        ((TextView)findViewById(R.id.friend_receiver_net_balance)).setText("Net Balance: " + inIntent.getStringExtra("balance"));

        new TransactionBetweenQueryTask().execute();

        mListView = (ListView) findViewById(R.id.expenses_list_view);
        final ArrayList<Expense> expenseList = Expense.getRecipesFromFile("recipes.json", this);

        ExpenseAdapter adapter = new ExpenseAdapter(this, expenseList);

        TextView netBalanceText = (TextView)findViewById(R.id.net_balance);

        /*if(adapter.getNetBalance() > 0){
            netBalanceText.append(Double.toString(adapter.getNetBalance()));
            netBalanceText.setTextColor(getResources().getColor(R.color.moneyGreen));
        }
        else {
            double netBalance = adapter.getNetBalance();
            netBalance *= (-1);
            netBalanceText.append("- $".concat(Double.toString(netBalance)));
            netBalanceText.setTextColor(getResources().getColor(R.color.negativeRed));
        }*/

        mListView.setAdapter(adapter);
    }

    class TransactionBetweenQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return friend_get_transaction_between();
        }

        @Override
        protected void onPostExecute(String s){
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<Expense> data = ((ExpenseAdapter)mListView.getAdapter()).getData();
            int limit = data.size();
            data.clear();
            for(int index = 0; index < jsonArray.length() && index < limit; index++){
                JSONObject jsonObj = null;
                try {
                    jsonObj = jsonArray.getJSONObject(index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Expense expense = null;
                try {
                    expense = new Expense(jsonObj.getString("category") + ":" + jsonObj.getString("memo"),
                            Double.parseDouble(jsonObj.getString("amount")), jsonObj.getString("date"));
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data.add(expense);
            }
            ((ExpenseAdapter)mListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private String friend_get_transaction_between(){
        String serverUrl = "http://10.0.2.2:8000/transaction/get_between";
        URL url = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos;
        BufferedOutputStream bos = null;
        HttpURLConnection connection = null;
        byte[] responseBody = null;
        try {
            url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            String requestBody = "sender_id=1&receiver_id=" + receiver_id;
            os.write(requestBody.getBytes("UTF-8"));
            os.flush();
            os.close();
            InputStream is = connection.getInputStream();
            bis =  new BufferedInputStream(is);
            baos = new ByteArrayOutputStream();
            bos = new BufferedOutputStream(baos);
            byte[] response_buffer = new byte[1024];
            int length = 0;
            while((length = bis.read(response_buffer)) > 0){
                bos.write(response_buffer, 0, length);
            }
            bos.flush();
            responseBody = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        }
        String text = null;
        try {
            text = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }
}
