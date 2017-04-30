package com.example.mihika.expocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Class for adding members to a group when creating a new group. Provides a drop down list of
 * friends' emails to select from and adds those emails selected to become group members when the
 * "FINISH" button is clicked.
 */
public class AddGroupMembersActivity extends AppCompatActivity{

    private String group_name;
    private int u_id = MainActivity.getU_id();

    private MultiAutoCompleteTextView group_members;
    private final Vector friend_autos = new Vector();

    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
                sendGroupMemberData();
            }
        });
    }

    // Adapter for AutoCompleteTextView. Autocompletes a friend's email if it exists once you start
    private void set_autotext_adapters(){
        ArrayAdapter<String> friend_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, friend_autos);
        group_members = (MultiAutoCompleteTextView) findViewById(R.id.group_members_emails);
        group_members.setAdapter(friend_adapter);
        group_members.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    /**
     * Creates a group in the back end by sending the group's name, the user id of the user creating
     * the group, and the emails of the members that are to be added. If the friend to add does not
     * exist, that email will return a warning and not be added to the group.
     */
    public void sendGroupMemberData(){
        new Thread(new Runnable(){
            @Override
            public void run() {
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
                    // Friend's email does not exist
                    if (jsonObject.has("warning")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("warning", jsonObject.getString("warning"));
                        Message msg = new Message();
                        msg.setData(bundle);
                    } else {
                        Intent intent = new Intent(AddGroupMembersActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException jsex){
                    jsex.printStackTrace();
                }
            }
        }).start();
    }

}
