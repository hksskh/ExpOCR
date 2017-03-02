package com.example.mihika.expocr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mihika.expocr.Expense;
import com.example.mihika.expocr.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by briannaifft on 3/1/17.
 */

public class ExpenseAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Expense> mDataSource;

    public ExpenseAdapter(Context context, ArrayList<Expense> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_expense, parent, false);

        TextView expenseTextView = (TextView)rowView.findViewById(R.id.expense_list_expense);
        TextView balanceTextView = (TextView)rowView.findViewById(R.id.expense_list_balance);
        TextView dayTextView = (TextView)rowView.findViewById(R.id.expense_list_day);
        TextView monthTextView = (TextView)rowView.findViewById(R.id.expense_list_month);

        Expense expense = (Expense) getItem(position);
        DecimalFormat df = new DecimalFormat("#.00");

        expenseTextView.setText(expense.expense);
        if(expense.balance < 0) {
            double balance = expense.balance *= (-1);
            balanceTextView.setText("- $".concat(df.format(expense.balance)));
        }
        else {
            balanceTextView.setText("$".concat(df.format(expense.balance)));
        }
        dayTextView.setText(Integer.toString(expense.date.get(Calendar.DAY_OF_MONTH)));
        monthTextView.setText(expense.date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        return rowView;
    }

}
