package ar.com.ksys.ringo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ar.com.ksys.ringo.service.xmpp.XMPPClientService;
import ar.com.ksys.ringo.service.discovery.DiscoveryService;


public class MainActivity extends Activity {
    //private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStopService = (Button) findViewById(R.id.buttonStopService);
        buttonStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, XMPPClientService.class);
                stopService(intent);
            }
        });

        Button buttonDiscover = (Button) findViewById(R.id.buttonDiscover);
        buttonDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DiscoveryService.class);

                Bundle serviceInfo = new Bundle();
                serviceInfo.putString("service_type", "_xmpp-server._tcp.local.");
                serviceInfo.putString("service_name", "RingoXMPPServer");
                intent.putExtra("service_info", serviceInfo);

                startService(intent);
            }
        });
    }
}
