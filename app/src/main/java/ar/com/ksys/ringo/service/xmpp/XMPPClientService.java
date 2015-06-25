package ar.com.ksys.ringo.service.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import ar.com.ksys.ringo.R;
import ar.com.ksys.ringo.VisitorActivity;
import ar.com.ksys.ringo.service.util.NotificationListener;
import ar.com.ksys.ringo.service.util.RingoServiceInfo;
import ar.com.ksys.ringo.service.util.VisitorNotificationParser;

public class XMPPClientService extends Service {
    private static final String TAG = XMPPClientService.class.getSimpleName();
    private XMPPClientThread xmppClientThread;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RingoServiceInfo info = intent.getParcelableExtra("service_info");
        info.setCertificateFile(getResources().openRawResource(R.raw.xmpp_cert));

        xmppClientThread = new XMPPClientThread(info);

        // This action will be called when a valid JSON object is received (i. e., a visitor arrived)
        xmppClientThread.setNotificationReceivedAction(new NotificationListener<JSONObject>() {
            @Override
            public void onNotificationReceived(JSONObject json) {
                Intent intent = new Intent(XMPPClientService.this, VisitorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("visitor_notification", VisitorNotificationParser.fromJSON(json));
                startActivity(intent);
            }
        });

        xmppClientThread.connect();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        xmppClientThread.disconnect();
        xmppClientThread.quitThread();
        Log.d(TAG, "Ringo service destroyed");
    }
}
