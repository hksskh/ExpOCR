package com.example.mihika.expocr;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A fragment representing a single Add_Transaction_Receipt_Item detail screen.
 * This fragment is either contained in a {@link AddTransactionReceiptItemListActivity}
 * in two-pane mode (on tablets) or a {@link AddTransactionReceiptItemDetailActivity}
 * on handsets.
 */
public class AddTransactionReceiptItemDetailFragment extends Fragment implements AddTransactionReceiptItemDetailAdapter.AddTransactionReceiptItemDetailItemClickListener {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_RECEIPT_LIST = "receipt_list";

    private JSONArray receipt_list;

    private View rootView;
    private RecyclerView mList;
    private AddTransactionReceiptItemDetailAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddTransactionReceiptItemDetailFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECEIPT_LIST)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            try {
                receipt_list  = new JSONArray(getArguments().getString(ARG_RECEIPT_LIST));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("receipt_list");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.addtransactionreceiptitem_detail, container, false);

        mList = (RecyclerView) rootView.findViewById(R.id.rv_addtransactionreceiptitem_detail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(layoutManager);

        mAdapter = new AddTransactionReceiptItemDetailAdapter(this, receipt_list);

        mList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onAddTransactionReceiptItemDetailItemClick(int clickedItemIndex) { }

    public double getBalance(){
        return mAdapter.getBalance();
    }

}
