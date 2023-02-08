package com.uiuc.fetchrewards;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GetJsonDataTest {

    @Mock
    private AsyncTaskListener listener;

    @Test
    public void testDoInBackground() {
        GetJsonData task = new GetJsonData(listener);

        String result = task.doInBackground("https://fetch-hiring.s3.amazonaws.com/mobile.html");
        String result2 = task.doInBackground(("badURL.com"));
        assertNotEquals("", result);
        assertEquals("", result2);
    }

    @Test
    public void testOnPostExecute() {
        GetJsonData task = new GetJsonData(listener);
        String jsonString = "[{\"listId\":1,\"name\":\"Item 1\"},{\"listId\":2,\"name\":\"Item 2\"}]";

        task.onPostExecute(jsonString);

        verify(listener).onTaskCompleted(org.mockito.ArgumentMatchers.argThat(argument -> {
            List<Map.Entry<Integer, List<Item>>> groups = argument;
            return groups.size() == 2;
        }));
    }
}