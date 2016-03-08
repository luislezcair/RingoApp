package ar.com.ksys.ringo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.ksys.ringo.integrated.Menu;
import ar.com.ksys.ringo.service.xmpp.XMPPClientService;
import ar.com.ksys.ringo.service.discovery.DiscoveryService;


public class MainActivity extends AppCompatActivity{
    //private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar;
        //registerForContextMenu(listita);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);




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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(getApplicationContext().ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  am.getRunningTasks(10);
                List<String> backStack = new ArrayList<String>();
                Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
                while(itr.hasNext()){
                    ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo)itr.next();
                    String topActivity = runningTaskInfo.topActivity.getShortClassName();
                    backStack.add(topActivity.trim());
                }
                if(backStack!=null){
                    if(backStack.get(1).equals(".MainActivity")){
                        moveTaskToBack(true); // or finish() if you want to finish it. I don't.
                    } else {
                        Intent intent = new Intent(this,ar.com.ksys.ringo.integrated.Menu.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }

                break;
                /*i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();*/
                /*Intent intent = NavUtils.getParentActivityIntent(this);

                NavUtils.navigateUpTo(this, intent);*/
                //NavUtils.navigateUpFromSameTask(this);

                //return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        //Changes 'back' button action
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            Intent i = new Intent(this, Menu.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
