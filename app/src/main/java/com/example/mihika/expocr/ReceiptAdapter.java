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

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    //number of views it will hold
    private int mNumberItems;
    private int maxItemNumber;

    private final ReceiptListItemClickListener mOnClickListener;
    private List<JSONObject> mData;

    //constructor
    public ReceiptAdapter(int numberOfItems, RecognizeReceiptActivity listener) {
        maxItemNumber = numberOfItems;
        mOnClickListener = listener;
        mData = new ArrayList<>();
        syncReceiptList();
    }

    interface ReceiptListItemClickListener {
        void onReceiptListItemClick(int clickedItemIndex);
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
    public ReceiptViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.receipt_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReceiptViewHolder viewHolder = new ReceiptViewHolder(view);

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
    public void onBindViewHolder(ReceiptViewHolder holder, int position) {
        //populates the view with data
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        mNumberItems = mData.size();
        return mNumberItems;
    }

    public List<JSONObject> getmData(){
        return mData;
    }

    public void syncReceiptList(){
        new ReceiptQueryTask().execute();
    }

    //inner class
    class ReceiptViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView item_text;

        //constructor
        ReceiptViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            item_text = (TextView) itemView.findViewById(R.id.receipt_list_item_text);

        }

        void bind(int listIndex){

            String text = null;
            try {
                text = mData.get(listIndex).getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            item_text.setText(text);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onReceiptListItemClick(clickedPosition);
        }
    }

    private class ReceiptQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return ((RecognizeReceiptActivity)mOnClickListener).get_receipt_sketch();
        }

        @Override
        protected void onPostExecute(String s){
            fill_receipt_list(s);
            mNumberItems = maxItemNumber > 0 ? (mData.size() > maxItemNumber ? maxItemNumber : mData.size()) : mData.size();
        }
    }

    private void fill_receipt_list(String s){
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
            JSONObject mDataJson = new JSONObject();
            mData.add(mDataJson);
            try {
                mDataJson.put("text", jsonObj.getString("text"));
                mDataJson.put("possible_price", jsonObj.getJSONArray("possible_price"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

}
