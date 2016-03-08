package ar.com.ksys.ringo.integrated;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.GregorianCalendar;
import java.util.HashMap;


import java.util.GregorianCalendar;
import java.util.HashMap;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.ksys.ringo.MainActivity;
import ar.com.ksys.ringo.R;
import ar.com.ksys.ringo.VisitActivity;
import ar.com.ksys.ringo.VisitorActivity;


@SuppressWarnings("serial")

public class Menu extends AppCompatActivity {
    // Session Manager Class
    SessionManager sesion;
    public static final String VALORES_MENU = "Configuraciones";
    Button btn_cfg;
    Button cfg_server;
    Timbre timbre;
    Switch sw_activar;
    Switch sw_sonido;
    Switch sw_casa;
    Long tiempoEnMilis;
    Long tiempoEnMilis2;
    TextView texto1;
    TextView texto2;
    private static final String TAG = "probando";
    AlertReceiver alertReceiver;
    boolean alarmUp;
    boolean alarmUp2;
    private ListView navList;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Session class instance
        sesion = new SessionManager(getApplicationContext());
        timbre = new Timbre();
        texto1= (TextView)findViewById(R.id.texto1);
        sw_activar = (Switch) findViewById(R.id.sw_activar);
        sw_sonido = (Switch)findViewById(R.id.sw_sonido);
        sw_casa = (Switch)findViewById(R.id.sw_casa);

