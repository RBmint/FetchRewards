package com.uiuc.fetchrewards;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The Network cannot work on main thread. Use a separate thread and AsyncTask
public class GetJsonData extends AsyncTask<String, Void, String> {

    private AsyncTaskListener listener;

    public GetJsonData(AsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        String jsonString = "";
        try {
            URL url = new URL(urls[0]);

            // Connect using the GET method
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Connection Failed with HTTP error code: " + conn.getResponseCode());
            }

            // Convert the input stream to output string
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();
            jsonString = sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        // Prevent null reference error if connection somehow failed
        if (jsonString.isEmpty()) {
            return;
        }

        // Parse the JSON into a list of Item objects
        Gson gson = new Gson();
        Type itemListType = new TypeToken<ArrayList<Item>>(){}.getType();
        List<Item> items = gson.fromJson(jsonString, itemListType);

        // Group items by listId
        Map<Integer, List<Item>> groupedItems = new HashMap<>();
        for (Item item : items) {
            // Filter null or blank names
            if (item.getName() == null || item.getName().trim().isEmpty()) {
                continue;
            }
            List<Item> itemGroup = groupedItems.get(item.getListId());
            if (itemGroup == null) {
                itemGroup = new ArrayList<>();
                groupedItems.put(item.getListId(), itemGroup);
            }
            itemGroup.add(item);
        }

        // Sort items within each group by name
        for (List<Item> itemGroup : groupedItems.values()) {
            // Note that name contains "Item" in front, so Item 102 < Item 12 < Item 132
            itemGroup.sort((a, b) -> a.getName().compareTo(b.getName()));
        }

        // Sort groups by listId
        List<Map.Entry<Integer, List<Item>>> sortedGroups = new ArrayList<>(groupedItems.entrySet());
        sortedGroups.sort(Map.Entry.comparingByKey());

        // Send the List to the listener to be used in main activity
        listener.onTaskCompleted(sortedGroups);
    }
}
