package ar.com.ksys.ringo.service.xmpp;

import android.os.*;
import android.os.Process;

import org.json.JSONObject;

import ar.com.ksys.ringo.service.util.DataRunnable;
import ar.com.ksys.ringo.service.util.RingoServiceInfo;

/**
 * This class is a wrapper for XMPPClientWorker. It provides a clean interface which
 * executes most of the methods in XMPPClientWorker in a separate thread.
 */
class XMPPClientThread {
    private static final String TAG = XMPPClientThread.class.getSimpleName();

    private final HandlerThread thread;
    private final XMPPHandler handler;
    private final XMPPClientWorker worker;

    // Commands:
    private static final int MSG_QUIT_THREAD = 0;
    private static final int MSG_CONNECT = 1;
    private static final int MSG_DISCONNECT = 2;

    public XMPPClientThread(RingoServiceInfo serviceInfo) {
        thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        handler = new XMPPHandler(thread.getLooper());
        worker = new XMPPClientWorker(serviceInfo);
    }

    public void connect() {
        handler.sendEmptyMessage(MSG_CONNECT);
    }

    public void disconnect() {
        handler.sendEmptyMessage(MSG_DISCONNECT);
    }

    public void quitThread() {
        handler.sendEmptyMessage(MSG_QUIT_THREAD);
    }

    public void setNotificationReceivedAction(DataRunnable<JSONObject> r) {
        worker.setNotificationAction(r);
    }

    private class XMPPHandler extends Handler {
        public XMPPHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_CONNECT:
                    worker.connect();
                    break;
                case MSG_DISCONNECT:
                    worker.disconnect();
                    break;
                case MSG_QUIT_THREAD:
                    thread.quit();
                    break;
            }
        }
    }
}