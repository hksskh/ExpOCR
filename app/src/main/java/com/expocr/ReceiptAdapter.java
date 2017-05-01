package com.expocr;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * adapter for recyclerview displaying recognized receipt item list
 */
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    private RecyclerView recyclerView;
    private final ReceiptListItemClickListener mOnClickListener;
    private List<JSONObject> mData;

    //constructor
    public ReceiptAdapter(RecognizeReceiptActivity listener, RecyclerView recyclerView) {
        mOnClickListener = listener;
        this.recyclerView = recyclerView;
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
        return mData.size();
    }

    public List<JSONObject> getmData(){
        return mData;
    }

    /**
     * return JSONArray containing mapping of each receipt item with its price
     * @return
     */
    public JSONArray getActualData(){
        JSONArray actualData = new JSONArray();

        try {
            for (JSONObject dataObj: mData) {
                double price = -1;
                try {
                    price = Double.parseDouble(dataObj.getString("price"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (price > 0) {
                    JSONObject actualDataObj = new JSONObject();
                    actualDataObj.put("text", dataObj.getString("text"));
                    actualDataObj.put("price", price);
                    actualData.put(actualDataObj);
                }
            }
        } catch (JSONException jsex) {
            jsex.printStackTrace();
        }

        return actualData;
    }

    public void syncReceiptList(){
        new ReceiptQueryTask().execute();
    }

    //inner class
    class ReceiptViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView item_text;
        EditText item_price;

        //constructor
        ReceiptViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            item_text = (TextView) itemView.findViewById(R.id.receipt_list_item_text);
            item_price = (EditText) itemView.findViewById(R.id.receipt_list_item_edittext);

        }

        void bind(final int listIndex){

            String text = null;
            try {
                text = mData.get(listIndex).getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            item_text.setText(text);

            try {
                item_price.setText(new BigDecimal(mData.get(listIndex).getString("price")).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            item_price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        mData.get(listIndex).put("price", s.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onReceiptListItemClick(clickedPosition);
        }
    }

    /**
     * fill in content of recyclerview the recognized receipt
     */
    private class ReceiptQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return ((RecognizeReceiptActivity)mOnClickListener).get_receipt_sketch();
        }

        @Override
        protected void onPostExecute(String s){
            fill_receipt_list(s);
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
                mDataJson.put("price", jsonObj.getJSONArray("possible_price").getString(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

}
