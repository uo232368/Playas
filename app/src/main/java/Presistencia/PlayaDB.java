package Presistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import Negocio.Playa;

/**
 * Created by pp_1_ on 11/04/2016.
 */
public class PlayaDB {

    private static PlayaDB playaDB;
    private final String NOMBRE_BD = "PLAYADB";
    private final String NOMBRE_TABLA = "Playa";
    private final int VERSION_BD = 1;
    private final String TAG = "PlayasApp";

    public ArrayList <Playa> playas;
    private Context contexto;


    public static PlayaDB getInstance (Context c){

        if (playaDB==null){
            playaDB = new PlayaDB(c);

        }

        return playaDB;


    }

    private PlayaDB(Context c){
        contexto = c;
        //playas = new ArrayList<Playa>();
    }

    public int creaPlaya(Playa p){

        DataBase dbh = new DataBase(contexto, NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getWritableDatabase();

        String servicios = toCSV(p.getServicios());


        ContentValues val = new ContentValues();
        val.put("nombre", p.getNombre());
        val.put("descripcion", p.getDescripcion());
        val.put("zona", p.getZona());
        val.put("concejo", p.getConcejo());
        val.put("accesos", p.getAccesos());
        val.put("tipo", p.getTipo());
        val.put("longitud", p.getLongitud());
        val.put("observaciones", p.getObservaciones());
        val.put("coordenadas", p.getCoordenadas());
        val.put("banderaAzul", p.isBanderaAzul());
        val.put("qCalidad", p.isqCalidad());
        val.put("servicios", servicios);
        val.put("imagen", p.getImagen());
        val.put("favorita", false);

        long ret = db.insert(NOMBRE_TABLA, null, val);

        db.close();

        return (int) ret;

    }

    public void creaPlayas(ArrayList<Playa> playas){

        DataBase dbh = new DataBase(contexto,NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getWritableDatabase();


        for (Playa p : playas){
            String servicios = toCSV(p.getServicios());


            String sentencia = "INSERT INTO Playa (nombre, descripcion, zona, concejo, accesos, tipo, longitud, observaciones, coordenadas,banderaAzul,qCalidad ,servicios, imagenes,favorita ) " +
                    "VALUES (\""+p.getNombre()+"\",\""+p.getDescripcion()+"\",\""+p.getZona()+"\",\""+p.getConcejo()+"\",\""+p.getAccesos()+"\",\""
                    +p.getTipo()+"\",\""+p.getLongitud()+"\",\""+p.getObservaciones()+"\",\""+p.getCoordenadas()+"\",\""+p.isBanderaAzul()+"\",\""+p.isqCalidad()+"\",\""+servicios+"\",\""+p.getImagen()+"\",\"false\")";



            db.execSQL(sentencia);

        }


        //Cerramos la base de datos
        db.close();

    }

    public ArrayList<Playa> getPlayas(){

        ArrayList<Playa> ret= new ArrayList<Playa>();

        DataBase dbh = new DataBase(contexto,NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * from Playa",null);
        while(c.moveToNext()){
            int id = c.getInt(0);
            String nombre = c.getString(1);
            String descripcion = c.getString(2);
            String zona = c.getString(3);
            String concejo = c.getString(4);
            String accesos = c.getString(5);
            String tipo = c.getString(6);
            String longitud = c.getString(7);
            String observaciones = c.getString(8);
            String coordenadas = c.getString(9);
            Boolean banderaAzul = (c.getInt(10) == 1);
            Boolean qCalidad = (c.getInt(11) == 1);
            List<String> servicios = getCSV(c.getString(12));
            String imagen = c.getString(13);

            ret.add(new Playa(id,nombre,descripcion,zona,concejo,accesos,tipo,longitud,observaciones,coordenadas,banderaAzul,qCalidad,servicios,imagen));

        }



        db.close();


        playas = ret;

        return ret;
    }

    public ArrayList<Playa> getPlayasFavoritas(){

        ArrayList<Playa> ret= new ArrayList<Playa>();

        DataBase dbh = new DataBase(contexto,NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * from Playa where favorita=1",null);
        while(c.moveToNext()){
            int id = c.getInt(0);
            String nombre = c.getString(1);
            String descripcion = c.getString(2);
            String zona = c.getString(3);
            String concejo = c.getString(4);
            String accesos = c.getString(5);
            String tipo = c.getString(6);
            String longitud = c.getString(7);
            String observaciones = c.getString(8);
            String coordenadas = c.getString(9);
            Boolean banderaAzul = (c.getInt(10) == 1);
            Boolean qCalidad = (c.getInt(11) == 1);
            List<String> servicios = getCSV(c.getString(12));
            String imagen = c.getString(13);

            ret.add(new Playa(id,nombre,descripcion,zona,concejo,accesos,tipo,longitud,observaciones,coordenadas,banderaAzul,qCalidad,servicios,imagen));

        }



        db.close();


        return ret;
    }


    public void setFavorita(int id,int val) {
        DataBase dbh = new DataBase(contexto,NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getWritableDatabase();


        db.execSQL("UPDATE Playa SET favorita="+val+" WHERE id="+id);

        db.close();

    }

    public boolean isFavorita(int id){

        DataBase dbh = new DataBase(contexto,NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT favorita from Playa where id=" + id, null);

        c.moveToFirst();
        boolean ret = (c.getInt(0)==1);

        db.close();


        return ret;
    }

    private String toCSV(List<String> lista) {
        String ret = "";

        for(String s : lista){
            ret +=s+";";
        }

        ret = ret.substring(0,ret.length()-1);
        return ret;
    }

    private List<String> getCSV(String s) {
        List<String> ret = new ArrayList<String>();

        String[] elementos = s.split(";");

        for ( int i =0; i<elementos.length ;i++){
            ret.add(elementos[i]);
        }

        return ret;
    }


    public  ArrayList<Playa> getListaPlayasActual(){
        return playas;
    }

    public Playa getPlaya(int _id) {
        Playa ret;

        DataBase dbh = new DataBase(contexto,NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * from Playa where id="+_id,null);
        c.moveToFirst();

        int id = c.getInt(0);
        String nombre = c.getString(1);
        String descripcion = c.getString(2);
        String zona = c.getString(3);
        String concejo = c.getString(4);
        String accesos = c.getString(5);
        String tipo = c.getString(6);
        String longitud = c.getString(7);
        String observaciones = c.getString(8);
        String coordenadas = c.getString(9);
        Boolean banderaAzul = (c.getInt(10) == 1);
        Boolean qCalidad = (c.getInt(11) == 1);
        List<String> servicios = getCSV(c.getString(12));
        String imagen = c.getString(13);

        ret = new Playa(id,nombre,descripcion,zona,concejo,accesos,tipo,longitud,observaciones,coordenadas,banderaAzul,qCalidad,servicios,imagen);


        db.close();


        return ret;
    }

    public ArrayList<Playa> busacaPlayas(String newText) {
        ArrayList<Playa> ret= new ArrayList<Playa>();

        DataBase dbh = new DataBase(contexto,NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * FROM "+NOMBRE_TABLA+" WHERE nombre LIKE '%"+newText+"%' OR concejo LIKE '%"+newText+"%'",null);
        while(c.moveToNext()){
            int id = c.getInt(0);
            String nombre = c.getString(1);
            String descripcion = c.getString(2);
            String zona = c.getString(3);
            String concejo = c.getString(4);
            String accesos = c.getString(5);
            String tipo = c.getString(6);
            String longitud = c.getString(7);
            String observaciones = c.getString(8);
            String coordenadas = c.getString(9);
            Boolean banderaAzul = (c.getInt(10) == 1);
            Boolean qCalidad = (c.getInt(11) == 1);
            List<String> servicios = getCSV(c.getString(12));
            String imagen = c.getString(13);

            ret.add(new Playa(id,nombre,descripcion,zona,concejo,accesos,tipo,longitud,observaciones,coordenadas,banderaAzul,qCalidad,servicios,imagen));

        }



        db.close();


        playas=ret;
        return ret;
    }
}
