package GUI;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import Negocio.Playa;
import Negocio.RecyclerViewAdapter;
import Presistencia.PlayaDB;
import es.ppn.playas_asturias.R;


public class Favoritas extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "PlayasApp" ;

    private RecyclerViewAdapter recAdapter;

    private ArrayList<Playa> playas;

    private RecyclerView recyclerItems;
    private SwipeRefreshLayout srl;
    private RelativeLayout nofavoritas;

    public Favoritas() {
        // Required empty public constructor
    }

    public static Favoritas newInstance() {
        Favoritas fragment = new Favoritas();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlayaDB db =PlayaDB.getInstance(getContext());
        playas =  db.getPlayasFavoritas();

    }

    @Override
    public void onResume() {

        onRefresh();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        //Colocar animacion
        if (Build.VERSION.SDK_INT >= 19) {
            setExitTransition(new Fade());
        }

        // Inflar el layout
        View vista  = inflater.inflate(R.layout.fragment_favoritos, container, false);

        //Ocultar buscador
        if (getActivity().findViewById(R.id.fragmentPlace2)==null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            MenuItem item = toolbar.getMenu().findItem(R.id.search);
            toolbar.setVisibility(View.VISIBLE);
            if (item!=null){ item.setVisible(false);}
        }

        nofavoritas = (RelativeLayout)vista.findViewById(R.id.noFavoritas);

        if (playas.size()!=0){
            nofavoritas.setVisibility(View.INVISIBLE);
        }

        srl = (SwipeRefreshLayout) vista.findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(this);

        recyclerItems = (RecyclerView) vista.findViewById(R.id.recycler);
        recyclerItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recAdapter = new RecyclerViewAdapter(playas,this);
        recyclerItems.setAdapter(recAdapter);

        //Al hacer click en un item
        recAdapter.setOnItemClickListener(new RecyclerViewAdapter.onPlayaClick() {
            @Override
            public void onItemClickListener(Playa p, View v) {
                Detalle fragment = Detalle.newInstance(p.getId());

                int id = R.id.fragmentPlace;

                View vg = (View) container.getParent().getParent();

                if (vg.findViewById(R.id.fragmentPlace2) != null) {
                    id = R.id.fragmentPlace2;
                }


                // Add Fragment B
                FragmentTransaction ft = getFragmentManager().beginTransaction()
                        .replace(id, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null);
                ft.commit();

            }
        });

        return vista;
    }




    @Override
    public void onRefresh() {
        //Actualizar la lista

        recAdapter.clear();

        PlayaDB db = PlayaDB.getInstance(getContext());
        playas =  db.getPlayasFavoritas();

        recAdapter.setPlayas(playas);
        srl.setRefreshing(false);

        if (playas.size()!=0){
            nofavoritas.setVisibility(View.INVISIBLE);
        }
        else{
            nofavoritas.setVisibility(View.VISIBLE);
        }




    }

}
