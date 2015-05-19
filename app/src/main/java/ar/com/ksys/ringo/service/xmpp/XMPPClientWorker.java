package ar.com.ksys.ringo.service.xmpp;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ar.com.ksys.ringo.service.util.DataRunnable;
import ar.com.ksys.ringo.service.util.RingoServiceInfo;

class XMPPClientWorker implements MessageListener {
    private static final String TAG = XMPPClientWorker.class.getSimpleName();

    private final RingoServiceInfo serviceInfo;
    private SSLContext sslContext;
    private XMPPTCPConnection connection;
    private MultiUserChat multiUserChat;
    private DataRunnable<JSONObject> notificationAction;


    public XMPPClientWorker(RingoServiceInfo info) {
        initializeSSLContext(info.getCertificateFile());
        initializeConnection(info);
        serviceInfo = info;
    }

    private void initializeConnection(RingoServiceInfo info) {
        XMPPTCPConnectionConfiguration connectionConfiguration =
                XMPPTCPConnectionConfiguration.builder()
                .setHost(info.getServiceHostAddress())
                .setPort(info.getPort())
                .setServiceName(info.getServiceName())
                .setUsernameAndPassword("device1", "device1-123")
                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                .setCustomSSLContext(sslContext)
                .build();
        connection = new XMPPTCPConnection(connectionConfiguration);
    }

    /**
     * Creates a SSLContext that trusts our self-signed certificate
     */
    private void initializeSSLContext(InputStream sslCert) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certFile = new BufferedInputStream(sslCert);
            Certificate ca = cf.generateCertificate(certFile);
            certFile.close();

            // KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to the XMPP server and join the chat room
     */
    public void connect() {
        try {
            if (!connection.isConnected()) {
                Log.d(TAG, "Connecting to XMPP server");
                connection.connect();
                connection.login();

                multiUserChat = MultiUserChatManager.getInstanceFor(connection)
                        .getMultiUserChat(serviceInfo.getMucRoom());
                multiUserChat.join("MrStarr");
                multiUserChat.addMessageListener(this);
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Leave the chat room and disconnect from the server
     */
    public void disconnect() {
        try {
            multiUserChat.leave();
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Connection error " + e.getMessage());
        }
        connection.disconnect();
        Log.d(TAG, "Disconnected");
    }

    /**
     * Set the action to execute when a valid JSON object is received in the chat.
     * The JSON object is passed as a parameter to the DataRunnable
     * @param notificationAction Object on which the run() method will be called.
     */
    public void setNotificationAction(DataRunnable<JSONObject> notificationAction) {
        this.notificationAction = notificationAction;
    }

    /**
     * This method is called by Smack when a message is received in the chat room
     * @param message Message received
     */
    @Override
    public void processMessage(Message message) {
        Log.i(TAG, message.getBody());
        try {
            JSONObject json = new JSONObject(message.getBody());
            notificationAction.run(json);
        } catch (JSONException e) {
            Log.e(TAG, "Invalid message received. Not a JSON object. Ignoring");
        }
    }
}
