package com.example.mihika.expocr;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/** 
 * This activity displays information about an individual friend, the net balance between you and 
 * that friend, and a list of all the transaction between you and your friends. 
 */
public class IndividualFriendActivity extends AppCompatActivity implements IndividualFriendFragment.OnFragmentInteractionListener {

    private IndividualFriendFragment fragment;
    private TextView nameView;
    private TextView emailView;
    private TextView balanceView;
    private FloatingActionButton fab;
    private Handler delete_handler;

    List<Expense> data;
    private int receiver_id;

    public static final int DELETED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.individual_friend_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent inIntent = getIntent();
        receiver_id = Integer.parseInt(inIntent.getStringExtra("receiver_id"));
        nameView = ((TextView) findViewById(R.id.friend_receiver_name));
        nameView.setText(inIntent.getStringExtra("receiver_name"));
        emailView = ((TextView) findViewById(R.id.friend_receiver_email));
        emailView.setText(inIntent.getStringExtra("receiver_email"));
        BigDecimal bd = new BigDecimal(inIntent.getStringExtra("balance"));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        double bd_double = bd.doubleValue();
        if (bd_double < 0) {
            balanceView = ((TextView) findViewById(R.id.friend_receiver_net_balance));
            balanceView.setText("Net Balance: - $" + Math.abs(bd_double));
        } else {
            balanceView = ((TextView) findViewById(R.id.friend_receiver_net_balance));
            balanceView.setText("Net Balance: $" + bd_double);
        }

        if (savedInstanceState == null) {
            // Create the friend transaction fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(IndividualFriendFragment.ARG_RECEIVER_ID, receiver_id);
            fragment = new IndividualFriendFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.individual_friend_container, fragment)
                    .commit();
        }

        fab = (FloatingActionButton) findViewById(R.id.individual_friend_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transaction = new Intent(IndividualFriendActivity.this, AddTransactionActivity.class);
                transaction.putExtra("from", "IndividualFriend");
                startActivity(transaction);
            }
        });

        delete_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DELETED:
                        Intent gotoMain = new Intent(IndividualFriendActivity.this, MainActivity.class);
                        startActivity(gotoMain);
                        break;
                }
            }
        };
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.hasExtra("settle_up")) {
            fragment.refreshFragment();
        } else if (intent.hasExtra("addTransaction")) {
            fragment.refreshFragment();
        }
    }

    /**
     * Create the popup menu of settings.
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_settings, menu);
        return true;
    }

    /**
     * Perform activity based on menu item selected. If settle up is clicked, a transaction will be
     * added. If delete friend is selected, the friend will be deleted.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_individual_friend_settle_up:
                Intent goToRecordPayment = new Intent(IndividualFriendActivity.this, RecordPaymentActivity.class);
                goToRecordPayment.putExtra("u_id", MainActivity.getU_id());
                goToRecordPayment.putExtra("receiver_id", receiver_id);
                startActivity(goToRecordPayment);
                return true;
            case R.id.action_individual_friend_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(IndividualFriendActivity.this);
                builder.setTitle("Delete Friend")
                        .setMessage("Do you really want to delete this friend?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete_friend();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.expenses_list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                Expense clickedExpense = data.get(info.position);
                currentPosition = info.position;
                int transactionId = clickedExpense.getId();
                new DeleteTransactionQueryTask().execute(String.valueOf(transactionId));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }*/

    public FloatingActionButton getFab() {
        return fab;
    }

    private void delete_friend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String serverUrl = "http://" + ServerUtil.getServerAddress() + "user/delete_friend";
                String requestString = "u_id=" + receiver_id + "&my_u_id=" + MainActivity.getU_id();
                String response = ServerUtil.sendData(serverUrl, requestString, "UTF-8");
                Message msg = new Message();
                msg.what = DELETED;
                delete_handler.sendMessage(msg);
            }
        }).start();
    }

    public void refreshToolBar(String s) {
        String[] infos = s.split(",");

        if (infos.length > 2) {
            nameView.setText(infos[0]);
            emailView.setText(infos[1]);
            BigDecimal bd = new BigDecimal(infos[2]);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            double bd_double = bd.doubleValue();
            if (bd_double < 0) {
                balanceView.setText("Net Balance: - $" + Math.abs(bd_double));
            } else {
                balanceView.setText("Net Balance: $" + bd_double);
            }
        } else if (infos.length == 2) {
            BigDecimal bd = new BigDecimal(infos[1]);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            double bd_double = bd.doubleValue();
            if (bd_double < 0) {
                balanceView.setText("Net Balance: - $" + Math.abs(bd_double));
            } else {
                balanceView.setText("Net Balance: $" + bd_double);
            }
        }
    }

    @Override
    public String onFragmentRefresh() {
        return null;
    }
}