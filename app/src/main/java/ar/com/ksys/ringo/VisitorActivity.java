package ar.com.ksys.ringo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VisitorActivity extends AppCompatActivity {
    Button btnVisitante;
    Button btnLista;
    Button btnInsertar;
    EditText texto;
    TextView resultado;
    ListView listita;
    private String urlToDelete;
    private String urlToModify;
    private int index;
    private ArrayList<String> listaFiltrada;
    private ArrayList<String> listaCompleta;
    private ArrayAdapter<String> adaptador;
    //private ArrayAdapter<String> adaptadorCompleto;
    private ListAdapter la;

    private String[] visitantes;
    private JSONArray arry;
    private static final String TAG = VisitorActivity.class.getSimpleName();
    private ImageView visitorPictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        btnVisitante = (Button)findViewById(R.id.btnVisitante);
        btnLista = (Button)findViewById(R.id.btnLista);
        btnInsertar = (Button)findViewById(R.id.btnInsertar);
        texto = (EditText)findViewById(R.id.idTexto);
        resultado = (TextView)findViewById(R.id.idResultado);
        listita = (ListView)findViewById(R.id.listita);
        ActionBar actionBar;
        registerForContextMenu(listita);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);










        //new ObtenerVisitante().execute("http://192.168.1.101:8000/doorbell/api/visitors");
        //ObtenerVisitante visitante = new ObtenerVisitante(this, "http://192.168.1.105:8000/doorbell/api/visitors/56/");
        //visitante.execute();




        //visitorPictureView = (ImageView) findViewById(R.id.imageView);

        //URL pictureUrl = (URL) getIntent().getSerializableExtra("url");
        //new PictureDownloader().execute(pictureUrl);

        btnLista.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ListarVisitantes tarea = new ListarVisitantes();
                tarea.execute();
            }
        });

        btnVisitante.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ObtenerVisitante tarea = new ObtenerVisitante();
                tarea.execute(texto.getText().toString());
            }
        });

        btnInsertar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*InsertarVisitante tarea = new InsertarVisitante();
                tarea.execute(
                        texto.getText().toString());*/
                        //txtTelefono.getText().toString());
                mostrarAlertDialog("Nuevo visitante","ingrese el nombre del nuevo visitante");
            }
        });
    }



    /**
     * Task that will execute on a separate thread to download the image from
     * the server and show it on the screen.
     */
    /*private class PictureDownloader extends AsyncTask<URL, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            Bitmap picture = null;
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                picture = BitmapFactory.decodeStream(in);
            } catch(IOException e) {
                Log.e(TAG, "Connection to media server failed");
            }
            return picture;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            visitorPictureView.setImageBitmap(bitmap);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listitem_menu, menu);

        /*if (v.getId()==R.id.listita) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(visitantes[info.position]);
            menu.add(Menu.NONE, R.id.edit, Menu.NONE, "Editar visitante");
            menu.add(Menu.NONE, R.id.delete, Menu.NONE, "Eliminar visitante");
        }*/
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        index=info.position;
        la= listita.getAdapter();



        switch (item.getItemId()) {
            case R.id.edit:
                //Toast.makeText(getApplicationContext(),"click en editar",Toast.LENGTH_SHORT).show();

                mostrarAlertDialog("Editar visitante","Nombre del visitante");
                /*new AlertDialog.Builder(this)
                        .setTitle("Eliminar")
                        .setMessage("¿Desea eliminar el visitante?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (la.getCount()==listaCompleta.size()){
                                    modifyItem(index,listaCompleta);
                                }else if (la.getCount()==listaFiltrada.size()) {
                                    modifyItem(index,listaFiltrada);
                                } ;
                            }})
                        .setNegativeButton(android.R.string.no, null).show();*/


                return true;
            case R.id.delete:
                //Toast.makeText(getApplicationContext(),"click en eliminar",Toast.LENGTH_SHORT).show();
                //if ((listita.)

                new AlertDialog.Builder(this)
                        .setTitle("Eliminar")
                        .setMessage("¿Desea eliminar el visitante?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (la.getCount()==listaCompleta.size()){
                                   urlToDelete= getUrl(index,listaCompleta);
                                   listaCompleta.remove(index);

                                }else if (la.getCount()==listaFiltrada.size()) {
                                    urlToDelete=getUrl(index,listaFiltrada);
                                    listaFiltrada.remove(index);
                                }
                                EliminarVisitante tarea = new EliminarVisitante();
                                tarea.execute(urlToDelete);
                                listita.requestLayout();;
                            }})
                        .setNegativeButton(android.R.string.no, null).show();


                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    private String getUrl(int i,ArrayList<String> al) {
        //String urltodelate= visitantes[i].valueOf(url);
        String data;
        data = al.get(i);
        int index = 0;

        Pattern pattern = Pattern.compile("URL:");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            index = matcher.end();
        }

        String url = data.substring(index);
        return url;
    }
        //Toast.makeText(getApplicationContext(),urlToDelete,Toast.LENGTH_SHORT).show();


        /*a.remove(mi);
        a.notifyDataSetChanged();*/



    private class EliminarVisitante extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            String url = params[0];

            HttpDelete del = new HttpDelete(url);

            String credentials = "ringo" + ":" + "ringo-123";
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            del.addHeader("Authorization", "Basic " + base64EncodedCredentials);

            del.setHeader("content-type", "application/json");

            try
            {
                HttpResponse resp = httpClient.execute(del);
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

            if (!result)
            {
                Toast.makeText(getApplicationContext(),"El visitante ha sido eliminado correctamente",Toast.LENGTH_SHORT).show();
            }
        }
    }



    private class ObtenerVisitante extends AsyncTask<String,Integer,Boolean> {

        private String[] visitantes;
        private int idCli;
        private String nombCli;
        private int telefCli;
        private Integer count;
        private String nombre;

        private String url;

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            listaFiltrada = new ArrayList<String>();
            count = 0;

            for (int j = 1; j < 200; j++) {


                HttpClient httpClient = new DefaultHttpClient();

                nombre = params[0];

                HttpGet del =
                        new HttpGet("http://192.168.1.106:8000/doorbell/api/visitors/?page="+j);

                String credentials = "ringo" + ":" + "ringo-123";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                del.addHeader("Authorization", "Basic " + base64EncodedCredentials);

                //del.setHeader("content-type", "application/json");

                try {
                    HttpResponse resp = httpClient.execute(del);
                    String respStr = EntityUtils.toString(resp.getEntity());

                    JSONObject respJSON = new JSONObject(respStr);

                    arry = respJSON.optJSONArray("results");
                    if (arry==null){
                        break;
                    }

                    //visitantes = new String[arry.length()];


                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject obj = arry.getJSONObject(i);

                        //int idVisita = obj.getInt("id");
                        if (obj.has("name")) {
                            nombCli = obj.getString("name");
                            url = obj.getString("url");
                        }

                        Pattern pattern = Pattern.compile(nombre.toLowerCase());
                        Matcher matcher = pattern.matcher(nombCli.toLowerCase());
                        if (matcher.find() && !nombre.isEmpty()) {
                            listaFiltrada.add("Nombre: " + nombCli + "\r\nURL:" + url);
                            count++;
                        }

                    /*if (nombCli.toLowerCase().equals(nombre)){


                    //visitantes[i]= *//*idVisita*//* "Nombre:" + nombCli;
                    };*/

                /*if (respJSON.has("name")) {
                    nombCli = respJSON.getString("name");  */
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

                 adaptador =
                        new ArrayAdapter<String>(VisitorActivity.this,
                                android.R.layout.simple_list_item_1, listaFiltrada);

                listita.setAdapter(adaptador);
                if (count!=0) {
                    resultado.setText("Existen " +count.toString()+" visitas a nombre de: "+nombre);
                } else resultado.setText("No existe visita registrada de ese visitante");
            }
        }
    }

    private class ListarVisitantes extends AsyncTask<String,Integer,Boolean> {

        private String url;



        protected Boolean doInBackground(String... params) {
            boolean resul = true;
            listaCompleta = new ArrayList<String>();
            for (int j = 1; j < 200; j++) {


                HttpClient httpClient = new DefaultHttpClient();

                HttpGet del = new HttpGet("http://192.168.1.106:8000/doorbell/api/visitors/?page="+j);

                String credentials = "ringo" + ":" + "ringo-123";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                del.addHeader("Authorization", "Basic " + base64EncodedCredentials);

                //del.setHeader("content-type", "application/json");

                try {
                    HttpResponse resp = httpClient.execute(del);
                    String respStr = EntityUtils.toString(resp.getEntity());


                    JSONObject respJSON = new JSONObject(respStr);


                    arry = respJSON.optJSONArray("results");
                    if (arry==null){
                        break;
                    }


                    //visitantes = new String[arry.length()];

                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject obj = arry.getJSONObject(i);

                        //int idVisita = obj.getInt("id");
                        String nombCli = obj.getString("name");
                        url = obj.getString("url");

                        listaCompleta.add("Nombre: " + nombCli + "\r\nURL:" + url);


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

            if (result)
            {
                //Rellenamos la lista con los nombres de los clientes
                //Rellenamos la lista con los resultados
                adaptador = new ArrayAdapter<String>(VisitorActivity.this,
                                android.R.layout.simple_list_item_1, listaCompleta);

                listita.setAdapter(adaptador);
                setListViewHeightBasedOnChildren(listita);


            }
        }



    }




    private class InsertarVisitante extends AsyncTask<String,Integer,Boolean> {
        private String nombre;

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://192.168.1.106:8000/doorbell/api/visitors/");
            String credentials = "ringo" + ":" + "ringo-123";
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            post.addHeader("Authorization", "Basic " + base64EncodedCredentials);
            post.setHeader("content-type", "application/json");
            nombre = params[0];

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();



                //dato.put("Id", Integer.parseInt(txtId.getText().toString()));
                dato.put("name", nombre);
                if (params[1].equals("true")){
                    dato.put("welcome",true);
                }else dato.put("welcome",false);
                //dato.put("welcome",params[1]);

                StringEntity entity = new StringEntity(dato.toString());
                post.setEntity(entity);

                HttpResponse resp = httpClient.execute(post);
                String respStr = EntityUtils.toString(resp.getEntity());
                //Toast.makeText(getApplicationContext(),"toast de prueba",Toast.LENGTH_SHORT).show();


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

            if (!result)
            {
                Toast.makeText(getApplicationContext(),nombre+ " fue registrado con éxito",Toast.LENGTH_SHORT).show();
                listita.requestLayout();
                //resultado.setText("Insertado OK.");
            }
        }
    }

    private class ActualizarVisitante extends AsyncTask<String,Integer,Boolean> {
        private String nombre;

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            String URL =params[0];

            HttpPut put = new HttpPut(URL);
            String credentials = "ringo" + ":" + "ringo-123";
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            put.addHeader("Authorization", "Basic " + base64EncodedCredentials);
            put.setHeader("content-type", "application/json");
            nombre = params[1];

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();

                dato.put("name", nombre);
                if (params[2].equals("true")){
                    dato.put("welcome",true);
                }else dato.put("welcome",false);

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

            if (!result)
            {
                Toast.makeText(getApplicationContext(),nombre+ " fue modificado con éxito",Toast.LENGTH_SHORT).show();
                listita.requestLayout();
            }
        }
    }





    public void mostrarAlertDialog(final String titulo,String mensaje) {
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText("Bienvenido");
        checkBox.setChecked(true);
        final EditText input = new EditText(this);
        input.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(input);
        linearLayout.addView(checkBox);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);

        //alertDialogBuilder.setView(input);

        //alertDialogBuilder.setView(checkBox);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // do something
                String nombre = input.getText().toString();
                String bienvenido;
                if (checkBox.isChecked()) {
                    bienvenido = "true";
                } else
                    bienvenido = "false";
                if (titulo.equals("Nuevo visitante")) {
                    InsertarVisitante tarea = new InsertarVisitante();
                    tarea.execute(nombre, bienvenido);
                } else if (titulo.equals("Editar visitante")) {
                    if (la.getCount() == listaCompleta.size()) {
                        urlToModify = getUrl(index, listaCompleta);
                    } else if (la.getCount() == listaFiltrada.size()) {
                        urlToModify = getUrl(index, listaFiltrada);
                    }
                    ActualizarVisitante tarea = new ActualizarVisitante();
                    tarea.execute(urlToModify,nombre,bienvenido);
                    listita.requestLayout();
                    ;
                }
            }
        });
        alertDialogBuilder.setNegativeButton(android.R.string.no, null);
        alertDialogBuilder.show();
    }



    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



}



