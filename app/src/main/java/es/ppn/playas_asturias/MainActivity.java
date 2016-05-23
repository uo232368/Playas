package es.ppn.playas_asturias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import GUI.Detalle;
import GUI.Favoritas;
import GUI.Informacion;
import GUI.Lista;
import GUI.Mapa;
import Presistencia.PlayaDB;
import Negocio.NukeSSLCerts;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Lista.ListaRefresh, SearchView.OnQueryTextListener {


    private static final String TAG = "PlayaApp";

    private static final String BUNDLE_PLAYA = "playas";

    private static final String BUSQUEDA = "busqueda";
    private static final String FRAGMENTO_ACTUAL ="fragmento_actual" ;

    private static final String TAG_FRAGMENT_LISTA ="fragment_lista" ;
    private static final String TAG_FRAGMENT_MAPA = "fragment_mapa";
    private static final String TAG_FRAGMENT_FAV= "fragment_favoritos";

    public static final String PREFERENCES ="Preferencias" ;
    public static final String PREFERENCES_WIFI_ONLY ="pref_only_wifi" ;
    ;


    private FrameLayout fragmentPlace;
    private FrameLayout fragmentPlace2;

    private Lista fragment_lista;
    private Detalle fragment_detalles;
    private Favoritas fragment_favoritas;
    private Mapa fragment_mapa;
    private Informacion fragment_informacion;


    private SearchView searchView;
    private String busqueda;

    private String actual;
    private Fragment fragment_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Establecer la ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Clase que se encarga de aceptar cualquier certficado SSL. Necesario para descargar deste web HTTPS en API 10
        NukeSSLCerts.nuke();

        //Creacion de fragmentos
        fragment_lista = Lista.newInstance();
        fragment_lista.setOnRefreshListener(this);

        fragment_detalles = Detalle.newInstance();
        fragment_mapa = Mapa.newInstance();
        fragment_favoritas = Favoritas.newInstance();
        fragment_informacion = Informacion.newInstance();

        //Obtencion de datos del bundle en caso de que existan
        if (savedInstanceState!=null && savedInstanceState.getString(BUSQUEDA)!=null){
            busqueda = savedInstanceState.getString(BUSQUEDA);
            actual = savedInstanceState.getString(FRAGMENTO_ACTUAL);
        }


        //Obtener La vista que se remplazará por el fragmento
        fragmentPlace = (FrameLayout) findViewById(R.id.fragmentPlace);


        //Comprobar si esta en modo tablet (existe fragmentplace2)
        if (findViewById(R.id.fragmentPlace2)!=null) {

            //Obtener La vista que se remplazará por el fragmento
            fragmentPlace2 = (FrameLayout) findViewById(R.id.fragmentPlace2);

            //Colocar por defecto la vista de detalles en el segundo fragmento
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace2, fragment_detalles).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

            //Mirar cual es el fragmento actual (si hay alguno)
            if (getSupportFragmentManager().findFragmentById(R.id.fragmentPlace)!=null){

                Fragment temp = getSupportFragmentManager().findFragmentById(R.id.fragmentPlace);

                //Si es la clase detalles, colocarlo a la derecha (Si se pasa de una vista de un fragmento a una de dos, hay que colocar la vista de detalles a la derecha)
                if (temp.getClass()==Detalle.class){
                    Detalle d = Detalle.newInstance(((Detalle)temp).getPlaya().getId());
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace2, d).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

                    //La vista de la izquierda corresponde a la seccion actual (Lista, Mapa o Favoritos)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace, getActualFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                    actual = TAG_FRAGMENT_LISTA;
                }

            }

        }


        //Obtener y configurar el Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.informacion,R.string.otros );
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Se es la primera ejecucion, se coloca el fragmento de la lista de playas
        if (actual==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace, fragment_lista,TAG_FRAGMENT_LISTA).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            actual = TAG_FRAGMENT_LISTA;
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        //Crear el searchView del Toolbar
        final MenuItem item = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        //Rellenar el searchview si hay una busqueda activa (Cambios de orientacion)
        if (busqueda!=null && busqueda.compareTo("")!=0){
            searchView.setQuery(busqueda, false);
            searchView.setIconified(false);
            searchView.clearFocus();
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        //Si se presiona la tecla "atras" del movil y esta abierto el drawer, cerrarlo
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Manejar los clicks en los elementos del menu lateral (Drawer).
        //Los campos del Drawer se definen en /res/menu/activity_main_drawer.xml

        int id = item.getItemId();

        getSupportFragmentManager().popBackStack();

        //En funcion del boton, abrir el fragmento (Lista, Favoritos, Mapa, Informacion) o actividad correspondiente (Configuracion)
        if (id == R.id.nav_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace, fragment_lista).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            actual = TAG_FRAGMENT_LISTA;


        } else if (id == R.id.nav_map) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace, fragment_mapa).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            actual = TAG_FRAGMENT_MAPA;


        } else if (id == R.id.nav_fav) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace, fragment_favoritas).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            actual = TAG_FRAGMENT_FAV;


        } else if (id == R.id.nav_settings) {
            Intent i = new Intent();
            i.setClass(this,Preferencias.class);
            startActivity(i);

        } else if (id == R.id.nav_info) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace, fragment_informacion).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

        }else if (id == R.id.nav_comment) {

            //En este caso, se lanza un intent para la aplicación de correo electronico

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","uo232368@uniovi.es", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_text));
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.email_send)));
        }

        //Se cierra el drawer tras el cambio de vista.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onListaRefresh() {

        //Manejar los eventos del fragmento Lista. Esta clase implementa una interfaz de la clase lista que se encarga de manejar eventos del SwipeRefreshLayout.
        //Este metodo es llamado por el fragmento Lista cuando se desliza hacia abajo la lista (Refresh) y se encarga de actualizar las playas de la BD

        //Obtener las preferencias de usuario y comprobar que esta permitida la descarga
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo gsm = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //Obtener informacion acerca de conexiones WIFI y MOVIL
        boolean can_dowload = !prefs.getBoolean(MainActivity.PREFERENCES_WIFI_ONLY, false);
        boolean hayWifi = wifi.isConnected();
        boolean hayGsm = false;

        if (gsm!=null){
            hayGsm=gsm.isConnected();
        }

        //Descargar en los casos que esté permitido, en caso contrario informar al usuario.

        if (!can_dowload && !hayWifi){
            Snackbar.make(fragmentPlace,R.string.no_descargar_wifi,Snackbar.LENGTH_LONG).show();

            return;
        }else if(hayWifi || hayGsm){

            Snackbar.make(fragmentPlace,R.string.descargando,Snackbar.LENGTH_LONG).show();
            cleanBusqueda();

        }else{
            Snackbar.make(fragmentPlace,R.string.no_descargar_red,Snackbar.LENGTH_LONG).show();
        }


    }

    private void cleanBusqueda() {

        //Limpia el elemento de busqueda del Toolbar
        if (searchView!=null){
            searchView.setQuery("",false);
            searchView.setIconified(true);
            searchView.clearFocus();
        }

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        //Cuando se escribe una letra (o se elimina), se hace una busqueda en la base de datos
        PlayaDB db = PlayaDB.getInstance(getApplicationContext());
        db.busacaPlayas(newText);

        // Se llama al metodo del fragmento correspondiente que se encarga de la actualizacion de la lista o mapa.
        if (fragment_lista.isVisible()){
            fragment_lista.actualizaPlayas();
        }

        if (fragment_mapa.isVisible()){
            fragment_mapa.actualizaPlayas();
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Almacenar datos en el bundle ante cambios de configuracion u otros.
        outState.putString(BUSQUEDA, searchView.getQuery().toString());
        outState.putString(FRAGMENTO_ACTUAL, actual);

    }

    public Fragment getActualFragment(){

        //Retorna el fragmento actual de los 3 principales
        switch(actual){
            case TAG_FRAGMENT_LISTA:
                return fragment_lista;
            case TAG_FRAGMENT_FAV:
                return fragment_favoritas;
            case TAG_FRAGMENT_MAPA:
                return fragment_mapa;
            default:
                return fragment_lista;
        }

    }

}
