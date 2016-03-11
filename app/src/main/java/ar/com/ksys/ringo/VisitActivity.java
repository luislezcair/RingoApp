package ar.com.ksys.ringo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import ar.com.ksys.ringo.integrated.CustomArrayAdapter;
import ar.com.ksys.ringo.service.VisitDetails;

/**
 * Created by Escritorio on 23/02/2016.
 */
public class VisitActivity extends AppCompatActivity {

    ListView listita;
    private ArrayList<String> listaUrls;
    private ArrayList<ArrayList<String>> listOfList;
    private JSONArray arry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        listita = (ListView) findViewById(R.id.listita);
        ActionBar actionBar;
        registerForContextMenu(listita);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        listaUrls = new ArrayList<String>();

        ObtenerURL obturl = new ObtenerURL();
        obturl.execute();
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
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        //Changes 'back' button action
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            Intent i = new Intent(this, ar.com.ksys.ringo.integrated.Menu.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public static void hacerIntent(Context context,int pos,ArrayList<String> st){
        Intent i = new Intent(context, VisitDetails.class);
        i.putExtra("posicion",pos);
        i.putExtra("nombre", st.get(pos));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public class ObtenerURL extends AsyncTask<String, Integer, Boolean> {

        private String url;
        private int i;
        private int j;

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            listaUrls = new ArrayList<String>();
            for (j = 1; j < 200; j++) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet del = new HttpGet("http://"+VisitorActivity.dirIp+"/doorbell/api/visits/?page=" + j);
                String credentials = "ringo" + ":" + "ringo-123";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                del.addHeader("Authorization", "Basic " + base64EncodedCredentials);
                try {
                    HttpResponse resp = httpClient.execute(del);
                    String respStr = EntityUtils.toString(resp.getEntity());
                    JSONObject respJSON = new JSONObject(respStr);
                    arry = respJSON.optJSONArray("results");
                    if (arry == null) {
                        break;
                    }
                    for (i = 0; i < arry.length(); i++) {
                        JSONObject obj = arry.getJSONObject(i);
                        url = obj.getString("visitors");
                        String urlDecoded = url.replaceAll("\\\\", "").replaceAll("\\[|\\]", "").replaceAll("\\s+","").replaceAll("\"", "");
                        listaUrls.add(urlDecoded);
                    }
                } catch (Exception ex) {
                    Log.e("ServicioRest", "Error!", ex);
                    resul = false;
                }
            }
            return resul;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                listOfList = armarListaDeListas(listaUrls, ",");
                for(int k = 0;k < listOfList.size(); k++){
                    for (int l = 0; l < listOfList.get(k).size(); l++){
                        ObtenerNombres obtnames = new ObtenerNombres();
                       obtnames.execute(listOfList.get(k).get(l), String.valueOf(k), String.valueOf(l));
                    }
                }
            }
        }
    }

    private class ObtenerNombres extends AsyncTask<String, Integer, Boolean> {

        private String nombre;
        private String url;
        private int i;
        private int j;

        protected Boolean doInBackground(String... params) {
            boolean resul = true;
            HttpClient httpClient = new DefaultHttpClient();
            url = params[0];
            i = Integer.parseInt(params[1]);
            j = Integer.parseInt(params[2]);
            HttpGet del = new HttpGet(url);
            String credentials = "ringo" + ":" + "ringo-123";
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            del.addHeader("Authorization", "Basic " + base64EncodedCredentials);
            try {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONObject respJSON = new JSONObject(respStr);
                nombre = respJSON.getString("name");
                ArrayList<String> rta= new ArrayList<>();
                rta.add(nombre);
                listOfList.get(i).set(j, nombre);
                } catch (Exception ex) {
                    listOfList.get(i).set(j,"Visitante desconocido");
                    Log.e("ServicioRest", "Error!", ex);
                    resul = false;
            }
            return resul;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                listaUrls = concatenarStrings(listOfList,listaUrls);
                CustomArrayAdapter adaptador = new CustomArrayAdapter(getApplicationContext(),listaUrls);
                listita.setAdapter(adaptador);
            }
        }
    }

    private ArrayList<ArrayList<String>> armarListaDeListas(ArrayList<String> al, String s) {
        ArrayList<ArrayList<String>> lOl = new ArrayList<ArrayList<String>>();
        ArrayList<String> parts = new ArrayList<String>();
        for (int i = 0; i < al.size(); i++) {
            if (al.get(i).contains(s)){
                parts = separarString(al.get(i));
                lOl.add(parts);
            }else{
                ArrayList<String> prueba = new ArrayList<>();
                prueba.add(al.get(i));
                lOl.add(prueba);
            }
        }
        return lOl;
    }

    public ArrayList<String> separarString(String st){
        String[] partes = st.split(",");
        ArrayList<String> arrayPartes = new ArrayList<String>(Arrays.asList(partes));
        return arrayPartes;
    }

    public ArrayList<String> concatenarStrings(ArrayList<ArrayList<String>> al,ArrayList<String>array){
        String salida;
        for(int i = 0;i < al.size(); i++){
            salida = String.valueOf(i + 1)+": ";
            for (int j = 0; j < al.get(i).size(); j++){
                if (j==0){
                    salida = salida.concat(al.get(i).get(j));
                }else{
                    salida = salida.concat(", "+al.get(i).get(j));
                }
            }
            array.set(i,salida);
        }
        return array;
    }
}
