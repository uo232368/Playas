package GUI;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import es.ppn.playas_asturias.R;


public class Informacion extends Fragment {




    public Informacion() {
        // Required empty public constructor
    }

    public static Informacion newInstance() {
        Informacion fragment = new Informacion();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ocultar buscador
        if (getActivity().findViewById(R.id.fragmentPlace2)==null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.informacion);
            MenuItem item = toolbar.getMenu().findItem(R.id.search);
            if (item!=null){ item.setVisible(false);}
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Colocar animacion
        if (Build.VERSION.SDK_INT >= 19) {
            setExitTransition(new Fade());
        }

        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_informacion, container, false);


        return vista;
    }



}
