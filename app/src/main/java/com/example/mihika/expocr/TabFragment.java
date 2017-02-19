package com.example.mihika.expocr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "page_title";

    private static final int FRAGMENT_REFRESH = 1;

    // TODO: Rename and change types of parameters
    private String page_title;

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
            baseView = inflater.inflate(R.layout.fragment_tab, container, false);
            textView = (TextView) baseView.findViewById(R.id.tab_textview);
            textView.setText("Hello, this is tab " + page_title);
            swipeRefreshLayout = (SwipeRefreshLayout) baseView.findViewById(R.id.tabSwipeRefreshLayout);
            swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue);
            handler = new Handler(){
                public void handleMessage(Message msg){
                    super.handleMessage(msg);
                    switch(msg.what){
                        case TabFragment.FRAGMENT_REFRESH:
                            Bundle b = msg.getData();
                            String text = b.getString("text");
                            if(text != null){
                                TabFragment.this.textView.setText(text);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            break;
                    }
                }
            };
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String text = TabFragment.this.fragmentRefresh();
                            Message msg = new Message();
                            msg.what = TabFragment.FRAGMENT_REFRESH;
                            Bundle b = new Bundle();
                            b.putString("text", text);
                            msg.setData(b);
                            TabFragment.this.handler.sendMessage(msg);
                        }
                    }).start();
                }
            });
        }
        return baseView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(baseView != null){
            ((ViewGroup)baseView.getParent()).removeView(baseView);
        }
    }

    private String fragmentRefresh(){
        for(long index = 0; index < Integer.MAX_VALUE; index++){
            index <<= 1;
            index >>= 1;
        }
        if (mListener != null) {
            return mListener.onFragmentRefresh(page_title);
        }
        return null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
         String onFragmentRefresh(String page_title);
    }
}
