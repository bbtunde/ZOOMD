package com.overdrivedx.zoomd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.overdrivedx.adapter.FeedListAdapter;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.model.Item;

import java.util.ArrayList;
import java.util.List;


public class RunningActivity extends AppCompatActivity {

    private DatabaseHandler databaseHandler;
    private List<Item> feedItems;
    private ListView listView;
    private FeedListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        databaseHandler  = new DatabaseHandler(this);
        this.setTitle("Running");

        List<Item> dbitem = databaseHandler.getFeed("1", "2");

        listView = (ListView)findViewById(R.id.listfeedView);
        feedItems = new ArrayList<Item>();

        for(int i=0; i < dbitem.size(); i++) {
            feedItems.add(dbitem.get(i));
        }

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
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
    public void onResume(){
        super.onResume();

    }
}