        SharedPreferences settings = getSharedPreferences(VALORES_MENU,MODE_PRIVATE);
        sw_activar.setChecked(settings.getBoolean("Timbre activado",true));
        sw_sonido.setChecked(settings.getBoolean("Sonido activado",true));
        sw_casa.setChecked(settings.getBoolean("Estoy en casa",true));
        sw_activar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clickActivarTimbre(buttonView);
            }
        });
        sw_sonido.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clickActivarSonido(buttonView);
            }
        });

        alarmUp = (PendingIntent.getBroadcast(this, 1,
                new Intent(this, AlertReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT) != null);
        if (!alarmUp){
            sw_activar.setChecked(true);
        }

        alarmUp2 = (PendingIntent.getBroadcast(this, 2,
                new Intent(this,AlertReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT) != null);
        if (!alarmUp2){
            sw_sonido.setChecked(true);
        }
        Toast.makeText(getApplicationContext(), "Sesión iniciada: " + sesion.isLoggedIn(), Toast.LENGTH_LONG).show();

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        sesion.checkLogin();
        HashMap<String, String> user = sesion.getDetallesUsuario();
        // name
        String nombre = user.get(SessionManager.NOMBRE);
        texto1.setText("Ud ha iniciado sesión como: " +nombre);

        Log.i(TAG, "onCreate");

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.navList = (ListView) findViewById(R.id.left_drawer);
        final String[] opciones = getResources().getStringArray(R.array.nav_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, opciones);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new DrawerItemClickListener());
        //String mtitle = getTitle();
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                //R.mipmap.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.open_drawer,  /* "open drawer" description */
                R.string.close_drawer  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Selecciona opción");
                // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
            // Get text from resources
            mTitle = getResources().getStringArray(R.array.nav_options)[position];
            switch (position) {
                case 0:
                    Intent i = new Intent(Menu.this, VisitorActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 1://completar//
                    Intent k = new Intent(Menu.this, VisitActivity.class);
                    startActivity(k);
                    finish();
                    break;
                case 2:
                    Intent j = new Intent(Menu.this, MainActivity.class);
                    startActivity(j);
                    finish();
                    break;
                case 3:
                    Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                    myWebLink.setData(Uri.parse("http://192.168.1.107:8000/doorbell/api/"));
                    startActivity(myWebLink);
                    finish();
                    break;
                case 4:
                    sesion.logoutUsuario();
                    break;
            }

             // Highlight the selected item, update the title, and close the drawer
            navList.setItemChecked(position, true);
            getSupportActionBar().setTitle(mTitle);
            drawerLayout.closeDrawer(navList);
    }




    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }



    public void clickActivarTimbre(final View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            if (alarmUp){
                cancelAlarm(view, 1);
            }
            //Activar timbre
            timbre.activarTimbre(getApplicationContext());
            SharedPreferences settings = getSharedPreferences(VALORES_MENU,0);
            SharedPreferences.Editor editable = settings.edit();
            editable.putBoolean("Timbre activado", true);
            editable.commit();
        } else {
            //mostar el alert dialog
            AlertDialog.Builder alerta = new AlertDialog.Builder(view.getContext());
            alerta.setTitle("Desactivar timbre");
            alerta.setMessage("Ingrese el tiempo a desactivar el timbre en minutos");
            final EditText input = new EditText(this);
            alerta.setView(input);
            //Presionar ok
            alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface viewa, int whichButton) {
                    //Activar contador
                    String value = input.getText().toString();
                    tiempoEnMilis = Long.parseLong(value);
                    setAlarma(tiempoEnMilis, "Timbre activado", "timbre", 1);
                    SharedPreferences settings = getSharedPreferences(VALORES_MENU,0);
                    SharedPreferences.Editor editable = settings.edit();
                    //Desactivar timbre
                    timbre.desactivarTimbre(getApplicationContext());
                    editable.putBoolean("Timbre activado", false);
                    editable.commit();
                }
            });
            //Presionar cancel
            alerta.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface viewa, int whichButton) {
                    Toast.makeText(getApplicationContext(), "sigue activado", Toast.LENGTH_SHORT).show();
                    ((Switch) view).setChecked(true);
                }
            });
            alerta.show();
        }
    }

    public void clickActivarSonido (final View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            //Si esta desactivado con el contador, lo detengo
            if (alarmUp2){
                cancelAlarm(view,2);
            }
            //Activar sonido
            timbre.activarSonido(getApplicationContext());
            SharedPreferences settings = getSharedPreferences(VALORES_MENU, 0);
            SharedPreferences.Editor editable = settings.edit();
            editable.putBoolean("Sonido activado", true);
            editable.commit();
        } else {
            //mostar el alert dialog
            AlertDialog.Builder alerta = new AlertDialog.Builder(view.getContext());
            alerta.setTitle("Silenciar timbre");
            alerta.setMessage("Ingrese el tiempo a silenciar el timbre");
            final EditText input = new EditText(this);
            alerta.setView(input);
            //Presionar ok
            alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface viewa, int whichButton) {
                    //Activar contador
                    String value = input.getText().toString();
                    tiempoEnMilis2 = Long.parseLong(value);
                    setAlarma(tiempoEnMilis2, "Sonido activado", "sonido", 2);//,sw_sonido);
                    SharedPreferences settings = getSharedPreferences(VALORES_MENU,0);
                    SharedPreferences.Editor editable = settings.edit();
                    //Desactivar sonido
                    timbre.desactivarSonido(getApplicationContext());
                    editable.putBoolean("Sonido activado", false);
                    editable.commit();
                }
            });
            //Presionar cancel
            alerta.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface viewa, int whichButton) {
                    Toast.makeText(getApplicationContext(), "sigue activado", Toast.LENGTH_SHORT).show();
                    ((Switch) view).setChecked(true);
                }
            });
            alerta.show();
        }
    }



    public void setAlarma(Long tiempo,String titulo,String mensaje,int id){
        Long alertTime = new GregorianCalendar().getTimeInMillis() + tiempo * 1000;
        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra("Titulo",titulo);
        alertIntent.putExtra("Mensaje",mensaje);
        alertIntent.putExtra("Tiempo",tiempo);
        alertIntent.putExtra("Notificacion",id);
        // Allows you to schedule for your application to do something at a later date
        // even if it is in he background or isn't active
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // set() schedules an alarm to trigger
        // FLAG_UPDATE_CURRENT : Update the Intent if active
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(this, id, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        if (id==1){
            Toast.makeText(this, "timbre desactivado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "sonido desactivado", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelAlarm(View view, int id){
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(this, id, alertIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
        Toast.makeText(this, "Alarma desactivada", Toast.LENGTH_SHORT).show();
    }
}
