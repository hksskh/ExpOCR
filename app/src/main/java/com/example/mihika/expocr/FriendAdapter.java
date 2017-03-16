package com.example.mihika.expocr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihika on 3/13/17.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    //number of views it will hold
    private int mNumberItems;
    private int maxItemNumber;
    private boolean isRefreshing;

    private final TabFragment mOnClickListener;
    private List<String> mData;

    //constructor
    public FriendAdapter(int numberOfItems, TabFragment listener) {
        maxItemNumber = numberOfItems;
        mOnClickListener = listener;
        mData = new ArrayList<>();
        isRefreshing = false;
        syncFriendList();
    }

    interface FriendListItemClickListener {
        void onFriendListItemClick(int clickedItemIndex);
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
    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.friend_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        FriendViewHolder viewHolder = new FriendViewHolder(view);

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
    public void onBindViewHolder(FriendViewHolder holder, int position) {
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

    public void setIsRefreshing(boolean isRefreshing){
        this.isRefreshing = isRefreshing;
    }

    public void syncFriendList(){
        new FriendsQueryTask().execute();
    }

    //inner class
    class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView item_avatar;
        TextView item_name;
        TextView item_balance;

        //constructor
        FriendViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            item_avatar = (ImageView) itemView.findViewById(R.id.friend_list_item_avatar);
            item_name = (TextView) itemView.findViewById(R.id.friend_list_item_name);
            item_balance = (TextView) itemView.findViewById(R.id.friend_list_item_balance);

        }

        void bind(int listIndex){

            String rawData = mData.get(listIndex);
            String[] rawList = rawData.split(",");
            item_avatar.setImageResource(R.drawable.ic_list_group);
            item_name.setText(rawList[1]);
            rawList = rawList[2].split(":");
            item_balance.setText(rawList[1]);
            if(Double.parseDouble(item_balance.getText().toString()) < 0){
                item_balance.setTextColor(mOnClickListener.getResources().getColor(R.color.orange));
            }else{
                item_balance.setTextColor(mOnClickListener.getResources().getColor(R.color.green));
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPostion = getAdapterPosition();
            mOnClickListener.onFriendListItemClick(clickedPostion);
        }
    }

    private class FriendsQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return friend_retrieve_all_receivers();
        }

        @Override
        protected void onPostExecute(String s){
            fill_receivers_list(s);
            mNumberItems = mData.size() > maxItemNumber ? maxItemNumber : mData.size();
            if(isRefreshing){
                isRefreshing = false;//do not forget
                Message msg = new Message();
                msg.what = mOnClickListener.FRIEND_FRAGMENT_REFRESH;
                mOnClickListener.getHandler().sendMessage(msg);
            }
        }
    }

    private void fill_receivers_list(String s){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int limit = maxItemNumber;
        mData.clear();
        for(int index = 0; index < jsonArray.length() && index < limit; index++){
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
        String serverUrl = "http://10.0.2.2:8000/transaction/get_all_receivers";
        URL url = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos;
        BufferedOutputStream bos = null;
        byte[] responseBody = null;
        try {
            url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            String requestBody = "sender_id=1";
            os.write(requestBody.getBytes("UTF-8"));
            os.flush();
            os.close();
            InputStream is = connection.getInputStream();
            bis =  new BufferedInputStream(is);
            baos = new ByteArrayOutputStream();
            bos = new BufferedOutputStream(baos);
            byte[] response_buffer = new byte[1024];
            int length = 0;
            while((length = bis.read(response_buffer)) > 0){
                bos.write(response_buffer, 0, length);
            }
            bos.flush();
            responseBody = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String text = null;
        try {
            text = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }


}
