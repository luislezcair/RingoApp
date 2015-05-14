package ar.com.ksys.ringo.service.discovery;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import ar.com.ksys.ringo.service.util.RingoServiceInfo;

class DiscoveryHandler extends Handler implements ServiceListener {
    //private static final String TAG = DiscoveryHandler.class.getSimpleName();

    public static final int MSG_START_DISCOVERY = 0;
    private static final int MSG_TIMEOUT = 1;
    private boolean isDiscoveryDown = false;
    private boolean isDiscoveryStarted = false;

    private final Context mContext;
    private WifiManager.MulticastLock multicastLock;
    private JmDNS jmdns;
    private String mServiceType;
    private String mServiceName;
    private RingoServiceInfo mServiceInfo;

    private Runnable mOnServiceResolved;
    private Runnable mOnDiscoveryTimeout;

    public DiscoveryHandler(Looper looper, Context context) {
        super(looper);
        mContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_START_DISCOVERY:
                // If we are already working don't start again
                if(isDiscoveryStarted) {
                    return;
                }
                mServiceType = msg.getData().getString("service_type");
                mServiceName = msg.getData().getString("service_name");
                discover();
                break;

            case MSG_TIMEOUT:
                tearDown();
                if(mOnDiscoveryTimeout != null) {
                    mOnDiscoveryTimeout.run();
                }
                break;
        }

    }

    private void discover() {
        final int timeout = 5000;

        try {
            jmdns = JmDNS.create(InetAddress.getByName("0.0.0.0"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        isDiscoveryStarted = true;

        // Acquire a multicast lock, required for DNS service discovery
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifiManager.createMulticastLock("RingoDiscoveryLock");
        multicastLock.acquire();

        sendEmptyMessageDelayed(MSG_TIMEOUT, timeout);
        jmdns.addServiceListener(mServiceType, this);
    }

    public RingoServiceInfo getServiceInfo() {
        return mServiceInfo;
    }

    public void setOnServiceResolved(Runnable action) {
        mOnServiceResolved = action;
    }

    public void setOnDiscoveryTimeout(Runnable action) {
        mOnDiscoveryTimeout = action;
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        if(event.getName().contains(mServiceName)) {
            jmdns.requestServiceInfo(mServiceType, mServiceName, 1);
        }
    }

    @Override
    public void serviceRemoved(ServiceEvent event) { }

    @Override
    public void serviceResolved(ServiceEvent event) {
        mServiceInfo = new RingoServiceInfo(event.getInfo());

        if(mOnServiceResolved != null) {
            mOnServiceResolved.run();
        }

        // Release the lock and stop discovery as soon as we have our service
        tearDown();
    }

    private void tearDown() {
        if(isDiscoveryDown) {
            return;
        }

        jmdns.removeServiceListener(mServiceType, this);
        multicastLock.release();

        // JmDNS takes up to five seconds to close. In case we resolved a service,
        // our timeout will trigger while this method is blocked in close().
        // So we set the flag before calling jmdns.close()
        isDiscoveryDown = true;

        try {
            jmdns.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
