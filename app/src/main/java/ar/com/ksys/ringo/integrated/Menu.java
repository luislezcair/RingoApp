package ar.com.ksys.ringo.integrated;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import ar.com.ksys.ringo.MainActivity;
import ar.com.ksys.ringo.R;
import ar.com.ksys.ringo.VisitActivity;
import ar.com.ksys.ringo.VisitorActivity;
@SuppressWarnings("serial")
public class Menu extends AppCompatActivity {
    // Session Manager Class
    SessionManager sesion;
    Timbre timbre;
    Switch sw_timbre;
    Switch sw_casa;
    TextView texto1;
    public static String nombre;
    public static String password;
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
        sw_timbre = (Switch)findViewById(R.id.sw_timbre);
        sw_casa = (Switch)findViewById(R.id.sw_casa);
        sw_timbre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clickActivarTimbre(buttonView);
            }
        });
        sw_casa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clickActivarModoFueraDeCasa(buttonView);
               }
        });

        ObtenerCfg obtcfg = new ObtenerCfg();
        obtcfg.execute();

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        sesion.checkLogin();
        HashMap<String, String> user = sesion.getDetallesUsuario();
        nombre = user.get(SessionManager.NOMBRE);
        password = user.get(SessionManager.PWD);
        texto1.setText("Ud ha iniciado sesión como: " + nombre);

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.navList = (ListView) findViewById(R.id.left_drawer);
        final String[] opciones = getResources().getStringArray(R.array.nav_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, opciones);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new DrawerItemClickListener());
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
                    myWebLink.setData(Uri.parse("http://"+VisitorActivity.dirIp+"/doorbell/api/"));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        ObtenerCfg obtcfg = new ObtenerCfg();
        obtcfg.execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void clickActivarTimbre(final View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            //Activar timbre
            ModificarCfg modCfg = new ModificarCfg();
            modCfg.execute("timbre","true");
            timbre.activarTimbre(getApplicationContext());
        } else {
            //Desactivar timbre
            ModificarCfg modCfg = new ModificarCfg();
            modCfg.execute("timbre", "false");
            timbre.desactivarTimbre(getApplicationContext());
        }
    }

    public void clickActivarModoFueraDeCasa (final View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            //Activar modo fuera de casa
            ModificarCfg modCfg = new ModificarCfg();
            modCfg.execute("casa","true");
            timbre.activarModoFueraDeCasa(getApplicationContext());
        } else {
            //Desactivar modo fuera de casa
            ModificarCfg modCfg = new ModificarCfg();
            modCfg.execute("casa","false");
            timbre.desactivarModoFueradeCasa(getApplicationContext());
        }
    }

    private class ObtenerCfg extends AsyncTask<String, Integer, Boolean> {

        private Boolean doorbellStatus;
        private Boolean oOHMode;
        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet del = new HttpGet("http://"+VisitorActivity.dirIp+"/doorbell/api/configuration/1");
            String credentials = Menu.nombre + ":" + Menu.password;
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            del.addHeader("Authorization", "Basic " + base64EncodedCredentials);
            try {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONObject respJSON = new JSONObject(respStr);
                doorbellStatus = respJSON.getBoolean("doorbell_status");
                oOHMode = respJSON.getBoolean("out_of_house_mode");
            } catch (
                    Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                resul = false;
            }
            return resul;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                if (doorbellStatus){
                    sw_timbre.setChecked(true);
                }else sw_timbre.setChecked(false);
                if (oOHMode){
                    sw_casa.setChecked(true);
                }else sw_casa.setChecked(false);
            }
        }
    }

    private class ModificarCfg extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            String boton = params[0];
            String activado = params[1];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut put = new HttpPut("http://"+VisitorActivity.dirIp+"/doorbell/api/configuration/1/");
            String credentials = Menu.nombre + ":" + Menu.password;
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            put.addHeader("Authorization", "Basic " + base64EncodedCredentials);
            put.setHeader("content-type", "application/json");

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();

                if (boton.equals("timbre")){
                    if (activado.equals("true")){
                        dato.put("doorbell_status",true);
                    }else dato.put("doorbell_status",false);

                }else if (activado.equals("true")){
                    dato.put("out_of_house_mode",true);
                }else dato.put("out_of_house_mode",false);

                StringEntity entity = new StringEntity(dato.toString());
                put.setEntity(entity);
                HttpResponse resp = httpClient.execute(put);
                String respStr = EntityUtils.toString(resp.getEntity());

                if(!respStr.equals("true"))
                    resul = false;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (!result){}
        }
    }
}
