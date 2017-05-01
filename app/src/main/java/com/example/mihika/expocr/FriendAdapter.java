package com.example.mihika.expocr;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by mihika on 3/13/17.
 */

/**
 * adapter for recyclerview in Friend tab page
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    //number of views it will hold
    private int mNumberItems;
    private int maxItemNumber;
    private boolean isRefreshing;

    private final FriendListItemClickListener mOnClickListener;
    private List<String> mData;
    private static Vector<String> friend_name_list = new Vector<>();
    private static Vector<String> friend_email_list = new Vector<>();
    private static List<String> friend_brief_list = new ArrayList<>();
    private static List<Integer> friend_id_list = new ArrayList<>();
    private static HashMap<String, Uri> friend_avatar_uri_list = new HashMap<>();

    //constructor
    public FriendAdapter(int numberOfItems, TabFragment listener) {
        maxItemNumber = numberOfItems;
        mOnClickListener = listener;
        mData = new ArrayList<>();
        isRefreshing = true;
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

    public static Vector<String> get_friend_name_list(){
        return friend_name_list;
    }

    public static Vector<String> get_friend_email_list(){
        return friend_email_list;
    }

    public static List<String> get_friend_brief_list() {
        return friend_brief_list;
    }

    public static List<Integer> get_friend_id_list() {
        return friend_id_list;
    }

    public static HashMap<String, Uri> get_friend_avatar_uri_list() {
        return friend_avatar_uri_list;
    }

    public void setIsRefreshing(boolean isRefreshing){
        this.isRefreshing = isRefreshing;
    }

    /**
     * synchronize friend list of this recyclerview with server
     */
    public void syncFriendList(){
        new FriendsQueryTask().execute();
    }

    //inner class
    class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView item_avatar;
        TextView item_name;
        TextView item_email;
        TextView item_balance;

        //constructor
        FriendViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            item_avatar = (ImageView) itemView.findViewById(R.id.friend_list_item_avatar);
            item_name = (TextView) itemView.findViewById(R.id.friend_list_item_name);
            item_email = (TextView) itemView.findViewById(R.id.friend_list_item_email);
            item_balance = (TextView) itemView.findViewById(R.id.friend_list_item_balance);

        }

        void bind(int listIndex){

            item_avatar.setImageURI(null);
            item_avatar.setImageResource(R.drawable.ic_uiuc_seal);
            Uri avatarUri = friend_avatar_uri_list.get(String.valueOf(friend_id_list.get(listIndex)));
            if (avatarUri != null) {
                item_avatar.setImageURI(null);
                item_avatar.setImageURI(avatarUri);
            }

            String rawData = mData.get(listIndex);
            String[] rawList = rawData.split(",");
            item_name.setText(rawList[1]);
            rawList = rawList[2].split(":");
            item_email.setText(rawList[0]);
            double bal = Double.parseDouble(rawList[1]);
            BigDecimal bd = new BigDecimal(bal);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            bal = bd.doubleValue();

            if(bal < 0){

                item_balance.setText("You owe" + System.getProperty("line.separator") + "$" + String.format("%.2f", Math.abs(bal)));
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.negativeRed));
            }else if (bal > 0){
                item_balance.setText("You are owed" + System.getProperty("line.separator") + "$" + String.format("%.2f", Math.abs(bal)));
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.moneyGreen));

           } else{
                item_balance.setText("Settled up!" );
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.moneyGreen));

            }
        }

        @Override
        public void onClick(View v) {
            int clickedPostion = getAdapterPosition();
            mOnClickListener.onFriendListItemClick(clickedPostion);
        }
    }

    /**
     * AsyncTask to request friend list and friend avatar from server, then fill in the dataset and update content of recyclerview
     */
    private class FriendsQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = friend_retrieve_all_receivers();
            fill_receivers_list(result);
            mNumberItems = mData.size() > maxItemNumber ? maxItemNumber : mData.size();

            friend_avatar_uri_list.clear();
            for (int friend_id: friend_id_list) {
                System.out.println("friend_id: " + friend_id);
                friend_avatar_uri_list.put(String.valueOf(friend_id), download_friend_avatar(friend_id));
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s){
            notifyDataSetChanged();
            if(isRefreshing){
                isRefreshing = false;//do not forget
                Message msg = new Message();
                msg.what = ((TabFragment)mOnClickListener).FRIEND_FRAGMENT_REFRESH;
                ((TabFragment)mOnClickListener).getHandler().sendMessage(msg);
            }
        }
    }

    /**
     * download avatar for u_id, and store it locally, return the uri to the avatar file
     * @param friend_id
     * @return
     */
    public static Uri download_friend_avatar(int friend_id) {
        byte[] bytes = MainActivity.download_avatar_bytes(friend_id);
        Uri avatarUri = null;
        System.out.println("download_friend_avatar: finish download avatar bytes");
        if (bytes != null && bytes.length > 0) {
            System.out.println("download_friend_avatar bytes size: " + bytes.length);

            File avatarDir = new File(MainActivity.getappExternalCacheDir(), "avatar");
            avatarDir = new File(avatarDir, String.valueOf(friend_id));
            if(!avatarDir.exists()){
                avatarDir.mkdirs();
            }
            File avatarFile = new File(avatarDir, "avatar.jpg");
            avatarUri = Uri.fromFile(avatarFile);
            System.out.println(avatarFile.exists());
            if (avatarFile.exists()) {
                avatarFile.delete();
            }
            System.out.println(avatarFile.exists());

            try {//do not forget
                FileOutputStream fos = new FileOutputStream(new File(avatarUri.getPath()));
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return avatarUri;
    }

    /**
     * fill in dataset the friend list retrieved from server
     * @param s
     */
    private void fill_receivers_list(String s){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int limit = maxItemNumber;
        mData.clear();
        friend_name_list.clear();
        friend_email_list.clear();
        friend_brief_list.clear();
        friend_id_list.clear();
        for(int index = 0; index < jsonArray.length() && index < limit; index++){
            JSONObject jsonObj = null;
            try {
                jsonObj = jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuilder builder = new StringBuilder();
            try {
                builder.append(jsonObj.get("friend_id")).append(",")
                        .append(jsonObj.get("friend_name")).append(",")
                        .append(jsonObj.get("friend_email")).append(":")
                        .append(jsonObj.get("balance"));
                mData.add(builder.toString());
                friend_name_list.add(jsonObj.getString("friend_name"));
                friend_email_list.add(jsonObj.getString("friend_email"));
                friend_brief_list.add(jsonObj.getString("friend_name") + " (" + jsonObj.getString("friend_email") + ")");
                friend_id_list.add(jsonObj.getInt("friend_id"));
                builder.setLength(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String friend_retrieve_all_receivers(){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "transaction/get_all_friends";
        String requestBody = "user_id=" + MainActivity.getU_id();

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");

        return text;
    }


}
