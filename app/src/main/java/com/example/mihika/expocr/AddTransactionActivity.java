package com.example.mihika.expocr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class AddTransactionActivity extends AppCompatActivity {

    private Spinner transactionKindSpinner;
    private Spinner userSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        transactionKindSpinner = (Spinner)findViewById(R.id.transaction_kind_spinner);
        userSpinner = (Spinner) findViewById(R.id.user_spinner);
        addEntriesForSpinner();
        addListenerOnSpinnerItemSelection();
    }

    public void addAndReturn(View view) {
        //TODO: Send data to server
        Intent returnIntent = new Intent(AddTransactionActivity.this, MainActivity.class);
        startActivity(returnIntent);
    }

    protected void addEntriesForSpinner() {
        List<String> list = new ArrayList<String>();
        //We use fake list here for demo
        list.add("Me");
        list.add("User 1");
        list.add("User 2");
        list.add("User 3");
        //List<String> list = getUsersFromServer();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        userSpinner.setAdapter(dataAdapter);
    }

    protected String getUsersFromServer() {
        //TODO Implement download data from server
        return null;
    }

    public void addListenerOnSpinnerItemSelection() {
        transactionKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("For group")) {
                    userSpinner.setVisibility(View.VISIBLE);
                }
                else {
                    userSpinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
