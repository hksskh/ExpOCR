package com.example.mihika.expocr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * internal fragment for individual friend page, used to display recyclerview of friend transactions list
 */
public class IndividualFriendFragment extends Fragment implements IndividualFriendAdapter.IndividualFriendItemClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_RECEIVER_ID = "receiver_id";

    public static final int INDIVIDUAL_FRIEND_LIST_REFRESH = 1;

    private int receiver_id;

    private View rootView;
    private RecyclerView mList;
    private IndividualFriendAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private OnFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IndividualFriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECEIVER_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            receiver_id  = getArguments().getInt(ARG_RECEIVER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_individual_friend, container, false);

        mList = (RecyclerView) rootView.findViewById(R.id.rv_fragment_individual_friend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(layoutManager);
        final FloatingActionButton fab = ((IndividualFriendActivity)mListener).getFab();
        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

        });

        mAdapter = new IndividualFriendAdapter(this, receiver_id);

        mList.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_individual_friend_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setIsRefreshing(true);
                mAdapter.syncIndividualFriendList();
            }
        });

        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case INDIVIDUAL_FRIEND_LIST_REFRESH:
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        };

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        String onFragmentRefresh();
    }

    @Override
    public void onIndividualFriendItemClick(int clickedItemIndex) {
    }

    public Handler getHandler() {
        return handler;
    }

    public RecyclerView getmList() {
        return mList;
    }

    /**
     * set swipe layout, synchronize friend transactions with server and refresh fragment content
     */
    public void refreshFragment(){
        swipeRefreshLayout.setRefreshing(true);
        mAdapter.setIsRefreshing(true);
        mAdapter.syncIndividualFriendList();
    }

    /**
     * forward refreshing request of friend information and netbalance to IndividualFriendActivity
     * @param s
     */
    public void refreshActivityToolBar(String s) {
        ((IndividualFriendActivity)mListener).refreshToolBar(s);
    }
}
