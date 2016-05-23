package Presistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pp_1_ on 11/04/2016.
 */
public class DataBase extends SQLiteOpenHelper {

    String sqlCreate = "CREATE TABLE Playa (id INTEGER PRIMARY KEY, nombre TEXT UNIQUE, descripcion TEXT,zona TEXT, concejo TEXT, accesos TEXT, tipo TEXT, longitud TEXT, observaciones TEXT, coordenadas TEXT,banderaAzul BOOLEAN,qCalidad BOOLEAN,servicios TEXT, imagen text,favorita BOOLEAN)";
    String sqlDrop = "DROP TABLE IF EXISTS Playa ";


    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla
        db.execSQL(sqlDrop);

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
    }
}
