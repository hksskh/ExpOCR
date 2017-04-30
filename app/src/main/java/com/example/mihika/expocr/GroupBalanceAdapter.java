package com.example.mihika.expocr;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mihika.expocr.util.LoadingDialog;
import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class GroupBalanceAdapter extends RecyclerView.Adapter<GroupBalanceAdapter.BalanceViewHolder> {

    private final BalanceListItemClickListener mOnClickListener;
    private List<String> mData;

    private Dialog loading_dialog;

    //constructor
    public GroupBalanceAdapter(GroupBalanceActivity listener) {
        mOnClickListener = listener;
        mData = new ArrayList<>();
        loading_dialog = LoadingDialog.showDialog((GroupBalanceActivity)mOnClickListener, "Initializing...");
        syncBalanceList();
    }

    interface BalanceListItemClickListener {
        void onBalanceListItemClick(int clickedItemIndex);
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
    public BalanceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_group_balance;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        BalanceViewHolder viewHolder = new BalanceViewHolder(view);

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
    public void onBindViewHolder(BalanceViewHolder holder, int position) {
        //populates the view with data
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<String> getmData(){
        return mData;
    }

    private void syncBalanceList(){
        new BalancesQueryTask().execute();
    }

    //inner class
    class BalanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView item_text;
        Button item_settle_up;

        //constructor
        BalanceViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            item_text = (TextView) itemView.findViewById(R.id.grp_balance_text);
            item_settle_up = (Button) itemView.findViewById(R.id.settle_up_grp_balance);
        }

        void bind(int listIndex) {

            final String rawData = mData.get(listIndex);

            item_text.setText(rawData);

            if (!rawData.contains("owes")) {
                item_settle_up.setVisibility(View.GONE);
            } else {
                item_settle_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rawData.startsWith("You owe")) {
                            String friendBrief = "You paid " + rawData.substring(8, rawData.lastIndexOf("$")).trim();
                            double balance = Double.parseDouble(rawData.substring(rawData.lastIndexOf("$") + 1).trim());
                            show_settle_up_dialog(friendBrief, balance);
                        } else {
                            Toast.makeText((GroupBalanceActivity)mOnClickListener, "Remind your friend!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onBalanceListItemClick(clickedPosition);
        }
    }

    private void show_settle_up_dialog(final String brief, double balance) {
        AlertDialog.Builder builder = new AlertDialog.Builder((GroupBalanceActivity)mOnClickListener);
        LayoutInflater inflater = ((GroupBalanceActivity)mOnClickListener).getLayoutInflater();
        View settle_up_view = inflater.inflate(R.layout.dialog_group_settle_up, null);

        TextView brief_view = (TextView) settle_up_view.findViewById(R.id.dialog_group_settle_up_brief);
        final EditText balance_view = (EditText) settle_up_view.findViewById(R.id.dialog_group_settle_up_balance);

        brief_view.setText(brief);
        balance_view.setText(String.valueOf(balance));

        builder.setTitle("Group Settle Up")
                .setView(settle_up_view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String balance_text = balance_view.getText().toString();
                        final double balance_double = Double.parseDouble(balance_text);
                        final String u_email = brief.substring(brief.indexOf("(") + 1, brief.lastIndexOf(")")).trim();

                        loading_dialog = LoadingDialog.showDialog((GroupBalanceActivity)mOnClickListener, "Saving...");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                doSettleUp(u_email, balance_double);
                                syncBalanceList();
                            }
                        }).start();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doSettleUp(String u_email, double balance) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String datetime = dateFormat.format(date);

        String url = "http://" + ServerUtil.getServerAddress() + "group/add_transaction_by_email";
        StringBuilder requestString = new StringBuilder();
        requestString.append("receiver_email=").append(u_email)
                .append("&group_id=").append(((GroupBalanceActivity)mOnClickListener).getG_id())
                .append("&category=").append("Payment")
                .append("&memo=").append("Cash")
                .append("&amount=")
                .append(balance * -1)//take care
                .append("&date=").append(datetime);
        System.out.println(requestString.toString());
        String response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

        System.out.println(response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("warning")) {
                // handle it
            }
        } catch (JSONException jsex){
            jsex.printStackTrace();
        }

        url = "http://" + ServerUtil.getServerAddress() + "group/add_transaction";
        requestString = new StringBuilder();
        requestString.append("receiver_id=").append(MainActivity.getU_id())
                .append("&group_id=").append(((GroupBalanceActivity)mOnClickListener).getG_id())
                .append("&category=").append("Payment")
                .append("&memo=").append("Cash")
                .append("&amount=")
                .append(balance)
                .append("&date=").append(datetime);
        System.out.println(requestString.toString());
        response = ServerUtil.sendData(url, requestString.toString(), "UTF-8");

        System.out.println(response);
    }

    private class BalancesQueryTask extends AsyncTask<String, Void, List<GroupTransaction.Pair>> {

        @Override
        protected List<GroupTransaction.Pair> doInBackground(String... params) {
            return get_user_balances();
        }

        @Override
        protected void onPostExecute(List<GroupTransaction.Pair> list){
            fill_balances_list(list);
            LoadingDialog.closeDialog(loading_dialog);
        }
    }

    private void fill_balances_list(List<GroupTransaction.Pair> list){
        System.out.println("GroupBalanceAdapter: fill_balances_list: size: " + list.size());
        mData.clear();//do not forget
        if (list.isEmpty()) {
            String text = "You are all settled up!";
            mData.add(text);
        }
        for (GroupTransaction.Pair pair: list) {
            String text;
            BigDecimal bd = new BigDecimal(pair.amount);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            pair.amount = bd.doubleValue();
            if (pair.amount < 0) {
                text = "You owe " + pair.getUserBrief() + " $" + Math.abs(pair.amount);
            } else {
                text = pair.getUserBrief() + " owes you $" + Math.abs(pair.amount);
            }
            mData.add(text);
            System.out.println("GroupBalanceAdapter: fill_balances_list: " + text);
        }

        notifyDataSetChanged();
    }

    private List<GroupTransaction.Pair> get_user_balances(){
        List<GroupTransaction.Pair> result = GroupTransaction.getOwedAmounts(((GroupBalanceActivity)mOnClickListener).getG_id(), MainActivity.getU_id());
        return result;
    }
    
}
