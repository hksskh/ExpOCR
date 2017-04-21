package com.example.mihika.expocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by mihika on 4/17/17.
 */

public class AddGroupMembersActivity extends AppCompatActivity {

    private String group_name;
    private int u_id = MainActivity.getU_id();

    private MultiAutoCompleteTextView group_members;
    private final Vector friend_autos = new Vector();

    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group_members);

        group_members = (MultiAutoCompleteTextView) findViewById(R.id.group_members_emails);
        Intent intent = getIntent();
        group_name = intent.getStringExtra("GroupName");
        System.out.println("GROUP NAME: " + group_name+ "\n....................");
        u_id = MainActivity.getU_id();
        friend_autos.addAll(FriendAdapter.get_friend_email_list());


        set_autotext_adapters();


        add_button = (Button) findViewById(R.id.add_group_members);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();




            }
        });



    }


    private void set_autotext_adapters(){
        //adapter for AutoCompleteTextView
        ArrayAdapter<String> friend_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, friend_autos);
        group_members = (MultiAutoCompleteTextView) findViewById(R.id.group_members_emails);
        group_members.setAdapter(friend_adapter);
        group_members.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

    }

    public void sendData() {
        new Thread(new Runnable(){
            @Override
            public void run() {
//                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                Date date = new Date();
//                String datetime = dateFormat.format(date);

                String url = "http://" + ServerUtil.getServerAddress() + "group/create_group";
                StringBuilder requestString = new StringBuilder();
                requestString.append("u_id=").append(MainActivity.getU_id())
                        .append("&group_name=").append(group_name)
                        .append("&group_members_emails=").append(group_members.getText());
                //group_members_emails have comma separated entries. parse it to get all group members and add it to database
                //e.g. "bob@gmail.com, bill@gmail.com"

                String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

                System.out.println("From server:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("warning")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", jsonObject.getString("warning"));
                        Message msg = new Message();
                      //  msg.what = FRIEND_EMAIL_NOT_EXIST;
                        msg.setData(bundle);
                        //handler.sendMessage(msg);
                    } else {
                        Intent intent = new Intent(AddGroupMembersActivity.this, IndividualGroupActivity.class);

                        intent.putExtra("group_id", jsonObject.getInt("g_id"));
                        intent.putExtra("group_name", group_name);

                        intent.putExtra("balance", "0");
                        intent.putExtra("u_id", u_id );
                        startActivity(intent);
                    }
                } catch (JSONException jsex){
                    jsex.printStackTrace();
                }

            }
        }).start();
    }


}
