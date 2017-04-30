package com.example.mihika.expocr;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * This is a class used to display all of a user's transactions in the Expense tab of the app.
 */
public class Expense {

    private Double balance;
    private Calendar date;
    private int id;
    private String expense;

    /**
     * Empty class object constructor
     */
    public Expense() { }

    /**
     * Class object constructor with all global variables initialized.
     * @param id u_id of receiver
     * @param expense description of transaction ("You lent %50.00 to Anthony" or "You borrowed $20.00 from Anthony")
     * @param balance amount lent or borrowed
     * @param date the date (April 11, 2017)
     * @throws ParseException
     */
    public Expense(int id, String expense, double balance, String date) throws ParseException {
        this.id = id;
        this.expense = expense;
        this.balance = balance;
        this.date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        this.date.setTime(sdf.parse(date));
    }

    /**
     * Class object constructor with 3 of 4 global variables initialized.
     * @param expense description of transaction ("You lent %50.00 to Anthony" or "You borrowed $20.00 from Anthony")
     * @param balance amount lent or borrowed
     * @param date the date (April 11, 2017)
     * @throws ParseException
     */
    public Expense(String expense, double balance, String date) throws ParseException {
        this.expense = expense;
        this.balance = balance;
        this.date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        this.date.setTime(sdf.parse(date));
    }

    /**
     * Getter for expense (description of a transaction).
     * @return String description of a transaction
     */
    public String getExpense() {
        return expense;
    }

    /**
     * Getter for balance (amount of a transaction).
     * @return Double amount of a transaction
     */
    public Double getBalance() {
        return balance;
    }

    /**
     * Getter for the date.
     * @return Calendar (April 11, 2017 is the format)
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Getter for user id.
     * @return int u_id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get all transactions from a JSON and display all the results in a arraylist.
     * @param filename json file containing the expenses
     * @param context
     * @return ArrayList an ArrayList of all expenses in the file given
     */
    public static ArrayList<Expense> getRecipesFromFile(String filename, Context context) {
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

    /**
     * test method to load from sample resource file
     * @param filename
     * @param context
     * @return
     */
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
