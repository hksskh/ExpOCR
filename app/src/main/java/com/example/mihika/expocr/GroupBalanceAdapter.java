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
        //syncBalanceList();
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
        return 10;//mData.size();
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

            String rawData = "test";//mData.get(listIndex);

            item_text.setText(rawData);

            item_settle_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText((GroupBalanceActivity)mOnClickListener, "SETTLE UP...", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPostion = getAdapterPosition();
            mOnClickListener.onBalanceListItemClick(clickedPostion);
        }
    }

    private class BalancesQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return friend_retrieve_all_receivers();
        }

        @Override
        protected void onPostExecute(String s){
            fill_receivers_list(s);

        }
    }

    private void fill_receivers_list(String s){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mData.clear();
        for(int index = 0; index < jsonArray.length(); index++){
            JSONObject jsonObj = null;
            try {
                jsonObj = jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuilder builder = new StringBuilder();
            try {
                builder.append(jsonObj.get("receiver_id")).append(",")
                        .append(jsonObj.get("receiver_name")).append(",")
                        .append(jsonObj.get("receiver_email")).append(":")
                        .append(jsonObj.get("balance"));
                mData.add(builder.toString());
                builder.setLength(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    private String friend_retrieve_all_receivers(){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "transaction/get_all_receivers";
        String requestBody = "sender_id=" + MainActivity.getU_id();

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");

        return text;
    }


}
