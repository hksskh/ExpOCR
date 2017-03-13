package com.example.mihika.expocr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.VISIBLE;

public class AddTransactionActivity extends AppCompatActivity {

    private Spinner transactionKindSpinner;
    private MultiSelectionSpinner userSpinner;
    private Spinner catergorySpinner;
    private Spinner incomeOrExpenseSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        transactionKindSpinner = (Spinner)findViewById(R.id.transaction_kind_spinner);
        userSpinner = (MultiSelectionSpinner) findViewById(R.id.user_spinner);
        catergorySpinner = (Spinner) findViewById(R.id.transaction_catergory_spinner);
        incomeOrExpenseSpinner = (Spinner) findViewById(R.id.income_or_expense_spinner);
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
        userSpinner.setItems(list);

        List<String> list2 = new ArrayList<String>();
        //We use fake list here for demo
        list2.add("Clothing");
        list2.add("Food");
        list2.add("Housing");
        list2.add("Salary");
        //List<String> list = getCatergoriesFromServer();
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        catergorySpinner.setAdapter(dataAdapter2);
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

