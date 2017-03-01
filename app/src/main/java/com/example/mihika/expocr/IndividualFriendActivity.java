package com.example.mihika.expocr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class IndividualFriendActivity extends AppCompatActivity {

    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_friend);

        mListView = (ListView) findViewById(R.id.expenses_list_view);
        final ArrayList<Expense> expenseList = Expense.getRecipesFromFile("recipes.json", this);

        String[] listItems = new String[expenseList.size()];

        for(int i = 0; i < expenseList.size(); i++){
            Expense expense = expenseList.get(i);
            listItems[i] = expense.expense;
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        mListView.setAdapter(adapter);
    }
}
