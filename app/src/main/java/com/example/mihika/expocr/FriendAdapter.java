package com.example.mihika.expocr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihika on 3/13/17.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    //number of views it will hold
    private int mNumberItems;

    private final ListItemClickListener mOnClickListener;
    public List<String> mData;

    //constructor
    public FriendAdapter(int numberOfItems, ListItemClickListener listener) {
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
        mData = new ArrayList<>();
        for(int index = 0;index < 10; index++){
            mData.add("Friend" + index);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
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

    public int getmNumberItems() {
        mNumberItems = mData.size();
        //TODO should return number of items in the query result
        return mNumberItems;
    }

    //inner class
    class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView listItemFriendView;

        //constructor
        FriendViewHolder(View itemView){

            super(itemView);
            itemView.setOnClickListener(this);
            listItemFriendView = (TextView) itemView.findViewById(R.id.friend_item);

        }

        void bind(int listIndex){
            //TODO: setText using the query data

            listItemFriendView.setText(mData.get(listIndex));
        }

        @Override
        public void onClick(View v) {
            int clickedPostion = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPostion);
        }
    }




}
