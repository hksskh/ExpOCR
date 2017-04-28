package com.example.mihika.expocr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class CreateGroupActivity extends AppCompatActivity {


    private EditText group_name;
    private Button create_group_button;
    private int u_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group2);

        group_name = (EditText) findViewById(R.id.group_name);
        create_group_button = (Button) findViewById(R.id.select_members);

        //TODO get u_id from intent
        create_group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attemptCreateGroup()){
                    Intent intent = new Intent(CreateGroupActivity.this, AddGroupMembersActivity.class);
                    String gname = group_name.getText().toString();
                    intent.putExtra("GroupName", gname);
                    intent.putExtra("u_id", u_id);
                    System.out.println("Create Group Activity GROUP NAME: " + gname + "\n....................");
                    startActivity(intent);
                }

            }
        });


//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


    }
    public boolean attemptCreateGroup(){
        group_name.setError(null);

        // Store values at the time of the login attempt.
        String name = group_name.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(name)) {
            group_name.setError("Please enter a group name");
            focusView = group_name;
            cancel = true;
            focusView.requestFocus();
            return false;
        }
        else
            return true;

    }

}
