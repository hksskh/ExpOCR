package com.example.mihika.expocr;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mihika.expocr.util.ServerUtil;

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
    private Button mSettleUpButton;
    List<Expense> data;
    private int currentPosition;

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
        registerForContextMenu(mListView);
        final ArrayList<Expense> expenseList = new ArrayList<>();

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

        mSettleUpButton = (Button) findViewById(R.id.settle_up);
        mSettleUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToRecordPayment = new Intent(IndividualFriendActivity.this, RecordPaymentActivity.class);
                goToRecordPayment.putExtra("u_id", MainActivity.getU_id());
                goToRecordPayment.putExtra("receiver_id", receiver_id);
                startActivity(goToRecordPayment);
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.expenses_list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                Expense clickedExpense = data.get(info.position);
                currentPosition = info.position;
                int transactionId = clickedExpense.getId();
                new DeleteTransactionQueryTask().execute(String.valueOf(transactionId));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    class DeleteTransactionQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            int transactionId = Integer.parseInt(params[0]);
            return delete_transaction(transactionId);
        }

        @Override
        protected void onPostExecute(String s){
            data.remove(currentPosition);
            ((ExpenseAdapter)mListView.getAdapter()).notifyDataSetChanged();
            double netBalance = ((ExpenseAdapter)mListView.getAdapter()).getNetBalance();
            ((TextView)findViewById(R.id.friend_receiver_net_balance)).setText("Net Balance: " + netBalance);
        }
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
            data = ((ExpenseAdapter)mListView.getAdapter()).getData();
            //int limit = data.size();
            data.clear();
            for(int index = 0; index < jsonArray.length(); index++){ //&& index < limit; index++){
                JSONObject jsonObj = null;
                try {
                    jsonObj = jsonArray.getJSONObject(index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Expense expense = null;
                try {
                    expense = new Expense(jsonObj.getInt("id"), jsonObj.getString("category") + ": " + jsonObj.getString("memo"),
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
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "transaction/get_between";
        String requestBody = "sender_id=" + MainActivity.getU_id() + "&receiver_id=" + receiver_id;

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        return text;
    }

    private String delete_transaction(int transactionId) {
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "transaction/delete_by_id";
        String requestBody = "tid=" + String.valueOf(transactionId);

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        return text;
    }
}
