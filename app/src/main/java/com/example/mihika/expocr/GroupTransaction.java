package com.example.mikiha.expoocr;
//import com.example.mihika.expoocr.util.ServerUtil;
//import org.json.JSONArray;
//import org.json.JSONException;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by awesomeness on 4/9/2017.
 */

public class GroupTransaction {

    private double amount;
    private Calendar date;
    private String memo;
    private String category;
    private int gid;
    private int uid;

    ///// TODO: 4/9/2017
    public static ArrayList<GroupTransaction> getGroupTransactionsFromServer(int g_id){
        ArrayList<GroupTransaction> retval =  new ArrayList<GroupTransaction>();
		GroupTransaction trans1 = new GroupTransaction();
		GroupTransaction trans2 = new GroupTransaction();
		GroupTransaction trans3 = new GroupTransaction();
		GroupTransaction trans4 = new GroupTransaction();
		GroupTransaction trans5 = new GroupTransaction();
		GroupTransaction trans6 = new GroupTransaction();
		GroupTransaction trans7 = new GroupTransaction();
		GroupTransaction trans8 = new GroupTransaction();
		GroupTransaction trans9 = new GroupTransaction();
		GroupTransaction trans10 = new GroupTransaction();
		trans1.amount = -10;
		trans1.gid = 1;
		trans1.uid = 1;
		retval.add(trans1);
		trans2.amount = -20;
		trans2.gid = 1;
		trans2.uid = 2;
		retval.add(trans2);
		trans3.amount = -45;
		trans3.gid = 1;
		trans3.uid = 3;
		retval.add(trans3);
		trans4.amount = 25;
		trans4.gid = 1;
		trans4.uid = 4;
		retval.add(trans4);
		trans5.amount = 25;
		trans5.gid = 1;
		trans5.uid = 5;
		retval.add(trans5);
		trans6.amount = 25;
		trans6.gid = 1;
		trans6.uid = 6;
		retval.add(trans6);
		trans7.amount = 30;
		trans7.gid = 2;
		trans7.uid = 7;
		retval.add(trans7);
		trans8.amount = -10;
		trans8.gid = 2;
		trans8.uid = 8;
		retval.add(trans8);
		trans9.amount = -10;
		trans9.gid = 2;
		trans9.uid = 9;
		retval.add(trans9);
		trans10.amount = -10;
		trans10.gid = 2;
		trans10.uid = 10;
		retval.add(trans10);
		return retval;
    }
    /*
    public static ArrayList<Expense> getGroupExpensesFromServer(int g_id){
        ArrayList<Expense> expenseList = new ArrayList<Expense>();
        JSONArray expenses = null;
        try {
            expenses = new JSONArray(expense_retrieve_group_transaction(g_id));
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
                expense.sender_id = expenses.getJSONObject(i).getInt("Sender_Id");
                expense.receiver_id = expenses.getJSONObject(i).getInt("Receiver_Id");

                expenseList.add(expense);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return expenseList;
    }
    */

    /*private static String expense_retrieve_group_transaction(int g_id){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "group/get_transactions"; //check this with group
        String requestBody = "g_id=" + g_id;

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");

        return text;
    }*/

    public static ArrayList<Pair> getOwedAmounts(int g_id,int u_id){
        ArrayList<Pair> amounts = getUserNetBalances(g_id);
        ArrayList<Pair> dues = new ArrayList<Pair>();

        ArrayList<Pair> positives = new ArrayList<Pair>();
        ArrayList<Pair> negatives = new ArrayList<Pair>();
        for(Pair pair : amounts){
            if(pair.amount > 0)
                positives.add(pair);
            else if(pair.amount < 0)
                negatives.add(pair);
        }

        int posIdx = 0;
        int negIdx = 0;
        while (posIdx < positives.size() && negIdx < negatives.size()){
            if (positives.get(posIdx).uid == u_id){
                if(-negatives.get(negIdx).amount > positives.get(posIdx).amount){
                    dues.add(new Pair(negatives.get(negIdx).uid,positives.get(posIdx).amount));
                    break;
                } else if(-negatives.get(negIdx).amount == positives.get(posIdx).amount){
                    dues.add(new Pair(negatives.get(negIdx).uid,positives.get(posIdx).amount));
                    break;
                }
                else{
                    positives.get(posIdx).amount += negatives.get(negIdx).amount;
                    dues.add(new Pair(negatives.get(negIdx).uid,-negatives.get(negIdx).amount));
                    negIdx++;
                }
            }
            else if (negatives.get(negIdx).uid == u_id){
                if(positives.get(posIdx).amount > -negatives.get(negIdx).amount){
                    dues.add(new Pair(positives.get(posIdx).uid,negatives.get(negIdx).amount));
                    break;
                } else if(-negatives.get(negIdx).amount == positives.get(posIdx).amount){
                    dues.add(new Pair(positives.get(posIdx).uid,negatives.get(negIdx).amount));
                    break;
                }
                else{
                   negatives.get(negIdx).amount += positives.get(posIdx).amount;
                   dues.add(new Pair(positives.get(posIdx).uid,-positives.get(posIdx).amount));
                   posIdx++;
                }
            }
            else{
                if(-negatives.get(negIdx).amount > positives.get(posIdx).amount){
                    negatives.get(negIdx).amount += positives.get(posIdx).amount;
                    posIdx++;
                } else if(-negatives.get(negIdx).amount == positives.get(posIdx).amount){
                    negIdx++;
                    posIdx++;
                }
                else{
                    positives.get(posIdx).amount += negatives.get(negIdx).amount;
                    negIdx++;
                }
            }
        }


        return dues;
    }


    private static ArrayList<Pair> getUserNetBalances(int g_id){
        ArrayList<GroupTransaction> mDataSource = getGroupTransactionsFromServer(g_id); 
        HashMap<Integer, Double> balances= new HashMap<Integer, Double>();
        for(GroupTransaction x : mDataSource) {
            if (balances.containsKey(x.uid))
                balances.put(x.uid, x.amount + balances.get(x.uid));
            else
                balances.put(x.uid, x.amount);
        }

        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for(Object uid : balances.keySet().toArray()){
            pairs.add(new Pair((Integer) uid,balances.get(uid)));
        }

        return pairs;
    }

    static class Pair {
        public Integer uid;
        public Double amount;

        Pair(Integer uid, Double amount){
            this.uid = uid;
            this.amount = amount;
        }

    }
	public static void main(String[] args){
		ArrayList<Pair> retval = GroupTransaction.getOwedAmounts(2,10);
		System.out.println("ran");
		for (Pair p : retval){
			System.out.println(""+p.uid+" "+p.amount);
		}
	}
}