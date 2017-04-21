package com.example.mihika.expocr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GroupBalanceAdapter extends RecyclerView.Adapter<GroupBalanceAdapter.BalanceViewHolder> {

    private final BalanceListItemClickListener mOnClickListener;
    private List<String> mData;

    //constructor
    public GroupBalanceAdapter(GroupBalanceActivity listener) {
        mOnClickListener = listener;
        mData = new ArrayList<>();
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

    public void syncBalanceList(){
        new BalancesQueryTask().execute();
    }

    //inner class
    class BalanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView item_text;
        Button item_settle_up;

        //constructor
        BalanceViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            item_text = (TextView) itemView.findViewById(R.id.grp_balance_text);
            item_settle_up = (Button) itemView.findViewById(R.id.settle_up_grp_balance);

        }

        void bind(int listIndex){

            String rawData = mData.get(listIndex);

            item_text.setText(rawData);

            if (!rawData.contains("owes")) {
                item_settle_up.setVisibility(View.GONE);
            }
            item_settle_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText((GroupBalanceActivity)mOnClickListener, "SETTLE UP...", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onBalanceListItemClick(clickedPosition);
        }
    }

    private class BalancesQueryTask extends AsyncTask<String, Void, List<GroupTransaction.Pair>> {

        @Override
        protected List<GroupTransaction.Pair> doInBackground(String... params) {
            return get_user_balances();
        }

        @Override
        protected void onPostExecute(List<GroupTransaction.Pair> list){
            fill_balances_list(list);

        }
    }

    private void fill_balances_list(List<GroupTransaction.Pair> list){
        System.out.println("GroupBalanceAdapter: fill_balances_list: size: " + list.size());
        if (list.isEmpty()) {
            String text = "You are all settled up!";
            mData.add(text);
        }
        for (GroupTransaction.Pair pair: list) {
            String text = "";
            if (pair.amount >= 0) {
                text = "You owes u_id: " + pair.uid + " amount: " + pair.amount;
            } else {
                text = pair.uid + " owes you amount: " + Math.abs(pair.amount);
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
