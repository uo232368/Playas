package GUI;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Negocio.Playa;
import Presistencia.PlayaDB;
import es.ppn.playas_asturias.R;


public class Mapa extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final String TAG = "PlayaApp";
    private static final String BUNDLE_LATITUDE ="latidude" ;
    private static final String BUNDLE_LONGITUDE ="longitude" ;


    private GoogleMap mMap;
    private FragmentManager fragmentMananger;
    private ArrayList<Playa> playas;
    private Map<String, Integer> markersMap;
    private Location miPosicion;

    public Mapa() {
        // Required empty public constructor
    }

    public static Mapa newInstance() {
        Mapa fragment = new Mapa();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener los datos almacenados
        if (savedInstanceState!=null){
            double lat = savedInstanceState.getDouble(BUNDLE_LATITUDE);
            double longi= savedInstanceState.getDouble(BUNDLE_LONGITUDE);

            miPosicion=new Location(LocationManager.NETWORK_PROVIDER);
            miPosicion.setLatitude(lat);
            miPosicion.setLongitude(longi);
        }

        fragmentMananger = getChildFragmentManager();
        markersMap = new HashMap<String, Integer>();
        actualizaPlayas();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        //Guardar datos en un bundle
        if (miPosicion!=null){
            outState.putDouble(BUNDLE_LATITUDE,miPosicion.getLatitude());
            outState.putDouble(BUNDLE_LONGITUDE,miPosicion.getLongitude());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizaPlayas();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        //Colocar animacion
        if (Build.VERSION.SDK_INT >= 19) {
            setExitTransition(new Fade());
        }


        View vista = inflater.inflate(R.layout.fragment_mapa, container, false);

        RelativeLayout main = (RelativeLayout) vista.findViewById(R.id.mainLayout);


        SupportMapFragment mapFragment = (SupportMapFragment) fragmentMananger.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Mostrar buscador
        if (getActivity().findViewById(R.id.fragmentPlace2) == null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            MenuItem item = toolbar.getMenu().findItem(R.id.search);
            toolbar.setVisibility(View.VISIBLE);
            if (item != null) {
                item.setVisible(true);
            }
        }

        return vista;
    }


    private void miPosicion(Location l) {
        miPosicion=l;
        marcadores();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        if ( ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{

            //Habilitar el boton de posicion
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if ( lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                mMap.setMyLocationEnabled(true);
            }


            //Marcar la ubicacion
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    miPosicion(location);
                }
            });

            //Al hacer click en un marcador, abrir los detalles de la playa
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    if (getActivity().findViewById(R.id.fragmentPlace2) == null) {

                        Detalle fragment = Detalle.newInstance(markersMap.get(marker.getTitle()));
                        int id = R.id.fragmentPlace;

                        FragmentTransaction ft = getFragmentManager().beginTransaction()
                                .add(id, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null);
                        ft.commit();
                    }

                }
            });


        }



        marcadores();


    }

    private void marcadores() {
        if (mMap == null) {
            return;
        }
        mMap.clear();
        LatLng marcador = new LatLng(43.36159, -5.87096);

        //Pintar marcadores en rojo. Si están cerca de la ubicacion, pintar en azul
        for (Playa p : playas) {
            String[] coor = p.getCoordenadas().replaceAll("°", "").replaceAll("\\+", "").trim().split(",");
            marcador = new LatLng(Double.parseDouble(coor[0]), Double.parseDouble(coor[1]));
            markersMap.put(p.getNombre(), p.getId());

            if (miPosicion!=null){

                if (cercano(marcador,miPosicion)){
                    mMap.addMarker(new MarkerOptions().position(marcador).title(p.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }else{
                    mMap.addMarker(new MarkerOptions().position(marcador).title(p.getNombre()));
                }

            }else{
                mMap.addMarker(new MarkerOptions().position(marcador).title(p.getNombre()));
            }

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(marcador));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(8));

    }

    private boolean cercano(LatLng marcador, Location miPosicion) {

        //Ver si esta cerca de la ubicacion
        double d = Math.sqrt(Math.pow(marcador.latitude - miPosicion.getLatitude(), 2) + Math.pow(marcador.longitude - miPosicion.getLongitude(), 2));

        if(d<0.2){
            return true;
        }
        
        return false;
    }

    public void actualizaPlayas() {
        PlayaDB db = PlayaDB.getInstance(getContext());
        playas = db.getListaPlayasActual();
        marcadores();


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (getActivity().findViewById(R.id.fragmentPlace2) != null) {

            Detalle fragment = Detalle.newInstance(markersMap.get(marker.getTitle()));
            int id = R.id.fragmentPlace2;

            FragmentTransaction ft = getFragmentManager().beginTransaction()
                    .replace(id, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null);
            ft.commit();
        }


        return false;
    }



}
