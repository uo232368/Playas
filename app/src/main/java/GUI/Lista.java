package GUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import Negocio.Playa;
import Negocio.RecyclerViewAdapter;
import Presistencia.PlayaDB;
import Negocio.SimpleLoader;
import es.ppn.playas_asturias.MainActivity;
import es.ppn.playas_asturias.R;


public class Lista extends Fragment implements SwipeRefreshLayout.OnRefreshListener,  LoaderManager.LoaderCallbacks<Void> {

    private static final String ARG_PLAYAS = "playas";
    private static final String TAG = "PlayaApp" ;

    private RecyclerViewAdapter recAdapter;

    private ArrayList<Playa> playas;

    private RecyclerView recyclerItems;
    private SwipeRefreshLayout srl;
    private ListaRefresh refreshlistener;
    private RelativeLayout noPlaya;

    public Lista() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();

        //Se encarga de mostrar una progressbar circular indefinida en la lista de las playas si no hay ninguna.
        //Se hace en un Runnable poruque es la unica forma de que funcione el metodo setRefreshing(true) del SwipeRefreshLayout
        srl.post(new Runnable() {
            @Override
            public void run() {
                PlayaDB db = PlayaDB.getInstance(getContext());

                //Si no hay playas,mostrar la animacion del SwipeRefreshLayout
                if(db.getListaPlayasActual().size()==0){
                    srl.setRefreshing(true);
                }
            }
        });

    }

    public static Lista newInstance() {
        Lista fragment = new Lista();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlayaDB db = PlayaDB.getInstance(getContext());

        //Obtener la lista de playas actual (resultados de busqueda o completa)
        playas=db.getListaPlayasActual();

        //Si no existe, obtenerlas todas
        if (playas==null){
            playas =  db.getPlayas();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Colocar animacion
        if (Build.VERSION.SDK_INT >= 19) {
            setExitTransition(new Fade());
        }

        View vista = inflater.inflate(R.layout.fragment_lista, container, false);

        //Mostrar buscador en la toolbar
        if (getActivity().findViewById(R.id.fragmentPlace2)==null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
            MenuItem item = toolbar.getMenu().findItem(R.id.search);
            if (item!=null){ item.setVisible(true);}
        }

        //Placeholder para el caso de que no existan playas
        noPlaya = (RelativeLayout)vista.findViewById(R.id.noPlayas);

        //Obtener el SwipeRefreshLayout, y registrar esta clase ante los eventos de refresco.
        srl = (SwipeRefreshLayout) vista.findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(this);

        //Se hace visible el PlaceHolder en el caso de que no existan playas
        if (playas.size()!=0){
            noPlaya.setVisibility(View.INVISIBLE);
            srl.setRefreshing(false);

        }

        //RecyclerView para la lista
        recyclerItems = (RecyclerView) vista.findViewById(R.id.recycler);
        recyclerItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recAdapter = new RecyclerViewAdapter(playas,this);
        recyclerItems.setAdapter(recAdapter);

        //Listener al hacer click en un item
        recAdapter.setOnItemClickListener(new RecyclerViewAdapter.onPlayaClick() {
            @Override
            public void onItemClickListener(Playa p, View v) {
                Detalle fragment = Detalle.newInstance(p.getId());

                int id = R.id.fragmentPlace;

                View vg = (View) container.getParent().getParent();


                //Si está en landscape se remplaza
                if (vg.findViewById(R.id.fragmentPlace2) != null) {
                    id = R.id.fragmentPlace2;
                    FragmentTransaction ft = getFragmentManager().beginTransaction()
                            .replace(id, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();

                }
                //Si no está en landscape se añade
                else{

                    FragmentTransaction ft = getFragmentManager().beginTransaction()
                            .replace(id, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();

                }




            }
        });

        //Si es la primera vez que se ejecuta, Se descargan las playas
        if (playas.size()==0){
            onRefresh();
        }



        return vista;
    }



    @Override
    public void onRefresh() {
        //Limpia la lista de playas y envia el evento a la clase principal

        if (refreshlistener!=null){
            refreshlistener.onListaRefresh();
        }

        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);

        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo gsm = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean can_dowload = !prefs.getBoolean(MainActivity.PREFERENCES_WIFI_ONLY, false);
        boolean hayWifi = wifi.isConnected();
        boolean hayGsm = false;

        if (gsm!=null){
            hayGsm=gsm.isConnected();
        }

        if (!can_dowload && !hayWifi){
            srl.setRefreshing(false);
        }else if(hayWifi || hayGsm){
            recAdapter.clear();

            //Inicio del loader de descarga
            getActivity().getSupportLoaderManager().initLoader(1, null, this);


        }else{
            srl.setRefreshing(false);
        }

    }

    public void setOnRefreshListener(final ListaRefresh listener){
        refreshlistener = listener;
    }


    public void actualizaPlayas() {
        PlayaDB db = PlayaDB.getInstance(getContext());
        recAdapter.setPlayas(db.getListaPlayasActual());
    }



    //Loader

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        //Crear loader encargado de descargar las playas
        SimpleLoader loader = new SimpleLoader(getContext());
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        //Cuando se finaliza la descarga
        Log.d(TAG, "Load Finish.");

        PlayaDB db = PlayaDB.getInstance(getContext());
        playas =  db.getPlayas();
        recAdapter.setPlayas(playas);
        srl.setRefreshing(false);
        if (playas.size()!=0){
            noPlaya.setVisibility(View.INVISIBLE);
        }

        getActivity().getSupportLoaderManager().destroyLoader(1);

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }



    //Clase para enviar enentos
    public interface ListaRefresh {
        public void onListaRefresh();
    }


}
