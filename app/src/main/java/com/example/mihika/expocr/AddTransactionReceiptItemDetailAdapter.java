package com.example.mihika.expocr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * adapter for recyclerview in AddTransactionReceiptItemDetailFragment
 */
public class AddTransactionReceiptItemDetailAdapter extends RecyclerView.Adapter<AddTransactionReceiptItemDetailAdapter.ItemViewHolder> {

    private final AddTransactionReceiptItemDetailItemClickListener mOnClickListener;
    private JSONArray mData;
    //mark if the data at this position in mData has been checked by user with checkbox view
    private List<Boolean> dataChecked;

    //constructor
    public AddTransactionReceiptItemDetailAdapter(AddTransactionReceiptItemDetailFragment listener, JSONArray mData) {
        mOnClickListener = listener;
        this.mData = mData;
        dataChecked = new ArrayList<>(mData.length());
        for (int index = 0; index < mData.length(); index++){
            dataChecked.add(false);
        }
    }

    interface AddTransactionReceiptItemDetailItemClickListener {
        void onAddTransactionReceiptItemDetailItemClick(int clickedItemIndex);
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
        int layoutIdForListItem = R.layout.addtransactionreceiptitem_detail_item;
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
        return mData.length();
    }

    /**
     * get balance of all the items checked by user
     * @return
     */
    public double getBalance() {
        double balance = 0.0;

        try {
            for (int index = 0; index < mData.length(); index++) {
                if (dataChecked.get(index)) {
                    balance += mData.getJSONObject(index).getDouble("price");
                }
            }
        } catch (JSONException jsonex) {
            jsonex.printStackTrace();
        }

        return balance;
    }

    public JSONArray getmData(){
        return mData;
    }

    //inner class
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CheckBox item_checkbox;
        TextView item_text;
        TextView item_price;

        ItemViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            item_checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_addtransactionreceiptitem_detail_item);
            item_text = (TextView) itemView.findViewById(R.id.text_addtransactionreceiptitem_detail_item);
            item_price = (TextView) itemView.findViewById(R.id.price_addtransactionreceiptitem_detail_item);

        }

        void bind(final int listIndex){
            try {
                JSONObject jsonObject = mData.getJSONObject(listIndex);
                item_text.setText(jsonObject.getString("text"));
                item_price.setText(jsonObject.getString("price"));
            } catch (JSONException jsonex) {
                jsonex.printStackTrace();
            }

            item_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    dataChecked.set(listIndex, isChecked);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPostion = getAdapterPosition();
            mOnClickListener.onAddTransactionReceiptItemDetailItemClick(clickedPostion);
        }
    }

}
