package com.example.mihika.expocr;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity implements the OCR technology by scanning and parsing a receipt into individual
 * items and prices.
 */
public class RecognizeReceiptActivity extends AppCompatActivity implements ReceiptAdapter.ReceiptListItemClickListener{

    private JSONArray receipt_sketch;
    private RecyclerView mList;
    private ReceiptAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_receipt);
        setSupportActionBar((Toolbar) findViewById(R.id.receipt_toolbar));

        Intent intent = getIntent();
        try {
            receipt_sketch = new JSONArray(intent.getStringExtra("receipt_sketch"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mList = (RecyclerView) findViewById(R.id.rv_recognize_receipt);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(false);

        mAdapter = new ReceiptAdapter(this, mList);

        mList.setAdapter(mAdapter);

    }

    /**
     * Creates the menu option to recognize the receipt.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_receipt_recognize, menu);
        return true;
    }

    /**
     * If the button is clicked, the receipt starts being recognized and parsed.
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_receipt_recognize_next:
                Intent intent = new Intent(RecognizeReceiptActivity.this, AddTransactionActivity.class);
                intent.putExtra("receipt_list", mAdapter.getActualData().toString());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onReceiptListItemClick(final int clickedItemIndex) {
    }

    public String get_receipt_sketch(){
        return receipt_sketch.toString();
    }
}
