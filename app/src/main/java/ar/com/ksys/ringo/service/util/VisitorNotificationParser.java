package ar.com.ksys.ringo.service.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ar.com.ksys.ringo.service.entities.Visitor;
import ar.com.ksys.ringo.service.entities.VisitorNotification;

public class VisitorNotificationParser {
    private static final String TAG = VisitorNotificationParser.class.getSimpleName();

    /**
     * Construct a VisitorNotification object from its JSON representation
     * @param json JSONObject containing a picture URL and an array of Visitor objects
     * @return A VisitorNotification with its corresponding list of Visitors
     */
    public static VisitorNotification fromJSON(JSONObject json) {
        VisitorNotification notification = new VisitorNotification();

        try {
            String pictureUrl = json.getString("picture_url");
            URL url = new URL(pictureUrl);
            notification.setPictureUrl(url);

            List<Visitor> visitors = new ArrayList<>();
            JSONArray jsonVisitors = json.getJSONArray("visitors");

            for(int i = 0; i < jsonVisitors.length(); i++) {
                Visitor visitor = new Visitor();
                JSONObject jsonVisitor = jsonVisitors.getJSONObject(i);
                visitor.setName(jsonVisitor.getString("name"));
                visitors.add(visitor);
            }

            notification.setVisitors(visitors);
        } catch (JSONException e) {
            Log.e(TAG, "The URL received is not valid. This might be due to a server misconfiguration");
            return null;
        } catch (MalformedURLException e) {
            Log.e(TAG, "There is no URL in this JSON object");
            e.printStackTrace();
        }

        return notification;
    }
}
