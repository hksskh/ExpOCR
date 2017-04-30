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

/**
 * Activity that starts the process of creating a new group. This activity will ask to give a name
 * for the new group and then redirect you to the AddGroupMembersActivity to add members to the group.
 */
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
    }

    /**
     * Creates a group named by the group name inputted by the user.
     * @return true if group was successfully created
     */
    public boolean attemptCreateGroup() {
        group_name.setError(null);

        // Store group name from user input
        String name = group_name.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if the user entered a group name.
        if (TextUtils.isEmpty(name)) {
            group_name.setError("Please enter a group name");
            focusView = group_name;
            cancel = true;
            focusView.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

}
