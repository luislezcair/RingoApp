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
    private ArrayList<String> partes;
    private ArrayList<String> listaPartes;
    private JSONArray arry;
    private ArrayAdapter<String> adaptador;
    private static final String TAG = "probando2";
    private String concat;


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


        ObtenerURL obturl = new ObtenerURL();
        obturl.execute();
        //armarListaDeLista();
        //obtenerNombre();
        //obtenerString();


        /*int size=listaUrls.size();
        Toast.makeText(getApplicationContext(),Integer.toString(size),Toast.LENGTH_SHORT).show();*/


        /*for (int i = 1; i < listaUrls.size(); i++) {
            ObtenerNombres obtnames = new ObtenerNombres();
            obtnames.execute(listaUrls.get(i));
        }*/
            //obtenerStrings(listOfList);
    }

    private class ObtenerURL extends AsyncTask<String, Integer, Boolean> {
        private String url;
        private int i;
        private int j;


        protected Boolean doInBackground(String... params) {
            boolean resul = true;
            listaUrls = new ArrayList<String>();
            //listOfList = new ArrayList<String[]>();

            for (int j = 1; j < 200; j++) {


                HttpClient httpClient = new DefaultHttpClient();

                HttpGet del = new HttpGet("http://192.168.1.103:8000/doorbell/api/visits/?page=" + j);

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


                    //visitantes = new String[arry.length()];

                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject obj = arry.getJSONObject(i);

                        //int idVisita = obj.getInt("id");
                        //String nombCli = obj.getString("name");
                        /*Gson gson= new Gson();
                        gson.fromJson(url,"UTF-8");*/
                        url = obj.getString("visitors");
                        String urlDecoded = url.replaceAll("\\\\", "").replaceAll("\\[|\\]", "").replaceAll("\\s+","").replaceAll("\"", "");




                        //url.replace("\\","");




                       /* ObtenerVisitante tarea = new ObtenerVisitante();
                        tarea.execute(url);*/

                        listaUrls.add(/*"Nombre: " + nombCli + "\r\nURL:" +*/ urlDecoded);



                        //visitantes[i] = /*idVisita*/ "Nombre:" + nombCli+" URL: "+url;
                    }
                } catch (Exception ex) {
                    Log.e("ServicioRest", "Error!", ex);
                    resul = false;
                }


            }
            return resul;
        }

        protected void onPostExecute(Boolean result) {
                ArrayList<String> partes = new ArrayList<String>();
            listOfList= new ArrayList<ArrayList<String>>();
            if (result) {
                //Rellenamos la lista con los nombres de los clientes
                //Rellenamos la lista con los resultados
                adaptador = new ArrayAdapter<String>(VisitActivity.this,
                        android.R.layout.simple_list_item_1, listaUrls);

                listita.setAdapter(adaptador);
                //Toast.makeText(getApplicationContext(),listaUrls.get(1), Toast.LENGTH_LONG).show();

               /* String prueba = listaUrls.get(1);
                //String prueba2=prueba.replaceAll("\\s+","");
                ObtenerNombres obtnames = new ObtenerNombres();
                obtnames.execute(prueba);*/
                //Log.i("size",String.valueOf(listaUrls.size()));
                //concat="";
                for (i = 0; i < listaUrls.size(); i++) {

                    if (listaUrls.get(i).contains(",")){
                        partes = separarString(listaUrls.get(i));
                        //Log.i(TAG,partes[0]);
                        //Log.i(TAG, partes[1]);

                      //  for (j = 0; j < partes.size(); j++){


                           /* if (j==0){
                                listaPartes.set(j,concat);
                            }else{*//*
                            listaPartes.add(j,concat);*/
                            listOfList.add(partes);

                           /* listOfList.add(partes);
                            ObtenerNombres obtnames = new ObtenerNombres();
                            obtnames.execute(listOfList.get(i)[j],String.valueOf(i),String.valueOf(j));*/

                            //Log.i("urls", listOfList.get(j) + " " + String.valueOf(i) + " " + String.valueOf(j));
                        //}


                    }else{
                         j=0;
                        ArrayList<String> prueba = new ArrayList<>();
                        prueba.add(listaUrls.get(i));
                        //partes.add(listaUrls.get(i));
                        //Log.i("prueba",partes.get(i));
                        /*String[] st = new String[1];
                        st[j]=listaUrls.get(i);*/
                        listOfList.add(prueba);
                        /*ObtenerNombres obtnames = new ObtenerNombres();
                        obtnames.execute(listOfList.get(i).get(j),String.valueOf(i),String.valueOf(j));*/
                        //partes.clear();
                    }

                   //


                //setListViewHeightBasedOnChildren(listita);
                }

                for(int k = 0;k < listOfList.size(); k++){
                    for (int l = 0; l < listOfList.get(k).size(); l++){
                        ObtenerNombres obtnames = new ObtenerNombres();
                        obtnames.execute(listOfList.get(k).get(l), String.valueOf(k), String.valueOf(l));
                        //Log.i("urls", listOfList.get(k).get(l));
                    }

                }
                // + " " + String.valueOf(i) +" "+ String.valueOf(listOfList.get(i).size()));
                //Log.i("prueba",listOfList.get(2).get(1)+" "+listOfList.get(4).get(2)+" "+listOfList.get(2).get(0));
                /*Log.i("partes",partes[0]);
                Log.i("partes",partes[1]);
                Log.i("partes",partes[2]);
                //Log.i("partes",partes[3]);
                Log.i("urls", listOfList.get(0)[0] + " " + String.valueOf(i) + " " + String.valueOf(j));
                Log.i("urls", listOfList.get(1)[0] + " " + String.valueOf(i) + " " + String.valueOf(j));
                Log.i("urls", listOfList.get(2)[0] + " " + String.valueOf(i) + " " + String.valueOf(j));
                Log.i("urls", listOfList.get(2)[1] + " " + String.valueOf(i) + " " + String.valueOf(j));
                Log.i("urls", listOfList.get(3)[0] + " " + String.valueOf(i) + " " + String.valueOf(j));
                Log.i("urls", listOfList.get(4)[0] + " " + String.valueOf(i) + " " + String.valueOf(j));
                Log.i("urls", listOfList.get(4)[1] + " " + String.valueOf(i) + " " + String.valueOf(j));
                Log.i("urls", listOfList.get(4)[2] + " " + String.valueOf(i) + " " + String.valueOf(j));*/

                Log.i("urls",listOfList.get(4).get(1)+" "+ listOfList.get(2).size()+" "+listOfList.get(3).size()+listOfList.get(4).size());
            }

        }
    }

    private class ObtenerNombres extends AsyncTask<String, Integer, Boolean> {
        private String nombre;
        //private String urlJson;
        private String url;
        private int i;
        private int j;
        //private String concatLoop;

        protected Boolean doInBackground(String... params) {
            boolean resul = true;
            //listaNombres = new ArrayList<String>();

            //for (int j = 1; j < 200; j++) {
            HttpClient httpClient = new DefaultHttpClient();
            url = params[0];
            i = Integer.parseInt(params[1]);
            j = Integer.parseInt(params[2]);
            //concat = params[1];
            //listaNombres = new ArrayList<String>();
            //String urlDecoded = url.replaceAll("\\\\", "").replaceAll("\\[|\\]", "").replaceAll("\\s+","").replaceAll("\"", "");

            //int posicion = params[1];


            HttpGet del = new HttpGet(url);

            String credentials = "ringo" + ":" + "ringo-123";
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            del.addHeader("Authorization", "Basic " + base64EncodedCredentials);

            //del.setHeader("content-type", "application/json");

            try {

                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                //Log.i("json",respStr);
                JSONObject respJSON = new JSONObject(respStr);
                //posicion = listaUrls.indexOf(url);

                    nombre = respJSON.getString("name");
                ArrayList<String> rta= new ArrayList<>();
                rta.add(nombre);
                //Log.i("asda",rta.get(0));

                /*String[] rta = new String[1];
                rta[j]=nombre;*/
                //concat =concat.concat(nombre);
                //listOfList.set(i,rta);
                listOfList.get(i).set(j, nombre);
                //Log.i("urls", listOfList.get(i) + " " + String.valueOf(i) + " " + String.valueOf(j));


                //listaNombres.add(nombre);
                //Log.i("concatenacion", concat);
                    //listaUrls.add(posicion,nombre);






                    /*arry = respJSON.optJSONArray("results");
                    if (arry == null) {
                        break;
                    }*/

                //JSONObject obj = respJSON.getJSONObject;




                //visitantes = new String[arry.length()];

/*
                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject obj = arry.getJSONObject(i);*/

                        //int idVisita = obj.getInt("id");
                        //urlJson = respJSON.getString("url");
                        /*if (urlJson == url) {*/

                            //url = obj.getString("visitors");

                            //listaNombres.add(nombre);



                        /*}else{
                            listaUrls.set(posicion,"no encuentra nada");
                        }*/
                //listaCompleta.add("Nombre: " + nombCli + "\r\nURL:" + url);


                //visitantes[i] = *//*idVisita*//* "Nombre:" + nombCli+" URL: "+url;
            } catch (
                    Exception ex
                    ) {
                Log.e("ServicioRest", "Error!", ex);
                resul = false;
            }
            // }
            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                //Rellenamos la lista con los nombres de los clientes
                //Rellenamos la lista con los resultados
                //concat=concatLoop;
                //Log.i("lista", listaUrls.get(posicion));
                //listaUrls.add(posicion, listaNombres.get(0));

                //obtenerStrings(listOfList);

                adaptador = new ArrayAdapter<String>(VisitActivity.this,
                        android.R.layout.simple_list_item_1, listaUrls);

                listita.setAdapter(adaptador);
                //Toast.makeText(getApplicationContext(),urlJson+ url, Toast.LENGTH_LONG).show();
                //setListViewHeightBasedOnChildren(listita);




            }
        }
    }

    public ArrayList<String> separarString(String st){
        String[] partes = st.split(",");
        //ArrayList<String> listaPartes = new ArrayList<String>(Arrays.asList(partes));
        ArrayList<String> arrayPartes = new ArrayList<String>(Arrays.asList(partes));

        //Log.i("valor",String.valueOf(partes.length));
        return arrayPartes;
    }

    public String obtenerStrings(ArrayList<ArrayList<String>> al){
        String salida ="";
        for(int i = 0;i < al.size(); i++){
            //salida = "";
            for (int j = 0; j < al.get(i).size(); j++){
                salida = salida.concat(al.get(i).get(j));
            }
        }
        return salida;
    }

}
