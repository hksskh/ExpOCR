package com.example.mihika.expocr;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * Created by mihika on 4/8/17.
 */
public class IndividualGroupActivity extends AppCompatActivity {

    private ListView mListView;
    private int receiver_id;
    private int g_id;
    private String g_name;
    private Button mAddTransactionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_group);

        Intent inIntent = getIntent();
        g_id = Integer.parseInt(inIntent.getStringExtra("group_id"));
        g_name = inIntent.getStringExtra("group_name");
        //receiver_id = Integer.parseInt(inIntent.getStringExtra("receiver_id"));
        ((TextView)findViewById(R.id.group_name)).setText(inIntent.getStringExtra("group_name"));

        ((TextView)findViewById(R.id.net_balance)).setText("Net Balance: " + inIntent.getStringExtra("balance"));

        mAddTransactionButton = (Button) findViewById(R.id.add_group_transaction);
        mAddTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddGroupTransaction = new Intent(IndividualGroupActivity.this, AddGroupTransactionActivity.class);
                goToAddGroupTransaction.putExtra("g_id", g_id);
                goToAddGroupTransaction.putExtra("g_name", g_name);
                startActivity(goToAddGroupTransaction);
            }
        });

        Button settle_up_btn = (Button) findViewById(R.id.settle_up_individual_grp);
        settle_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualGroupActivity.this, GroupBalanceActivity.class);
                startActivity(intent);
            }
        });

        new IndividualGroupActivity.TransactionBetweenQueryTask().execute();

        mListView = (ListView) findViewById(R.id.expenses_list_view);

        final ArrayList<Expense> expenseList = new ArrayList<>();//Expense.getRecipesFromFile("recipes.json", this);
        ExpenseAdapter adapter = new ExpenseAdapter(this, expenseList);

        /*TextView netBalanceText = (TextView)findViewById(R.id.net_balance);

        if(adapter.getNetBalance() > 0){
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

            String ret=group_get_transaction_list_for(getIntent().getStringExtra("group_id"), MainActivity.getU_id());
            JSONArray rawTransactionsArray=null;
            JSONArray transactionsArray=new JSONArray();
            int limit=10;
            try {
                rawTransactionsArray = new JSONArray(ret);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int i=0;i<rawTransactionsArray.length() && i < limit; i++)
            {
                JSONObject jsonObj=null;
                try {
                    jsonObj = rawTransactionsArray.getJSONObject(i);
                    transactionsArray.put(jsonObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            return transactionsArray.toString();
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
            //int limit = data.size();
            data.clear();
            for(int index = 0; index < jsonArray.length(); index++){ //&& index < limit; index++){
                JSONObject jsonObj = null;
                try {
                    jsonObj=jsonArray.getJSONObject(index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Expense expense = null;
                try {
                    expense = new Expense(jsonObj.getString("category") + ":" + jsonObj.getString("memo"),
                            Double.parseDouble(jsonObj.getString("amount")), jsonObj.getString("date"));
                    //TODO can also fetch people involved
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

    private String group_get_transaction_list_for(String g_id, int u_id){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "group/get_transactions";
        String requestBody = "g_id="+g_id + "&u_id=" + u_id;

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        return text;
    }

    private String group_get_transactions_by_t_id(int t_id){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "transaction/get_transactions_by_t_id";
        String requestBody = "t_id="+t_id;

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        return text;
    }
}
