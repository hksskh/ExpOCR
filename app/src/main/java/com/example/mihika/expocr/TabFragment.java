package com.example.mihika.expocr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment implements FriendAdapter.FriendListItemClickListener, GroupAdapter.GroupListItemClickListener, ExpenseTabAdapter.ExpenseListItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "page_title";

    public static final int FRIEND_FRAGMENT_REFRESH = 1;
    public static final int EXPENSE_FRAGMENT_REFRESH = 2;
    public static final int GROUP_FRAGMENT_REFRESH = 3;
    private static final int NUM_LIST_ITEMS = 10;

    // TODO: Rename and change types of parameters
    private String page_title;
    private FriendAdapter mFriendAdapter;
    private GroupAdapter mGroupAdapter;
    private ExpenseTabAdapter mExpenseAdapter;
    private RecyclerView mList;

    private Handler handler;

    private View baseView;

    private TextView textView;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeRefreshLayout;

    public TabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param page_title Title of page associated to this fragment.
     * @return A new instance of fragment TabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment newInstance(String page_title) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, page_title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page_title = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(baseView == null){//if baseView has been created, then we enter into this onCreateView function because of the limited cached pages size in ViewPager, so we can simply restore view from baseView
            asssignView(inflater, container);
            setupRefreshLayout();
        }
        return baseView;
    }

    private void setupRefreshLayout(){
        swipeRefreshLayout = (SwipeRefreshLayout) baseView.findViewById(R.id.tabSwipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue);
        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case FRIEND_FRAGMENT_REFRESH:
                        swipeRefreshLayout.setRefreshing(false);

                        break;
                    case GROUP_FRAGMENT_REFRESH:
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                    case EXPENSE_FRAGMENT_REFRESH:
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        };
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch(page_title){
                    case "FRIENDS":
                        mFriendAdapter.setIsRefreshing(true);
                        mFriendAdapter.syncFriendList();
                        break;
                    case "GROUPS":
                        mGroupAdapter.setIsRefreshing(true);
                        mGroupAdapter.syncGroupList();
                        break;
                    case "EXPENSES":
                        mExpenseAdapter.setIsRefreshing(true);
                        mExpenseAdapter.syncExpenseList();
                        break;
                }
            }
        });
    }

    public Handler getHandler(){
        return this.handler;
    }

    private void asssignView(LayoutInflater inflater, ViewGroup container){
        switch(page_title){
            case "FRIENDS":
                baseView = inflater.inflate(R.layout.fragment_tab, container, false);

                mList = (RecyclerView) baseView.findViewById(R.id.rv_friends);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
                mList.setLayoutManager(layoutManager);
                mList.setHasFixedSize(true);

                mFriendAdapter = new FriendAdapter(NUM_LIST_ITEMS, this);

                mList.setAdapter(mFriendAdapter);
                break;
            case "GROUPS":
                baseView = inflater.inflate(R.layout.fragment_tab, container, false);

                mList = (RecyclerView) baseView.findViewById(R.id.rv_friends);
                layoutManager = new LinearLayoutManager(this.getContext());
                mList.setLayoutManager(layoutManager);
                mList.setHasFixedSize(true);

                mGroupAdapter = new GroupAdapter(NUM_LIST_ITEMS, ((MainActivity)mListener).getU_id(), this);

                mList.setAdapter(mGroupAdapter);
                break;

            case "EXPENSES":
                baseView = inflater.inflate(R.layout.fragment_tab, container, false);

                mList = (RecyclerView) baseView.findViewById(R.id.rv_friends);
                layoutManager = new LinearLayoutManager(this.getContext());
                mList.setLayoutManager(layoutManager);
                mList.setHasFixedSize(true);

                mExpenseAdapter = new ExpenseTabAdapter(NUM_LIST_ITEMS, this);

                mList.setAdapter(mExpenseAdapter);
                break;
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(baseView != null){
            ((ViewGroup)baseView.getParent()).removeView(baseView);
        }
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

    @Override
    public void onFriendListItemClick(int clickedItemIndex) {
        String rawData = mFriendAdapter.getmData().get(clickedItemIndex);
        String[] rawList = rawData.split(",");
        Intent intent = new Intent(this.getActivity(), IndividualFriendActivity.class);
        intent.putExtra("receiver_id", rawList[0]);
        intent.putExtra("receiver_name", rawList[1]);
        rawList = rawList[2].split(":");
        intent.putExtra("receiver_email", rawList[0]);
        intent.putExtra("balance", rawList[1]);
        startActivity(intent);
    }

    @Override
    public void onGroupListItemClick(int clickedItemIndex) {
        String rawData = mGroupAdapter.getmData().get(clickedItemIndex);
        String[] rawList = rawData.split(",");
        Intent intent = new Intent(this.getActivity(), IndividualGroupActivity.class);
        intent.putExtra("group_id", rawList[0]);
        intent.putExtra("group_name", rawList[1]);
//        rawList = rawList[2].split(":");
        //intent.putExtra("receiver_email", rawList[0]);
        intent.putExtra("balance", rawList[2]);
        intent.putExtra("u_id", mGroupAdapter.getU_id());
        startActivity(intent);
    }

    @Override
    public void onExpenseListItemClick(int clickedItemIndex) {

    }

    public void refreshTabFragment(){
        switch(page_title){
            case "FRIENDS":
                mFriendAdapter.syncFriendList();
                break;
            case "GROUPS":
                mGroupAdapter.syncGroupList();
                break;
            case "EXPENSES":
                mExpenseAdapter.syncExpenseList();
                break;
        }
    }

    private String fragmentRefresh(){
        for(long index = 0; index < 10000000; index++){
            index <<= 1;
            index >>= 1;
        }
        if (mListener != null) {
            return mListener.onFragmentRefresh(page_title);
        }
        return null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    //Todo: an interface left for interaction between fragments and the MainActivity
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
         String onFragmentRefresh(String page_title);
    }
}
