package com.uiuc.fetchrewards;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AsyncTaskListener{

    private ExpandableListView expandableListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permission at runtime for Android 6.0 or higher versions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    520);
        } else {
            // Permission has already been granted, get JSON data from website
            new GetJsonData(this).execute("https://fetch-hiring.s3.amazonaws.com/hiring.json");
        }
    }

    @Override
    public void onTaskCompleted(List<Map.Entry<Integer, List<Item>>> result) {

        Toast.makeText(getApplicationContext(),
                "Successfully retrieved data from URL",
                Toast.LENGTH_SHORT).show();

        expandableListView = findViewById(R.id.expandableListView);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Put the sorted and grouped items into expandable list
        int count = 0;
        for (Map.Entry<Integer, List<Item>> group : result) {
            listDataHeader.add("List ID: " + group.getKey());
            List<String> header = new ArrayList<>();
            for (Item item : group.getValue()) {
                header.add("  ID: " + item.getId() + ";   Name: " + item.getName());
            }
            listDataChild.put(listDataHeader.get(count), header);
            count++;
        }

        listAdapter = new MyExpandableListAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(listAdapter);
    }
}

// Class for Item from JSON file retrieved from URL
class Item {
    private int id;
    private int listId;
    private String name;

    public int getId() {
        return id;
    }

    public int getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }
}

