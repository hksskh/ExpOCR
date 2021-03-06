package com.expocr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by mihika on 4/8/17.
 */

/**
 * adapter for recyclerview in group tab page
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    //number of views it will hold
    private int mNumberItems;
    private int maxItemNumber;
    private int u_id;
    private boolean isRefreshing;

    private final GroupListItemClickListener mOnClickListener;
    private List<String> mData;
    private static Vector<String> group_name_list = new Vector<>();

    //constructor
    public GroupAdapter(int numberOfItems, int u_id, TabFragment listener) {
            maxItemNumber = numberOfItems;
            this.u_id = u_id;
            mOnClickListener = listener;
            mData = new ArrayList<>();
            isRefreshing = true;
            syncGroupList();
    }

    interface GroupListItemClickListener {
        void onGroupListItemClick(int clickedItemIndex);
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
        public GroupAdapter.GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.friend_list_item;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            GroupAdapter.GroupViewHolder viewHolder = new GroupAdapter.GroupViewHolder(view);

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
        public void onBindViewHolder(GroupAdapter.GroupViewHolder holder, int position) {
            //populates the view with data
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            mNumberItems = mData.size();
            return mNumberItems;
        }

        public List<String> getmData(){
            return this.mData;
        }

        public int getU_id(){
            return this.u_id;
        }

        public static Vector<String> get_group_name_list(){
            return group_name_list;
        }

        public void setIsRefreshing(boolean isRefreshing){
            this.isRefreshing = isRefreshing;
        }

    /**
     * synchronize with server the group list
     */
    public void syncGroupList(){
            new GroupAdapter.GroupsQueryTask().execute();
        }

    //inner class
    class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView item_avatar;
        TextView item_name;
        TextView item_balance;

        //constructor
        GroupViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            item_avatar = (ImageView) itemView.findViewById(R.id.friend_list_item_avatar);
            item_name = (TextView) itemView.findViewById(R.id.friend_list_item_name);
            item_balance = (TextView) itemView.findViewById(R.id.friend_list_item_balance);
        }

        void bind(int listIndex) {
            String rawData = mData.get(listIndex);
            String[] rawList = rawData.split(",");
            item_avatar.setImageResource(R.drawable.ic_list_group);
            item_name.setText(rawList[1]);
            //rawList = rawList[2].split(":");
            //item_balance.setText(rawList[1]);
            //String test=new String("10");
            double balance = Double.parseDouble(rawList[2]);
            BigDecimal bd = new BigDecimal(balance);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            balance = bd.doubleValue();
            if(balance < 0){
                item_balance.setText("You owe" + System.getProperty("line.separator") + "$" + String.format("%.2f", Math.abs(balance)));
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.negativeRed));
            }else if (balance > 0){
                item_balance.setText("You are owed" + System.getProperty("line.separator") + "$" + String.format("%.2f", Math.abs(balance)));
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.moneyGreen));
            }else {
                item_balance.setText("Settled up!" );
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.moneyGreen));

            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onGroupListItemClick(clickedPosition);
        }
    }

    /**
     * AsyncTask to sync with server the group list, the fill in the dataset and update recyclerview content
     */
    private class GroupsQueryTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            String groups= retrieve_all_groups();
            JSONArray groupArray = null;
            try {
                groupArray = new JSONArray(groups);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            fill_groups_list(groupArray);

            return groupArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray){

            notifyDataSetChanged();
            mNumberItems = mData.size() > maxItemNumber ? maxItemNumber : mData.size();
            if(isRefreshing){
                isRefreshing = false;//do not forget
                Message msg = new Message();
                msg.what = TabFragment.GROUP_FRAGMENT_REFRESH;
                ((TabFragment)mOnClickListener).getHandler().sendMessage(msg);
            }
        }
    }

    /**
     * fill dataset with group list retrieved from server
     * @param jsonArray
     */
    private void fill_groups_list(JSONArray jsonArray) {
        int limit = maxItemNumber;
        mData.clear();
        group_name_list.clear();
        for(int index = 0; index < jsonArray.length() && index < limit; index++){
            JSONObject jsonObject;
            StringBuilder builder = new StringBuilder();
            try {
                jsonObject = jsonArray.getJSONObject(index);
                double balance = GroupTransaction.getUserNetBalance(jsonObject.getInt("g_id"), u_id);
                builder.append(jsonObject.getInt("g_id")).append(",")
                        .append(jsonObject.getString("g_name")).append(",")
                        .append(balance);
                mData.add(builder.toString());
                group_name_list.add(jsonObject.getString("g_name"));
                builder.setLength(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String retrieve_all_groups(){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "group/get_groups_by_member";
        String requestBody = "u_id=" + u_id;

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        return text;
    }

}