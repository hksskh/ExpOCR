package com.example.mihika.expocr;

/**
 * Created by briannaifft on 2/28/17.
 */


/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Expense {

    public String expense;
    public Double balance;
    public Calendar date;

    public static ArrayList<Expense> getRecipesFromFile(String filename, Context context){
        final ArrayList<Expense> expenseList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset("expenses.json", context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray expenses = json.getJSONArray("expenses");

            // Get Recipe objects from data
            for(int i = 0; i < expenses.length(); i++){
                Expense expense = new Expense();

                expense.expense = expenses.getJSONObject(i).getString("Expense");
                expense.balance = expenses.getJSONObject(i).getDouble("Balance");
                int date = expenses.getJSONObject(i).getInt("Day");
                int month = expenses.getJSONObject(i).getInt("Month");
                int year = expenses.getJSONObject(i).getInt("Year");
                expense.date = Calendar.getInstance();
                expense.date.set(year, month-1, date);

                expenseList.add(expense);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return expenseList;
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

}
