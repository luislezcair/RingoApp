package ar.com.ksys.ringo.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import ar.com.ksys.ringo.R;
import ar.com.ksys.ringo.VisitorActivity;

/**
 * Created by Escritorio on 28/02/2016.
 */
public class VisitDetails extends AppCompatActivity {
    private ImageView foto;
    private TextView nombre;
    private TextView fecha;
    private TextView hora;
    private int pos;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_detail);
        foto = (ImageView) findViewById(R.id.foto);
        nombre = (TextView)findViewById(R.id.nombre);
        fecha = (TextView)findViewById(R.id.fecha);
        hora = (TextView)findViewById(R.id.hora);
        ActionBar actionBar;
        //registerForContextMenu(listita);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pos = extras.getInt("posicion");
            name = extras.getString("nombre");
        }

        name = name.substring(name.indexOf(":") + 1);
        name = name.trim();
        if (name.contains(",")){
            nombre.setText("Nombre de los visitantes: "+name);
        }else nombre.setText("Nombre del visitante: "+name);
        nombre.setGravity(Gravity.CENTER);
        ObtenerDetallesVisita odv = new ObtenerDetallesVisita();
        odv.execute(String.valueOf(pos+1));
    }

    private class ObtenerDetallesVisita extends AsyncTask<String, Integer, Boolean> {
        private int nroVisita;
        private String contacPict;
        private String date;
        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            HttpClient httpClient = new DefaultHttpClient();
            nroVisita = Integer.parseInt(params[0]);
            HttpGet del = new HttpGet("http://"+ VisitorActivity.dirIp+"/doorbell/api/visits/"+nroVisita);
                String credentials = "ringo" + ":" + "ringo-123";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                del.addHeader("Authorization", "Basic " + base64EncodedCredentials);
                //del.setHeader("content-type", "application/json");
            try {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONObject respJSON = new JSONObject(respStr);
                if (respJSON.getString("picture").contains("http")) {
                    contacPict = respJSON.getString("picture");
                }else contacPict = "";

                date = respJSON.getString("date");

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
                String[] parts = date.split("T");
                String fe = parts[0];
                String hs = parts[1];
                fecha.setText("Fecha de la visita: "+fe);
                hora.setText("Hora de la visita: "+hs.substring(0,hs.indexOf(".")));
                if (!(contacPict.equals(""))){
                    ObtenerURLImg urlimg = new ObtenerURLImg();
                    urlimg.execute(contacPict);
                }else foto.setImageResource(R.drawable.persona);
            }
        }
    }

    private class ObtenerURLImg extends AsyncTask<String, Integer, Boolean> {

        private String url;
        private String imgUrl;
        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            HttpClient httpClient = new DefaultHttpClient();
            url = params[0];
            HttpGet del = new HttpGet(url);
            String credentials = "ringo" + ":" + "ringo-123";
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            del.addHeader("Authorization", "Basic " + base64EncodedCredentials);
            //del.setHeader("content-type", "application/json");
            try {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONObject respJSON = new JSONObject(respStr);
                imgUrl = respJSON.getString("picture");
            } catch (
                Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                resul = false;
            }
            return resul;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                new DownloadImageTask(foto).execute(imgUrl);
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
