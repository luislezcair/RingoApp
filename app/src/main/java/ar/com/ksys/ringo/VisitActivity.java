package ar.com.ksys.ringo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Escritorio on 23/02/2016.
 */
public class VisitActivity extends AppCompatActivity {

    ListView listita;
    private ArrayList<String> listaUrls;
    private ArrayList<ArrayList<String>> listOfList;
    private JSONArray arry;
    private ArrayAdapter<String> adaptador;

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

    private class ObtenerURL extends AsyncTask<String, Integer, Boolean> {

        private String url;
        private int i;
        private int j;

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            listaUrls = new ArrayList<String>();
            for (j = 1; j < 200; j++) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet del = new HttpGet("http://192.168.1.102:8000/doorbell/api/visits/?page=" + j);
                String credentials = "ringo" + ":" + "ringo-123";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                del.addHeader("Authorization", "Basic " + base64EncodedCredentials);
                //del.setHeader("content-type", "application/json");
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
                //Rellenamos la lista con los nombres de los clientes
                //Rellenamos la lista con los resultados
                adaptador = new ArrayAdapter<String>(VisitActivity.this,
                        android.R.layout.simple_list_item_1, listaUrls);
                listita.setAdapter(adaptador);
                //Log.i("tag", String.valueOf(listaUrls.size()));

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
                } catch (
                    Exception ex
                    ) {
                Log.e("ServicioRest", "Error!", ex);
                resul = false;
            }
            return resul;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                //Rellenamos la lista con los nombres de los clientes
                //Rellenamos la lista con los resultados
                listaUrls = concatenarStrings(listOfList,listaUrls);
                adaptador = new ArrayAdapter<String>(VisitActivity.this,
                        android.R.layout.simple_list_item_1, listaUrls);
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
