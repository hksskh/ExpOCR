package com.example.mihika.expocr;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class IndividualGroupAdapter extends RecyclerView.Adapter<IndividualGroupAdapter.ItemViewHolder> {

    private boolean isRefreshing;

    private final IndividualGroupItemClickListener mOnClickListener;
    private int g_id;
    private List<Expense> mData;

    //constructor
    public IndividualGroupAdapter(IndividualGroupFragment listener, int g_id) {
        mOnClickListener = listener;
        this.g_id = g_id;
        mData = new ArrayList<>();
        isRefreshing = true;

        setupItemTouchHelper();
        syncIndividualGroupList();
    }

    interface IndividualGroupItemClickListener {
        void onIndividualGroupItemClick(int clickedItemIndex);
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_expense;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ItemViewHolder viewHolder = new ItemViewHolder(view);

        return viewHolder;

    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        //populates the view with data
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<Expense> getmData(){
        return mData;
    }

    public double getNetBalance() {
        double netBalance = 0.0;
        for(Expense x : mData) {
            netBalance += x.getBalance();
        }
        return netBalance;
    }

    public void setIsRefreshing(boolean isRefreshing){
        this.isRefreshing = isRefreshing;
    }

    public void syncIndividualGroupList() {
        new TransactionBetweenQueryTask().execute();
    }

    private void setupItemTouchHelper(){
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ACTION_STATE_SWIPE, ItemTouchHelper.START);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.START) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(((IndividualGroupFragment)mOnClickListener).getContext());
                    builder.setTitle("Delete Group Transaction")
                            .setMessage("Do you really want to delete this transaction?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = viewHolder.getAdapterPosition();
                                    Calendar transactionDate = mData.get(position).getDate();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    String transactionDateString = dateFormat.format(transactionDate.getTime());

                                    mData.remove(position);
                                    new DeleteTransactionQueryTask().execute(transactionDateString);

                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    notifyDataSetChanged();//crucial
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(((IndividualGroupFragment)mOnClickListener).getmList());
    }

    //inner class
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView expenseTextView;
        TextView balanceTextView;
        TextView dayTextView;
        TextView monthTextView;
        TextView yearTextView;

        ItemViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            expenseTextView = (TextView)itemView.findViewById(R.id.expense_list_expense);
            balanceTextView = (TextView)itemView.findViewById(R.id.expense_list_balance);
            dayTextView = (TextView)itemView.findViewById(R.id.expense_list_day);
            monthTextView = (TextView)itemView.findViewById(R.id.expense_list_month);
            yearTextView = (TextView) itemView.findViewById(R.id.expense_list_year);

        }

        void bind(final int listIndex){

            Expense expense = mData.get(listIndex);
            DecimalFormat df = new DecimalFormat("#.00");

            expenseTextView.setText(expense.getExpense());
            if(expense.getBalance() < 0) {
                double balance = expense.getBalance() * (-1);
                balanceTextView.setText("- $".concat(df.format(balance)));
                balanceTextView.setTextColor(((IndividualGroupFragment)mOnClickListener).getResources().getColor(R.color.negativeRed));
            }
            else {
                balanceTextView.setText("$".concat(df.format(expense.getBalance())));
                balanceTextView.setTextColor(((IndividualGroupFragment)mOnClickListener).getResources().getColor(R.color.moneyGreen));
            }
            dayTextView.setText(Integer.toString(expense.getDate().get(Calendar.DAY_OF_MONTH)));
            monthTextView.setText(expense.getDate().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            yearTextView.setText(Integer.toString(expense.getDate().get(Calendar.YEAR)));

        }

        @Override
        public void onClick(View v) {
            int clickedPostion = getAdapterPosition();
            mOnClickListener.onIndividualGroupItemClick(clickedPostion);
        }
    }

    class TransactionBetweenQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String s = group_get_transaction_list_for(g_id, MainActivity.getU_id());
            fill_individual_group_list(s);

            return get_group_info();
        }

        @Override
        protected void onPostExecute(String s){
            notifyDataSetChanged();
            double netBalance = getNetBalance();
            s += ("," + netBalance);
            refreshActivityToolBar(s);
            if(isRefreshing){
                isRefreshing = false;//do not forget
                Message msg = new Message();
                msg.what = IndividualGroupFragment.INDIVIDUAL_GROUP_LIST_REFRESH;
                ((IndividualGroupFragment)mOnClickListener).getHandler().sendMessage(msg);
            }
        }
    }

    private void fill_individual_group_list(String s) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //int limit = data.size();
        mData.clear();
        for(int index = 0; index < jsonArray.length(); index++){ //&& index < limit; index++){
            JSONObject jsonObj = null;
            try {
                jsonObj=jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Expense expense = null;
            try {
                expense = new Expense(jsonObj.getString("category") + ": " + jsonObj.getString("memo"),
                        Double.parseDouble(jsonObj.getString("amount")), jsonObj.getString("date"));
                //TODO can also fetch people involved
            } catch (ParseException | JSONException e) {
                e.printStackTrace();
            }
            mData.add(expense);
        }
    }

    private String group_get_transaction_list_for(int g_id, int u_id){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "group/get_user_transactions";
        String requestBody = "g_id="+g_id + "&u_id=" + u_id;

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        return text;
    }

    class DeleteTransactionQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String transactionDate = params[0];
            delete_transaction(transactionDate);

            return get_group_info();
        }

        @Override
        protected void onPostExecute(String s){
            notifyDataSetChanged();
            double netBalance = getNetBalance();
            s += ("," + netBalance);
            refreshActivityToolBar(s);
        }
    }

    private String delete_transaction(String transactionDate) {
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "group/delete_transaction_by_date";
        String requestBody = "date=" + transactionDate;
        Log.d("delete_transaction", requestBody);

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        return text;
    }

    private String get_group_info() {
        String url = "http://" + ServerUtil.getServerAddress() + "group/get_group_name";
        StringBuilder requestString = new StringBuilder();
        requestString.append("g_id=").append(g_id);
        String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");
        String result = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            String name = jsonObject.getString("g_name");
            result = name;

        } catch (JSONException jsex) {
            jsex.printStackTrace();
        }
        return result;
    }

    private void refreshActivityToolBar(String s) {
        ((IndividualGroupFragment)mOnClickListener).refreshActivityToolBar(s);
    }

}
