package ar.com.ksys.ringo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ar.com.ksys.ringo.service.entities.VisitorNotification;

public class VisitorActivity extends Activity {
    private static final String TAG = VisitorActivity.class.getSimpleName();
    private ImageView visitorPictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);

        visitorPictureView = (ImageView) findViewById(R.id.imageView);
        TextView visitorNameView = (TextView) findViewById(R.id.textVisitorName);

        VisitorNotification notification = getIntent().getParcelableExtra("visitor_notification");
        new PictureDownloader().execute(notification.getPictureUrl());

        Log.d(TAG, "URL: " + notification.getPictureUrl().toString());

        String visitorNames = TextUtils.join("\n", notification.getVisitors().toArray());
        visitorNameView.setText(visitorNames);
    }

    /**
     * Task that will execute on a separate thread to download the image from
     * the server and show it on the screen.
     */
    private class PictureDownloader extends AsyncTask<URL, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            Bitmap picture = null;
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                picture = BitmapFactory.decodeStream(in);
            } catch(IOException e) {
                Log.e(TAG, "Connection to media server failed");
            }
            return picture;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            visitorPictureView.setImageBitmap(bitmap);
        }
    }
}
