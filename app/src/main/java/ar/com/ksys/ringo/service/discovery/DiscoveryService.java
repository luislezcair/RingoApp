package ar.com.ksys.ringo.service.discovery;

import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.*;
import android.os.Process;
import android.util.Log;

import ar.com.ksys.ringo.service.xmpp.XMPPClientService;


public class DiscoveryService extends Service {
    private static final String TAG = DiscoveryService.class.getSimpleName();
    private DiscoveryHandler mHandler;

    @Override
    public void onCreate() {
        final HandlerThread thread = new HandlerThread("DiscoveryThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mHandler = new DiscoveryHandler(thread.getLooper(), this);

        // Quit when a timeout occurs
        mHandler.setOnDiscoveryTimeout(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Timeout on service discovery");
                thread.quit();
                stopSelf();
            }
        });

        // When the server is found, start the xmpp service and send it the connection info
        mHandler.setOnServiceResolved(new Runnable() {
            @Override
            public void run() {
                Intent xmppIntent = new Intent(DiscoveryService.this, XMPPClientService.class);
                xmppIntent.putExtra("service_info", mHandler.getServiceInfo());
                startService(xmppIntent);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Message msg = mHandler.obtainMessage();
        msg.what = DiscoveryHandler.MSG_START_DISCOVERY;
        msg.setData(intent.getBundleExtra("service_info"));
        mHandler.sendMessage(msg);

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
