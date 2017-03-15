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
public class TabFragment extends Fragment implements FriendAdapter.ListItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "page_title";

    private static final int FRAGMENT_REFRESH = 1;
    private static final int DJANGO_TEST = 2;
    private static final int NUM_LIST_ITEMS = 10;

    // TODO: Rename and change types of parameters
    private String page_title;
    private FriendAdapter mFriendAdapter;
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
            swipeRefreshLayout = (SwipeRefreshLayout) baseView.findViewById(R.id.tabSwipeRefreshLayout);
            swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue);
            handler = new Handler(){
                public void handleMessage(Message msg){
                    super.handleMessage(msg);
                    switch(msg.what){
                        case TabFragment.FRAGMENT_REFRESH:
                            if(page_title.equals("FRIENDS")){
                                //mFriendAdapter.mData.remove(0);
                                mFriendAdapter.notifyDataSetChanged();


                            }else{
                                Bundle b = msg.getData();
                                String text = b.getString("text");
                            /*if(text != null && TabFragment.this.page_title != "EXPENSES"){
                            }*/
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            break;
                        case TabFragment.DJANGO_TEST:
                            //b = msg.getData();
                            //text = b.getString("text");
                            //Toast.makeText(baseView.getContext(), text, Toast.LENGTH_LONG).show();
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

    private void asssignView(LayoutInflater inflater, ViewGroup container){
        switch(page_title){
            case "FRIENDS":
                new FriendsQueryTask().execute();

                baseView = inflater.inflate(R.layout.fragment_tab, container, false);

                mList = (RecyclerView) baseView.findViewById(R.id.rv_friends);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
                mList.setLayoutManager(layoutManager);
                mList.setHasFixedSize(true);

                mFriendAdapter = new FriendAdapter(NUM_LIST_ITEMS, this);

                mList.setAdapter(mFriendAdapter);
                break;
            case "GROUPS":
                baseView = inflater.inflate(R.layout.fragment_tab_expenses, container, false);
                final ListView listView_grp = (ListView) baseView.findViewById(R.id.fragment_tab_expenses_listview);
                List listItems = new ArrayList<>();
                int [] imageIDs = new int[]{R.drawable.ic_list_group, R.drawable.ic_list_group, R.drawable.ic_list_group};
                String[] infos = new String[]{"You recorded a payment from Jack in group1", "You paid Jack in group1", "You created the group group 1"};
                String[] alerts = new String[]{"You received $20.00", "You paid $10.00", "2 members in group group 1"};
                String[] dates = new String[]{"Mar 2", "Mar 2", "Mar 2"};
                for(int ij = 0; ij < 5; ij++){
                    for(int i = 0; i < 3; i++){
                        Map<String, Object> listItem = new HashMap<>();
                        listItem.put("imageID", imageIDs[i]);
                        listItem.put("info", infos[i]);
                        listItem.put("alert", alerts[i]);
                        listItem.put("date", dates[i]);
                        listItem.put("textColor", getResources().getColor(R.color.blue));
                        listItems.add(listItem);
                    }
                }
                Expenses_List_Adapter list_adapter = new Expenses_List_Adapter(this.getContext(), listItems, R.layout.fragment_tab_expenses_list_item, new String[]{"imageID", "info", "alert", "date", "textColor"}, new int[]{R.id.fragment_tab_expenses_list_icon, R.id.fragment_tab_expenses_list_info, R.id.fragment_tab_expenses_list_alert, R.id.fragment_tab_expenses_list_date});
                listView_grp.setAdapter(list_adapter);
                listView_grp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(baseView.getContext(), IndividualGroupActivity.class);
                    startActivity(intent);
                    }
                });
                break;
            case "EXPENSES":
                baseView = inflater.inflate(R.layout.fragment_tab_expenses, container, false);
                final ListView listView_exp = (ListView) baseView.findViewById(R.id.fragment_tab_expenses_listview);
                listItems = new ArrayList<>();
                imageIDs = new int[]{R.drawable.ic_list_money_off, R.drawable.ic_list_money_in, R.drawable.ic_list_group};
                infos = new String[]{"You recorded a payment from Jack in group1", "You paid Jack in group1", "You created the group group 1"};
                alerts = new String[]{"You received $20.00", "You paid $10.00", "2 members in group group 1"};
                dates = new String[]{"Mar 2", "Mar 2", "Mar 2"};
                for(int ij = 0; ij < 5; ij++){
                    for(int i = 0; i < 3; i++){
                        Map<String, Object> listItem = new HashMap<>();
                        listItem.put("imageID", imageIDs[i]);
                        listItem.put("info", infos[i]);
                        listItem.put("alert", alerts[i]);
                        listItem.put("date", dates[i]);
                        switch(i){
                            case 0:
                                listItem.put("textColor", getResources().getColor(R.color.floatingbtnbgd));
                                break;
                            case 1:
                                listItem.put("textColor", getResources().getColor(R.color.green));
                                break;
                            case 2:
                                listItem.put("textColor", getResources().getColor(R.color.blue));
                                break;
                        }
                        listItems.add(listItem);
                    }
                }
                list_adapter = new Expenses_List_Adapter(this.getContext(), listItems, R.layout.fragment_tab_expenses_list_item, new String[]{"imageID", "info", "alert", "date", "textColor"}, new int[]{R.id.fragment_tab_expenses_list_icon, R.id.fragment_tab_expenses_list_info, R.id.fragment_tab_expenses_list_alert, R.id.fragment_tab_expenses_list_date});
                listView_exp.setAdapter(list_adapter);
                listView_exp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String info = (String) ((TextView)view.findViewById(R.id.fragment_tab_expenses_list_info)).getText();
                        if(info.contains("recorded")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    TabFragment.this.test_create_user();
                                }
                            }).start();
                            Intent intent = new Intent(baseView.getContext(), IndividualFriendActivity.class);
                            startActivity(intent);
                        }else if(info.contains("paid")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    TabFragment.this.test_get_user_by_id();
                                }
                            }).start();
                            Intent intent = new Intent(baseView.getContext(), IndividualFriendActivity.class);
                            startActivity(intent);
                        }else if(info.contains("group")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    TabFragment.this.test_get_gmail_user();
                                }
                            }).start();
                            Intent intent = new Intent(baseView.getContext(), IndividualGroupActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                break;
        }
    }

    private void test_create_user(){
        String serverUrl = "http://10.0.2.2:8000/user/create";
        URL url = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos;
        BufferedOutputStream bos = null;
        byte[] responseBody = null;
        try {
            url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            byte[] requestBody = "username=fwefewf&email=fwefew@qq.com&password=12345".getBytes("UTF-8");
            os.write(requestBody);
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
            }
        }
        String text = null;
        StringBuilder builder = null;
        try {
            text = new String(responseBody, "UTF-8");
            JSONObject jsonObject = new JSONObject(text);
            builder = new StringBuilder();
            builder.append("{U_Id: ").append(jsonObject.get("id")).append(", U_Name: ")
                    .append(jsonObject.get("name")).append(", Email: ")
                    .append(jsonObject.get("email")).append("}");
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        text = builder.toString();
        Message msg = new Message();
        msg.what = TabFragment.DJANGO_TEST;
        Bundle b = new Bundle();
        b.putString("text", text);
        msg.setData(b);
        TabFragment.this.handler.sendMessage(msg);
    }

    private void test_get_user_by_id(){
        String serverUrl = "http://10.0.2.2:8000/user/get_user_by_id";
        URL url = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos;
        BufferedOutputStream bos = null;
        byte[] responseBody = null;
        try {
            url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            byte[] requestBody = "id=9".getBytes("UTF-8");
            os.write(requestBody);
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
            }
        }
        String text = null;
        StringBuilder builder = new StringBuilder("[");
        try {
            text = new String(responseBody, "UTF-8");
            JSONArray jsonArray = new JSONArray(text);
            for(int index = 0; index < jsonArray.length(); index++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(index);
                builder.append("{U_Id: ").append(jsonObject.get("pk")).append(", U_Name: ");
                jsonObject = jsonObject.getJSONObject("fields");
                builder.append(jsonObject.get("U_Name")).append(", Email: ")
                        .append(jsonObject.get("Email")).append("}, ");
            }
            builder.append("]");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        text = builder.toString();
        Message msg = new Message();
        msg.what = TabFragment.DJANGO_TEST;
        Bundle b = new Bundle();
        b.putString("text", text);
        msg.setData(b);
        TabFragment.this.handler.sendMessage(msg);
    }

    private void test_get_gmail_user(){
        String serverUrl = "http://10.0.2.2:8000/user/gmail_user";
        URL url = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos;
        BufferedOutputStream bos = null;
        byte[] responseBody = null;
        try {
            url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            byte[] requestBody = "".getBytes("UTF-8");
            os.write(requestBody);
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
            }
        }
        String text = null;
        StringBuilder builder = new StringBuilder("[");
        try {
            text = new String(responseBody, "UTF-8");
            JSONArray jsonArray = new JSONArray(text);
            for(int index = 0; index < jsonArray.length(); index++){
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                jsonObject = jsonObject.getJSONObject("fields");
                builder.append("{U_Name: ").append((String)jsonObject.get("U_Name"))
                        .append(", Email: ").append((String)jsonObject.get("Email")).append("}, ");
            }
            builder.append("]");
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        text = builder.toString();
        Message msg = new Message();
        msg.what = TabFragment.DJANGO_TEST;
        Bundle b = new Bundle();
        b.putString("text", text);
        msg.setData(b);
        TabFragment.this.handler.sendMessage(msg);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(baseView != null){
            ((ViewGroup)baseView.getParent()).removeView(baseView);
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
    public void onListItemClick(int clickedItemIndex) {
        String rawData = mFriendAdapter.mData.get(clickedItemIndex);
        String[] rawList = rawData.split(",");
        Intent intent = new Intent(this.getActivity(), IndividualFriendActivity.class);
        intent.putExtra("receiver_id", rawList[0]);
        intent.putExtra("receiver_name", rawList[1]);
        rawList = rawList[2].split(":");
        intent.putExtra("receiver_email", rawList[0]);
        intent.putExtra("balance", rawList[1]);
        startActivity(intent);
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

    class FriendsQueryTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            return mFriendAdapter.friend_retrieve_all_receivers();
        }

        @Override
        protected void onPostExecute(String s){
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int limit = mFriendAdapter.getItemCount();
            mFriendAdapter.mData.clear();
            for(int index = 0; index < jsonArray.length() && index < limit; index++){
                JSONObject jsonObj = null;
                try {
                    jsonObj = jsonArray.getJSONObject(index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StringBuilder builder = new StringBuilder();
                try {
                    builder.append(jsonObj.get("receiver_id")).append(",")
                            .append(jsonObj.get("receiver_name")).append(",")
                            .append(jsonObj.get("receiver_email")).append(":")
                            .append(jsonObj.get("balance"));
                    mFriendAdapter.mData.add(builder.toString());
                    builder.setLength(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mFriendAdapter.notifyDataSetChanged();
        }
    }
}

class Expenses_List_Adapter extends SimpleAdapter {

    private Context mContext;
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;
    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private LayoutInflater mInflater;

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public Expenses_List_Adapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        mData = data;
        mResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(mResource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    private void bindView(int position, View view){
        final Map<String, ?> dataSet = mData.get(position);
        if(dataSet == null){
            return;
        }
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;
        for(int index = 0; index < count; index++){
            final View v = view.findViewById(to[index]);
            if(v != null){
                final Object data = dataSet.get(from[index]);
                switch(index){
                    case 0:
                        ((ImageView)v).setImageResource((Integer) data);
                        break;
                    case 1:
                        ((TextView)v).setText((String) data);
                        break;
                    case 2:
                        ((TextView)v).setText((String) data);
                        ((TextView)v).setTextColor((Integer) (dataSet.get(from[count])));
                        break;
                    case 3:
                        ((TextView)v).setText((String) data);
                        break;
                }
            }
        }
    }
}
