package com.example.mihika.expocr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class GroupBalanceActivity extends AppCompatActivity implements GroupBalanceAdapter.BalanceListItemClickListener {

    private RecyclerView mList;
    private GroupBalanceAdapter mAdapter;
    private int g_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_balance);

        g_id = getIntent().getIntExtra("g_id", 1);

        mList = (RecyclerView) findViewById(R.id.rv_group_balance);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);

        mAdapter = new GroupBalanceAdapter(this);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onBalanceListItemClick(int clickedItemIndex) {
    }

    public int getG_id() {
        return g_id;
    }
}