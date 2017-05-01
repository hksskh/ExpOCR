package com.example.mihika.expocr;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * This activity shows an individual group's name, the net balance between the user and the group,
 * and a list of the group's transactions. Options to settle up, add a group transaction, or change
 * the group settings are present as well.
 */
public class IndividualGroupActivity extends AppCompatActivity implements IndividualGroupFragment.OnFragmentInteractionListener {

    private IndividualGroupFragment fragment;

    private int g_id;
    private String g_name;

    private TextView nameView;
    private TextView balanceView;
    private FloatingActionButton fab;

    private Button mBalanceButton;
    private Button mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.individual_group_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent inIntent = getIntent();
        g_id = Integer.parseInt(inIntent.getStringExtra("group_id"));
        g_name = inIntent.getStringExtra("group_name");
        nameView = ((TextView)findViewById(R.id.individual_group_name));
        nameView.setText(g_name);

        balanceView = ((TextView) findViewById(R.id.individual_group_net_balance));
        BigDecimal bd = new BigDecimal(inIntent.getStringExtra("balance"));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        double bd_double = bd.doubleValue();
        if (bd_double < 0) {
            balanceView.setText("Net Balance: - $" + Math.abs(bd_double));
        } else {
            balanceView.setText("Net Balance: $" + bd_double);
        }

        if (savedInstanceState == null) {
            // Create the group transaction fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(IndividualGroupFragment.ARG_GROUP_ID, g_id);
            fragment = new IndividualGroupFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.individual_group_container, fragment)
                    .commit();
        }

        mBalanceButton = (Button) findViewById(R.id.balance_individual_grp);
        mBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualGroupActivity.this, GroupBalanceActivity.class);
                intent.putExtra("g_id", g_id);
                startActivity(intent);

            }
        });

        mSettingsButton = (Button) findViewById(R.id.individual_grp_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualGroupActivity.this, GroupSettingsActivity.class);
                intent.putExtra("g_id", g_id);
                intent.putExtra("g_name", g_name);
                startActivity(intent);

            }
        });
        fab = (FloatingActionButton) findViewById(R.id.individual_group_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddGroupTransaction = new Intent(IndividualGroupActivity.this, AddGroupTransactionActivity.class);
                goToAddGroupTransaction.putExtra("g_id", g_id);
                goToAddGroupTransaction.putExtra("g_name", g_name);
                startActivity(goToAddGroupTransaction);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.hasExtra("new_group_name")) {
            g_name = intent.getStringExtra("new_group_name");
            ((TextView)findViewById(R.id.group_name)).setText(g_name);
        } else if (intent.hasExtra("addTransaction")) {
            fragment.refreshFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_individual_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_individual_group_balance:
                Intent intent = new Intent(IndividualGroupActivity.this, GroupBalanceActivity.class);
                intent.putExtra("g_id", g_id);
                startActivity(intent);
                return true;
            case R.id.action_individual_group_settings:
                intent = new Intent(IndividualGroupActivity.this, GroupSettingsActivity.class);
                intent.putExtra("g_id", g_id);
                intent.putExtra("g_name", g_name);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.expenses_list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }*/

    /*@Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                Expense clickedExpense = data.get(info.position);
                currentPosition = info.position;
                Calendar transactionDate = clickedExpense.getDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                String transactionDateString = dateFormat.format(transactionDate.getTime());
                new IndividualGroupActivity.DeleteTransactionQueryTask().execute(transactionDateString);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }*/

    public FloatingActionButton getFab() {
        return fab;
    }

    /**
     * handle refreshing request of group name and net balance from invidual group fragment/adapter
     * @param s
     */
    public void refreshToolBar(String s) {
        String[] infos = s.split(",");

        if (infos.length == 2) {
            String group_name = infos[0].trim();
            if (group_name.length() > 0) {
                nameView.setText(group_name);
            }

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