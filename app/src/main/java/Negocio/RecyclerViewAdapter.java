package Negocio;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import es.ppn.playas_asturias.R;

/**
 * Created by pp_1_ on 17/02/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>   {

    public static ArrayList<Playa> playas;
    private final RequestQueue queue;
    public  Context context;
    public static Fragment _fragment;
    public onPlayaClick clicklistener;

    public RecyclerViewAdapter(List<Playa> playas, Fragment fragment) {
        this.playas = (ArrayList<Playa>)playas;
        _fragment =fragment;
        queue = Volley.newRequestQueue(fragment.getContext());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context =parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return new RecyclerViewAdapter.ViewHolder(view);
    }

    public ArrayList<Playa> getPlayas(){
        return playas;
    }

    public void setPlayas(ArrayList<Playa> c){
        playas=c;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Obtener el curso Actual
        // Colocar el nombre del curso y el profesor en el viewHolder

        final Playa p = playas.get(position);
        holder.nombre.setText(p.getNombre());
        holder.concejo.setText(p.getConcejo());
        if (p.getImagenBitmap(context)!=null){
            holder.fondo.setImageBitmap(p.getImagenBitmap(context));
        }

       /* if (p.isqCalidad()){ holder.qCalidad.setVisibility(View.VISIBLE); }
        else{holder.qCalidad.setVisibility(View.GONE);}

        if (p.isBanderaAzul()){holder.banderaAzul.setVisibility(View.VISIBLE);}
        else{holder.banderaAzul.setVisibility(View.GONE);}


        ocultaServicios(holder);
        pintaServicios(holder, p.servicios);*/

    }

   /* private void ocultaServicios(ViewHolder v) {

        v.hosteleria.setVisibility(View.GONE);
        v.ducha.setVisibility(View.GONE);
        v.servicios.setVisibility(View.GONE);
        v.parking.setVisibility(View.GONE);
        v.surf.setVisibility(View.GONE);
        v.accesible.setVisibility(View.GONE);
        v.salvamento.setVisibility(View.GONE);
        v.pesca.setVisibility(View.GONE);
        
    }


    private void pintaServicios(ViewHolder v,List<String> servicios) {
        for (String s : servicios){
            ImageView im;
            switch (s){
                case "Servicio de Hostelería":
                    v.hosteleria.setVisibility(View.VISIBLE);
                    break;
                case "Duchas":
                    v.ducha.setVisibility(View.VISIBLE);
                    break;
                case "Aseos":
                    v.servicios.setVisibility(View.VISIBLE);
                    break;
                case "Parking":
                    v.parking.setVisibility(View.VISIBLE);
                    break;
                case "Surf":
                    v.surf.setVisibility(View.VISIBLE);
                    break;
                case "Accesible":
                    v.accesible.setVisibility(View.VISIBLE);
                    break;
                case "Socorristas":
                    v.salvamento.setVisibility(View.VISIBLE);
                    break;

                case "Pesca (submarina o no)":
                    v.pesca.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }*/


    @Override
    public int getItemCount() {
        return playas.size();
    }

    public void setOnItemClickListener(final onPlayaClick listener){
        clicklistener = listener;
    }

    public void addPlaya(Playa c){
        playas.add(c);
        notifyDataSetChanged();
    }

    public void clear() {
        playas.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nombre ;
        public TextView concejo ;
        public ImageView fondo,qCalidad, banderaAzul, accesible, ducha, hosteleria,parking, pesca, salvamento, servicios, surf;


        public ViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView)itemView.findViewById(R.id.txtNombre);
            concejo = (TextView)itemView.findViewById(R.id.txtConcejo);
            fondo = (ImageView) itemView.findViewById(R.id.imgFondo);
           /* banderaAzul  = (ImageView) itemView.findViewById(R.id.banderaAzul);
            qCalidad = (ImageView) itemView.findViewById(R.id.qCalidad);
            accesible = (ImageView) itemView.findViewById(R.id.accesible);
            ducha = (ImageView) itemView.findViewById(R.id.ducha);
            hosteleria = (ImageView) itemView.findViewById(R.id.hosteleria);
            parking= (ImageView) itemView.findViewById(R.id.parking);
            pesca= (ImageView) itemView.findViewById(R.id.pesca);
            salvamento= (ImageView) itemView.findViewById(R.id.salvamento);
            servicios= (ImageView) itemView.findViewById(R.id.servicios);
            surf= (ImageView) itemView.findViewById(R.id.surf);*/

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            Playa p = playas.get(getAdapterPosition());

            if (clicklistener!=null){
                clicklistener.onItemClickListener(p,v);
            }


        }

    }

    public interface onPlayaClick {
        public void onItemClickListener(Playa p, View v);
    }



}
