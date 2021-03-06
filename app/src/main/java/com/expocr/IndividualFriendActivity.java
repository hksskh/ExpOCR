package com.expocr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.expocr.util.ServerUtil;

import java.math.BigDecimal;
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
    private Button mSettleUpButton;
    private Button mDeleteFriendButton;

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


        mDeleteFriendButton = (Button) findViewById(R.id.delete_friend);
        mDeleteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });


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
                transaction.putExtra("email", emailView.getText());
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

    /**
     * called by internal fragments to refresh friend information and balance when refreshing fragment content list
     * @param s
     */
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