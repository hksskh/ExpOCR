package com.example.mihika.expocr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
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
import java.util.Vector;

/**
 * Created by mihika on 4/8/17.
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
            isRefreshing = false;
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

        public void syncGroupList(){
            new GroupAdapter.GroupsQueryTask().execute();
        }

    //inner class
    class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

        void bind(int listIndex){

            String rawData = mData.get(listIndex);
            String[] rawList = rawData.split(",");
            item_avatar.setImageResource(R.drawable.ic_list_group);
            item_name.setText(rawList[1]);
            //rawList = rawList[2].split(":");
            //item_balance.setText(rawList[1]);
            String test=new String("10");
            item_balance.setText(test);
            if(Double.parseDouble(item_balance.getText().toString()) < 0){
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.orange));
            }else{
                item_balance.setTextColor(((TabFragment)mOnClickListener).getResources().getColor(R.color.green));
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onGroupListItemClick(clickedPosition);
        }
    }

    private class GroupsQueryTask extends AsyncTask<String, Void, Pair> {

        @Override
        protected Pair doInBackground(String... params) {
            String groups= retrieve_all_groups();
            JSONArray groupnamesArray=new JSONArray();
            JSONArray groupArray = null;
            int limit=maxItemNumber;
            try {
                groupArray = new JSONArray(groups);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int index = 0; index < groupArray.length() && index < limit; index++){
                JSONObject jsonObj = null;
                JSONObject nameObj = null;
                try {
                    jsonObj =groupArray.getJSONObject(index);
                    String nameJson=get_group_name(jsonObj.get("g_id").toString());
                    JSONArray nameArray=new JSONArray(nameJson);
                    nameObj=nameArray.getJSONObject(0);
                    groupnamesArray.put(nameObj);
                    System.out.println(nameJson);
                    System.out.println(nameObj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Pair<JSONObject, JSONArray> grouppair=new Pair( groupArray, groupnamesArray);

            return grouppair;
        }

        @Override
        protected void onPostExecute(Pair p){
            fill_groups_list(p);
            notifyDataSetChanged();
            mNumberItems = mData.size() > maxItemNumber ? maxItemNumber : mData.size();
            if(isRefreshing){
                isRefreshing = false;//do not forget
                Message msg = new Message();
                msg.what = ((TabFragment)mOnClickListener).GROUP_FRAGMENT_REFRESH;
                ((TabFragment)mOnClickListener).getHandler().sendMessage(msg);
            }
        }
    }

        private void fill_groups_list(Pair p){
            JSONArray jsonArray = null;
            JSONArray nameArray = null;
            jsonArray = (JSONArray)p.first;
            nameArray = (JSONArray)p.second;
            int limit = maxItemNumber;
            mData.clear();
            group_name_list.clear();
            for(int index = 0; index < jsonArray.length() && index < limit; index++){
                JSONObject jsonObj = null;
                JSONObject nameObj = null;
                StringBuilder builder = new StringBuilder();
                try {
                    jsonObj=jsonArray.getJSONObject(index);
                    nameObj=nameArray.getJSONObject(index);
                    String balance="10";
                    builder.append(jsonObj.get("g_id")).append(",")
                            .append(nameObj.getJSONObject("fields").get("G_Name")).append(","+balance);
                            //.append(jsonObj.get("receiver_email")).append(":")
                            //.append(jsonObj.get("balance")
                    mData.add(builder.toString());
                    group_name_list.add(nameObj.getJSONObject("fields").get("G_Name").toString());
                    builder.setLength(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private String get_group_name(String group_id){
            String serverUrl = "http://10.0.2.2:8000/group/get_name";
            URL url = null;
            BufferedInputStream bis = null;
            ByteArrayOutputStream baos;
            BufferedOutputStream bos = null;
            HttpURLConnection connection = null;
            byte[] responseBody = null;
            try {
                url = new URL(serverUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String requestBody = "id=" + group_id;
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
                } finally {
                    connection.disconnect();
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

        private String retrieve_all_groups(){
            String serverUrl = "http://10.0.2.2:8000/group/get_groups_by_member";
            URL url = null;
            BufferedInputStream bis = null;
            ByteArrayOutputStream baos;
            BufferedOutputStream bos = null;
            HttpURLConnection connection = null;
            byte[] responseBody = null;
            try {
                url = new URL(serverUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String requestBody = "u_id=" + u_id;
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
                } finally {
                    connection.disconnect();
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
