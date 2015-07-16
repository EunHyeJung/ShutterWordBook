package com.example.lab.myapplication;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button buttonHide;
    private Button buttonShow;
    private ListView list;

    //list adapter
    private ArrayAdapter<String> adapter;

    // list data
    private ArrayList<String> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonHide = (Button) findViewById(R.id.button_hide);
        buttonShow = (Button) findViewById(R.id.button_show);
        list = (ListView) findViewById(R.id.listView);

        buttonShow.setOnClickListener(this);
        buttonHide.setOnClickListener(this);


        // list data
        data = new ArrayList<>();
        data.add("a");
        data.add("b");
        data.add("c");
        data.add("d");

        // list adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
                                        // 액티비티, 레이아웃, 데이터

        // connect list and adapter
        list.setAdapter(adapter);
        list.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button_hide :
                list.setVisibility(View.GONE);
                break;
            case R.id.button_show :
                list.setVisibility(View.VISIBLE);
                break;
        }
    }
}
