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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.receipt_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "To be implemented...", Toast.LENGTH_LONG).show();
            }
        });

        mList = (RecyclerView) findViewById(R.id.rv_recognize_receipt);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(false);

        mAdapter = new ReceiptAdapter(0, this);

        mList.setAdapter(mAdapter);

    }

    @Override
    public void onReceiptListItemClick(final int clickedItemIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View receipt_editor_view = inflater.inflate(R.layout.dialog_receipt_editor, null);
        final EditText price_text_view = (EditText) receipt_editor_view.findViewById(R.id.dialog_receipt_editor_price);
        final Spinner price_spinner_view = (Spinner) receipt_editor_view.findViewById(R.id.dialog_receipt_editor_price_spinner);
        double price_value = -1;

        final JSONObject jsonObject = mAdapter.getmData().get(clickedItemIndex);
        List<Double> prices_spinner_list = new ArrayList<>();
        JSONArray possible_prices;

        try {
            possible_prices = jsonObject.getJSONArray("possible_price");
            for(int index = 0; index < possible_prices.length(); index++){
                prices_spinner_list.add(possible_prices.getDouble(index));
            }
            if(jsonObject.has("price")){
                price_value = jsonObject.getDouble("price");
                if(price_value >= 0){
                    price_text_view.setText(String.valueOf(price_value));
                    if(!prices_spinner_list.contains(price_value)){
                        prices_spinner_list.add(price_value);//do not forget
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<Double> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prices_spinner_list);
        price_spinner_view.setAdapter(spinner_adapter);
        if(price_value >= 0){
            //crucial
            price_spinner_view.setSelection(spinner_adapter.getPosition(price_value));
        }

        price_spinner_view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                price_text_view.setText(String.valueOf(spinner.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setTitle("Specify the price")
                .setView(receipt_editor_view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String price_text = price_text_view.getText().toString();
                        double price_double = Double.parseDouble(price_text);
                        try {
                            jsonObject.put("price", price_double);
                            mAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String get_receipt_sketch(){
        return receipt_sketch.toString();
    }
}
