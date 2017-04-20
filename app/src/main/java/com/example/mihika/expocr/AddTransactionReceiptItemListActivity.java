package com.example.mihika.expocr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An activity representing a list of Add_Transaction_Receipt_Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AddTransactionReceiptItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AddTransactionReceiptItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private JSONArray receipt_list;
    private List<String> friend_list;
    private List<Double> balance_list;
    private int selectedFriend = 0;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtransactionreceiptitem_list);

        Intent inIntent = getIntent();
        try {
            receipt_list = new JSONArray(inIntent.getStringExtra("receipt_list"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        friend_list = new ArrayList<>();
        Collections.addAll(friend_list, inIntent.getStringArrayExtra("friend_list"));
        balance_list = new ArrayList<>();
        for (int index = 0; index < friend_list.size(); index++) {
            balance_list.add(0.0);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTransactionReceiptItemListActivity.this, AddTransactionActivity.class);
                intent.putExtra("amount", balance_list.get(0));
                NavUtils.navigateUpTo(AddTransactionReceiptItemListActivity.this, intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.addtransactionreceiptitem_list);
        assert recyclerView != null;
        setupRecyclerView();

        if (findViewById(R.id.addtransactionreceiptitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            //mTwoPane = true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.hasExtra("balance")) {
            balance_list.set(selectedFriend, intent.getDoubleExtra("balance", 0.0));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void setupRecyclerView() {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(friend_list));
    }

    class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<String> mValues;

        SimpleItemRecyclerViewAdapter(List<String> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.addtransactionreceiptitem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.nameView.setText(friend_list.get(position));
            holder.balanceView.setText(String.valueOf(balance_list.get(position)));
            holder.balanceView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        try {
                            balance_list.set(position, Double.parseDouble(s.toString()));//do not forget
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedFriend = position;
                    /*if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(AddTransactionReceiptItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        AddTransactionReceiptItemDetailFragment fragment = new AddTransactionReceiptItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.addtransactionreceiptitem_detail_container, fragment)
                                .commit();
                    } else {*/
                        Context context = v.getContext();
                        Intent intent = new Intent(context, AddTransactionReceiptItemDetailActivity.class);
                        intent.putExtra(AddTransactionReceiptItemDetailFragment.ARG_RECEIPT_LIST, receipt_list.toString());

                        context.startActivity(intent);
                    //}
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final ImageView avatarView;
            final EditText balanceView;
            final TextView nameView;

            ViewHolder(View view) {
                super(view);
                mView = view;

                avatarView = (ImageView) mView.findViewById(R.id.add_transaction_receipt_friend_list_item_avatar);
                balanceView = (EditText) mView.findViewById(R.id.add_transaction_receipt_friend_list_item_balance);
                nameView = (TextView) mView.findViewById(R.id.add_transaction_receipt_friend_list_item_name);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + nameView.getText() + ": " + balanceView.getText() + "'";
            }
        }
    }
}
