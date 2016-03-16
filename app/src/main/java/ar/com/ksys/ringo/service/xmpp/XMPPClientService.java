package ar.com.ksys.ringo.service.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import ar.com.ksys.ringo.R;
import ar.com.ksys.ringo.VisitorActivity;
import ar.com.ksys.ringo.integrated.Timbre;
import ar.com.ksys.ringo.service.util.NotificationListener;
import ar.com.ksys.ringo.service.util.RingoServiceInfo;
import ar.com.ksys.ringo.service.util.VisitorNotificationParser;

public class XMPPClientService extends Service {
    private static final String TAG = XMPPClientService.class.getSimpleName();
    private XMPPClientThread xmppClientThread;
    Timbre timbre;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timbre = new Timbre();
        RingoServiceInfo info = intent.getParcelableExtra("service_info");
        info.setCertificateFile(getResources().openRawResource(R.raw.xmpp_cert));

        xmppClientThread = new XMPPClientThread(info);

        // This action will be called when a valid JSON object is received (i. e., a visitor arrived)
        xmppClientThread.setNotificationReceivedAction(new NotificationListener<JSONObject>() {
            @Override
            public void onNotificationReceived(JSONObject json) {
                try {
                    //solamente muestra la notificación si el timbre está activado, de lo contrario no hace nada
                    if (timbre.isEstaActivado()){
                    URL pictureUrl = new URL(json.getString("picture_url"));
                    Intent intent = new Intent(XMPPClientService.this, VisitorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("url", pictureUrl);
                    startActivity(intent);
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "The URL received is not valid. This might be due to a server misconfiguration");
                } catch (JSONException e) {
                    Log.e(TAG, "There is no URL in this JSON object");
                }
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
