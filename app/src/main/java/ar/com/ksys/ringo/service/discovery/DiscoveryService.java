package ar.com.ksys.ringo.service.discovery;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;

import ar.com.ksys.ringo.service.XmppClientService;


public class DiscoveryService extends Service {
    //private static final String TAG = DiscoveryService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final HandlerThread thread = new HandlerThread("ServiceDiscovery", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        final DiscoveryHandler discoveryHandler = new DiscoveryHandler(thread.getLooper(), this);

        discoveryHandler.setOnDiscoveryTimeout(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        });

        discoveryHandler.setOnServiceResolved(new Runnable() {
            @Override
            public void run() {
                // Start the xmpp service and send it the connection info
                Intent xmppIntent = new Intent(DiscoveryService.this, XmppClientService.class);
                xmppIntent.putExtra("service_info", discoveryHandler.getServiceInfo());
                startService(xmppIntent);
            }
        });

        Message msg = discoveryHandler.obtainMessage();
        msg.what = DiscoveryHandler.MSG_START_DISCOVERY;
        msg.setData(intent.getBundleExtra("service_info"));
        discoveryHandler.sendMessage(msg);

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
