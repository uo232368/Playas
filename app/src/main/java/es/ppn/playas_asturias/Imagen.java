package es.ppn.playas_asturias;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import Negocio.Playa;
import Presistencia.PlayaDB;

public class Imagen extends AppCompatActivity {

    private static final String ARG_PLAYA = "playa";
    private static final String TAG = "PlayasApp" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);



        int id = getIntent().getIntExtra(ARG_PLAYA,-1);

        PlayaDB pdb = PlayaDB.getInstance(this);
        Playa playa = pdb.getPlaya(id);

        ImageView fondo = (ImageView) findViewById(R.id.imagen);
        fondo.setImageBitmap(playa.getImagenBitmap(this));

    }
}
