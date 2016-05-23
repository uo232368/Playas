package Negocio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pp_1_ on 08/04/2016.
 */
public class Playa {
    public int id;
    public String nombre, descripcion, zona, concejo, accesos, tipo, longitud, observaciones, coordenadas,imagen;
    public boolean banderaAzul, qCalidad;
    public List<String> servicios;
    public Bitmap imagenBitmap;


    public Playa(String nombre, String descripcion, String zona, String concejo, String accesos, String tipo, String longitud, String observaciones, String coordenadas, boolean banderaAzul, boolean qCalidad, List<String> servicios, String imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.zona = zona;
        this.concejo = concejo;
        this.accesos = accesos;
        this.tipo = tipo;
        this.longitud = longitud;
        this.observaciones = observaciones;
        this.coordenadas = coordenadas;
        this.banderaAzul = banderaAzul;
        this.qCalidad = qCalidad;
        this.servicios = servicios;
        this.imagen = imagen;
    }

    public Playa(int id,String nombre, String descripcion, String zona, String concejo, String accesos, String tipo, String longitud, String observaciones, String coordenadas, boolean banderaAzul, boolean qCalidad, List<String> servicios, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.zona = zona;
        this.concejo = concejo;
        this.accesos = accesos;
        this.tipo = tipo;
        this.longitud = longitud;
        this.observaciones = observaciones;
        this.coordenadas = coordenadas;
        this.banderaAzul = banderaAzul;
        this.qCalidad = qCalidad;
        this.servicios = servicios;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getConcejo() {
        return concejo;
    }

    public void setConcejo(String concejo) {
        this.concejo = concejo;
    }

    public String getAccesos() {
        return accesos;
    }

    public void setAccesos(String accesos) {
        this.accesos = accesos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public boolean isBanderaAzul() {
        return banderaAzul;
    }

    public void setBanderaAzul(boolean banderaAzul) {
        this.banderaAzul = banderaAzul;
    }

    public boolean isqCalidad() {
        return qCalidad;
    }

    public void setqCalidad(boolean qCalidad) {
        this.qCalidad = qCalidad;
    }

    public List<String> getServicios() {
        return servicios;
    }

    public void setServicios(List<String> servicios) {
        this.servicios = servicios;
    }

    public String getImagen(){
        return imagen;
    }

    public String getImagen(Context context) {
        if (imagen!=null){
            return imagen;
        }
        String path = context.getFilesDir().toString();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        imagen = path+"/Playa_"+getId()+".jpg";
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Bitmap getImagenBitmap(Context context) {

        if (imagenBitmap!=null){
            return imagenBitmap;
        }
        String path = context.getFilesDir().toString();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 2;

        try{
            Bitmap ret = BitmapFactory.decodeFile(path+"/Playa_"+getId()+".jpg",options);
            imagenBitmap=ret;
            return ret;
        }catch (Exception e){
            return null;
        }
    }

    public void setImagenBitmap(Bitmap imagen) {
        imagenBitmap = imagen;
    }



}