package Negocio;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.AsyncTaskLoader;
import android.text.Html;
import android.transition.Fade;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Presistencia.PlayaDB;

/**
 * Created by arias on 17/02/2016.
 */
public class SimpleLoader extends AsyncTaskLoader<Void> {

    private static final String URL_JSON = "https://www.turismoasturias.es/open-data/catalogo-de-datos?p_p_id=opendata_WAR_importportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=exportJson&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_count=1&_opendata_WAR_importportlet_structure=27514&_opendata_WAR_importportlet_robots=nofollow";
    private static final String URL_BASE = "https://www.turismoasturias.es";
    private static final String TAG = "PlayaApp";
    private final Context context;

    public SimpleLoader(Context _context) {
        super(_context);
        context=_context;
    }

    @Override
    public Void loadInBackground() {

        //Descargar las playas
        String json = null;

        Log.d(TAG, "Descargando datos...");

        try {
            json= downloadUrl(URL_JSON);
            Log.d(TAG, "Parseando datos...");

            descargaDatos(new JSONObject(json));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Fin descarga.");

        return null;
    }

    public void descargaDatos(JSONObject response){
        PlayaDB db = PlayaDB.getInstance(context);

        try {
            JSONObject ob = response.getJSONObject("articles");
            JSONArray a = ob.getJSONArray("article");


            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);

                final String nombre = o.getJSONArray("dynamic-element").getJSONObject(0).getJSONObject("dynamic-content").getString("content");

                JSONArray detalles = o.getJSONArray("dynamic-element").getJSONObject(1).getJSONArray("dynamic-element");

                //----------------------------------------------------------------------------------------------------------------------
                //Obtener propiedades basicas (Comunes a todas las playas)
                //----------------------------------------------------------------------------------------------------------------------
                final List<String> servicios = new ArrayList<String>();
                List<String> imagenes = new ArrayList<String>();

                final String zona = detalles.getJSONObject(0).getJSONObject("dynamic-content").getString("content");
                final String descripcion = Html.fromHtml(getPropiedad(detalles.getJSONObject(1), "Sin información")).toString().replaceAll("\"", "'").trim();
                final boolean banderaAzul = new Boolean(getPropiedad(detalles.getJSONObject(2), "false"));
                final boolean qCalidad = new Boolean(getPropiedad(detalles.getJSONObject(3), "false"));
                final String concejo = getPropiedad(detalles.getJSONObject(4), "Sin información");
                final String accesos = Html.fromHtml(getPropiedad(detalles.getJSONObject(5), "Sin información")).toString().trim();
                final String tipo = Html.fromHtml(getPropiedad(detalles.getJSONObject(6), "Sin información")).toString().trim();
                servicios.add(getPropiedad(detalles.getJSONObject(7), "Sin información"));
                final String longitud = getPropiedad(detalles.getJSONObject(8), "Longitud desconocida");
                final String observaciones = Html.fromHtml(getPropiedad(detalles.getJSONObject(9), "No hay observaciones")).toString().trim();
                //----------------------------------------------------------------------------------------------------------------------

                for (int j = 10; j < detalles.length(); j++) {
                    servicios.add(getPropiedad(detalles.getJSONObject(j), "Desconocido"));
                }
                //----------------------------------------------------------------------------------------------------------------------

                final String coordenadas = o.getJSONArray("dynamic-element").getJSONObject(3).getJSONObject("dynamic-element").getJSONObject("dynamic-content").getString("content");

                imagenes = getImagenes(o);

                Playa p = new Playa(nombre, descripcion, zona, concejo, accesos, tipo, longitud, observaciones, coordenadas, banderaAzul, qCalidad, servicios, null);

                int idPlaya = db.creaPlaya(p);

                if (idPlaya != -1) {
                    descargaImagen(imagenes.get(0), idPlaya);
                } else {
                    // Log.d(TAG, "Playa ya creada, se omite");
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void descargaImagen(String url, final int id) {


        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                try{
                    String path = context.getFilesDir().toString();
                    OutputStream fOut = null;
                    File file = new File(path, "Playa_"+id+".jpg"); // the File to save to
                    fOut = new FileOutputStream(file);

                    if (Build.VERSION.SDK_INT <= 15) {
                        response.compress(Bitmap.CompressFormat.JPEG, 50, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    }

                    fOut.flush();
                    fOut.close(); // do not forget to close the stream

                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //do stuff
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(imgRequest);


    }


    private List<String> getImagenes(JSONObject o) {

        List<String> imagenes = new ArrayList<String>();

        // Si hay una lista de imagenes
        try{
            JSONArray img = o.getJSONArray("dynamic-element").getJSONObject(4).getJSONArray("dynamic-element");

            for(int j=0 ;j<img.length();j++){
                String imagen = ((JSONObject) img.get(j)).getJSONObject("dynamic-content").getString("content");
                imagenes.add(URL_BASE + imagen);
            }

            return imagenes;
        }catch (Exception e){

        }

        // Si hay una sola imagen

        try{
            String imagen = o.getJSONArray("dynamic-element").getJSONObject(4).getJSONObject("dynamic-element").getJSONObject("dynamic-content").getString("content");
            imagenes.add(URL_BASE+imagen);

            return imagenes;

        }catch (Exception e) {

        }

        return imagenes;

    }

    private InputStream openHttpInputStream(String myUrl)
            throws MalformedURLException, IOException, ProtocolException {
        InputStream is;
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // Aquí se hace realmente la petición
        conn.connect();
        is = conn.getInputStream();
        return is;
    }


    private String downloadUrl(String myUrl) throws IOException {
        InputStream is = null;
        try {
            is = openHttpInputStream(myUrl);
            return streamToString(is);
        } finally {
            // Asegurarse de que el InputStream se cierra
            if (is != null) {
                is.close();
            }
        }
    }
    // Pasa un InputStream a un String
    public String streamToString(InputStream stream) throws IOException,
            UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int length = 0;
        do {
            length = stream.read(buffer);
            if (length != -1) {
                baos.write(buffer, 0, length);
            }
        } while (length != -1);
        return baos.toString("UTF-8");
    }

    private String getPropiedad(JSONObject o, String val) {

        String ret;
        try {
            ret = o.getJSONObject("dynamic-content").getString("content");
            return ret;
        }

        catch (JSONException e) {

        }

        try {
            o.getJSONObject("dynamic-content");
            return val;
        }

        catch (JSONException e) {

        }

        try {
            ret = o.getString("dynamic-content");
            return ret;
        }

        catch (JSONException e) {

        }

        return val;
    }



}
