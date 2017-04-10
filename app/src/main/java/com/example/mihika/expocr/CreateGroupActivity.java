package com.example.mihika.expocr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText name_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name_text = (EditText) findViewById(R.id.name_text);

        Button createGroupButton = (Button) findViewById(R.id.createGroupButton);
        createGroupButton.setOnlClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
    }

    private void sendData () {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String url = "http://" + ServerUtil.getServerAddress() + "group/create";
                StringBuilder requestString = new StringBuilder();
                requestString.append("&name=").append(name_text.getText());
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
                        int g_id = jsonObject.getInt("g_id");
                        sendMemeber(g_id);
                    }
                } catch (JSONException jsex){
                    jsex.printStackTrace();
                }

            }
        }).start();
    }

    private void sendMemeber(int g_id){
        String url = "http://" + ServerUtil.getServerAddress() + "group/add_member";
        StringBuilder requestString = new StringBuilder();
        requestString.append("&g_id=").append(g_id).append("&u_id=").append(MainActivity.u_id);
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
                Intent gotoMain = new Intent(CreateGroupActivity.this, MainActivity.class);
                gotoMain.putExtra("createGroup", true);
                startActivity(gotoMain);
            }
        } catch (JSONException jsex){
            jsex.printStackTrace();
        }
    }


}
