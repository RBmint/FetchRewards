package com.uiuc.fetchrewards;

import java.util.List;
import java.util.Map;

// Use an interface to send the result List back to the main activity
public interface AsyncTaskListener {
    void onTaskCompleted(List<Map.Entry<Integer, List<Item>>> result);
}
