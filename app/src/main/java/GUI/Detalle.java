package GUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import Negocio.Playa;
import Presistencia.PlayaDB;
import es.ppn.playas_asturias.Imagen;
import es.ppn.playas_asturias.R;


public class Detalle extends Fragment {

    private static final String ARG_PLAYA = "playa";
    private static final String TAG = "PlayasApp" ;


    private Playa playa;
    private PlayaDB pdb;
    private FloatingActionButton fab;



    public Detalle() {
        // Required empty public constructor
    }

    public static Detalle newInstance() {
        Detalle fragment = new Detalle();
        return fragment;
    }


    public static Detalle newInstance(int id) {
        //Se pasa el ID de la playa al crear
        Detalle fragment = new Detalle();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYA, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener la playa de la BD
        pdb =PlayaDB.getInstance(getContext());
        if (getArguments() != null) {

            playa = pdb.getPlaya(getArguments().getInt(ARG_PLAYA));

        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Colocar animacion
        if (Build.VERSION.SDK_INT >= 19) {
            setExitTransition(new Fade());
        }

        View vista;

        //Si la playa no esta disponible se pone un Placeholder
        if (playa != null) {

            vista = inflater.inflate(R.layout.fragment_detalle, container, false);

            creaVistaDetalles(vista);

        }
        else{
            vista = inflater.inflate(R.layout.fragment_detalle_vacio, container, false);
        }


        //Ocultar buscador de la toolbar
        if (getActivity().findViewById(R.id.fragmentPlace2)==null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
            MenuItem item = toolbar.getMenu().findItem(R.id.search);
            if (item!=null) {item.setVisible(false);}
        }

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public Playa getPlaya() {
        return playa;
    }

    public void setPlaya(Playa playa) {
        this.playa = playa;
    }

    private void creaVistaDetalles(View vista) {


        //Obtener elementos de la vista
        fab = (FloatingActionButton) vista.findViewById(R.id.fab);
        ImageView fondo = (ImageView) vista.findViewById(R.id.imgFondo);
        TextView txtNombre = (TextView) vista.findViewById(R.id.txtNombre);
        TextView txtConcejo = (TextView) vista.findViewById(R.id.txtConcejo);
        TextView txtDescripcion = (TextView) vista.findViewById(R.id.txtDescripcion);
        TextView txtZona = (TextView) vista.findViewById(R.id.txtZona);
        TextView txtAccesos = (TextView) vista.findViewById(R.id.txtAccesos);
        TextView txtTipo = (TextView) vista.findViewById(R.id.txtTipo);
        TextView txtLongitud = (TextView) vista.findViewById(R.id.txtLongitud);
        TextView txtObservaciones = (TextView) vista.findViewById(R.id.txtObservaciones);
        TextView txtCoordenadas = (TextView) vista.findViewById(R.id.txtCoordenadas);

        ImageView banderaAzul = (ImageView) vista.findViewById(R.id.imgBanderaAzul);
        ImageView qCalidad = (ImageView) vista.findViewById(R.id.imgQcalidad);

        if (playa.getImagenBitmap(getContext())!=null){
            fondo.setImageBitmap(playa.getImagenBitmap(getContext()));
        }

        //Poner el titulo en la Toolbar
        CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout)vista.findViewById(R.id.collapsing_toolbar);
        toolbar.setTitle(playa.getNombre());

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();


        //txtNombre.setText(playa.getNombre());
        //txtConcejo.setText(playa.getConcejo());
        txtDescripcion.setText(playa.getDescripcion());
        txtZona.setText(playa.getZona());
        txtAccesos.setText(playa.getAccesos());
        txtTipo.setText(playa.getTipo());
        txtLongitud.setText(playa.getLongitud());
        txtObservaciones.setText(playa.getObservaciones());
        txtCoordenadas.setText(playa.getCoordenadas());


        //Colocar los servicios
        if (playa.isBanderaAzul()){
            banderaAzul.setVisibility(View.VISIBLE);
        }
        if(playa.isqCalidad()){
            qCalidad.setVisibility(View.VISIBLE);
        }
        pintaServicios(vista,playa.getServicios());
        
        

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdb.isFavorita(playa.getId())) {
                    quitFavorita();
                } else {
                    setFavorita();
                }
            }
        });

        fondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getContext(),Imagen.class);
                i.putExtra(ARG_PLAYA,playa.getId());
                startActivity(i);


            }
        });

        if(pdb.isFavorita(playa.getId())){
            fab.setImageResource(R.mipmap.ic_star_white_48dp);
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Ocultar buscador
        if (getActivity().findViewById(R.id.fragmentPlace2)==null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
            MenuItem item = toolbar.getMenu().findItem(R.id.search);
            if (item!=null) {item.setVisible(true);}
        }
    }

    private void pintaServicios(View v,List<String> servicios) {

        //Pintar los servicios
        for (String s : servicios){
            ImageView im;
            switch (s){
                case "Servicio de Hostelería":
                    im = (ImageView) v.findViewById(R.id.imgHosteleria);
                    im.setVisibility(View.VISIBLE);
                    break;
                case "Duchas":
                    im = (ImageView) v.findViewById(R.id.imgDucha);
                    im.setVisibility(View.VISIBLE);
                    break;
                case "Aseos":
                    im = (ImageView) v.findViewById(R.id.imgServicios);
                    im.setVisibility(View.VISIBLE);
                    break;
                case "Parking":
                    im = (ImageView) v.findViewById(R.id.imgParking);
                    im.setVisibility(View.VISIBLE);
                    break;
                case "Surf":
                    im = (ImageView) v.findViewById(R.id.imgSurf);
                    im.setVisibility(View.VISIBLE);
                    break;
                case "Accesible":
                    im = (ImageView) v.findViewById(R.id.imgAccesible);
                    im.setVisibility(View.VISIBLE);
                    break;
                case "Socorristas":
                    im = (ImageView) v.findViewById(R.id.imgSalvamento);
                    im.setVisibility(View.VISIBLE);
                    break;

                case "Pesca (submarina o no)":
                    im = (ImageView) v.findViewById(R.id.imgPesca);
                    im.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void setFavorita() {
        //Cambiar el icono y poner la playa como favorita
        Toast.makeText(getContext(), R.string.playa_favorita, Toast.LENGTH_LONG).show();
        pdb.setFavorita(playa.getId(), 1);
        fab.setImageResource(R.mipmap.ic_star_white_48dp);


    }

    private void quitFavorita() {
        //Cambiar el icono y quitar la playa como favorita
        Toast.makeText(getContext(), R.string.playa_no_favorita, Toast.LENGTH_LONG).show();
        pdb.setFavorita(playa.getId(), 0);
        fab.setImageResource(R.mipmap.ic_star_outline_white_48dp);


    }


}
