package com.example.mihika.expocr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Internal Fragment in individual group activity, used to display recyclerview containing group transactions list
 */
public class IndividualGroupFragment extends Fragment implements IndividualGroupAdapter.IndividualGroupItemClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_GROUP_ID = "group_id";

    public static final int INDIVIDUAL_GROUP_LIST_REFRESH = 1;

    private int g_id;

    private View rootView;
    private RecyclerView mList;
    private IndividualGroupAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private OnFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IndividualGroupFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_GROUP_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            g_id  = getArguments().getInt(ARG_GROUP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_individual_group, container, false);

        mList = (RecyclerView) rootView.findViewById(R.id.rv_fragment_individual_group);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mList.setLayoutManager(layoutManager);
        final FloatingActionButton fab = ((IndividualGroupActivity)mListener).getFab();
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

        mAdapter = new IndividualGroupAdapter(this, g_id);

        mList.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_individual_group_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setIsRefreshing(true);
                mAdapter.syncIndividualGroupList();
            }
        });

        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case INDIVIDUAL_GROUP_LIST_REFRESH:
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
    public void onIndividualGroupItemClick(int clickedItemIndex) {
    }

    public Handler getHandler() {
        return handler;
    }

    public RecyclerView getmList() {
        return mList;
    }

    /**
     * set swipe layout, sync group transactions with server and refresh fragment content
     */
    public void refreshFragment(){
        swipeRefreshLayout.setRefreshing(true);
        mAdapter.setIsRefreshing(true);
        mAdapter.syncIndividualGroupList();
    }

    /**
     * forward request of refreshing group name and net balance from adapter to individual group activity
     * @param s
     */
    public void refreshActivityToolBar(String s) {
        ((IndividualGroupActivity)mListener).refreshToolBar(s);
    }
}
