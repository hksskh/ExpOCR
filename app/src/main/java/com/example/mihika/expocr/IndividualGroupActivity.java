package com.example.mihika.expocr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by briannaifft on 3/2/17.
 */

public class IndividualGroupActivity extends AppCompatActivity {

    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_group);

        mListView = (ListView) findViewById(R.id.expenses_list_view);
        final ArrayList<Expense> expenseList = Expense.getRecipesFromFile("recipes.json", this);

        ExpenseAdapter adapter = new ExpenseAdapter(this, expenseList);

        TextView netBalanceText = (TextView)findViewById(R.id.net_balance);

        if(adapter.getNetBalance() > 0){
            netBalanceText.append(Double.toString(adapter.getNetBalance()));
            netBalanceText.setTextColor(getResources().getColor(R.color.moneyGreen));
        }
        else {
            double netBalance = adapter.getNetBalance();
            netBalance *= (-1);
            netBalanceText.append("- $".concat(Double.toString(netBalance)));
            netBalanceText.setTextColor(getResources().getColor(R.color.negativeRed));
        }

        mListView.setAdapter(adapter);
    }
}
