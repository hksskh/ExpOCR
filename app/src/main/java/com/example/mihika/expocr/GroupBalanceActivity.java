package com.example.mihika.expocr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class GroupBalanceActivity extends AppCompatActivity implements GroupBalanceAdapter.BalanceListItemClickListener {

    private RecyclerView mList;
    private GroupBalanceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_balance);

        mList = (RecyclerView) findViewById(R.id.rv_group_balance);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);

        mAdapter = new GroupBalanceAdapter(this);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onBalanceListItemClick(int clickedItemIndex) {
    }
}
