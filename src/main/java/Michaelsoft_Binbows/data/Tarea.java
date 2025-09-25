package Michaelsoft_Binbows.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class Tarea{
    private String nombre, descripcion;
    private int exp;
    private Date fecha_expiracion;
    // constructor
    public Tarea (String nombre, String descripcion, int exp, Date fecha_expiracion){
        setNombre(nombre);
        setDescripcion(descripcion);
        setExp(exp);
        setFecha_expiracion(fecha_expiracion);
    }
    public String getNombre() {
        return nombre;
    }
    public Date getFecha_expiracion() {
        return fecha_expiracion;
    }
    public int getExp() {
        return exp;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setNombre(String nombre) {
        if(nombre == null || nombre.isEmpty()){
            throw new IllegalArgumentException("El nombre de la tarea no puede estar vacío.");
        }if( nombre.length()<5 || nombre.length() > 30){
            throw new IllegalArgumentException("El nombre de la tarea debe tener entre 5 y 30 carácteres");
        }
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) {
        if(descripcion == null || descripcion.isEmpty()){
            throw new IllegalArgumentException("La descripción de la tarea no puede estar vacía.");
        }if(descripcion.length()<5 || descripcion.length() > 70){
            throw new IllegalArgumentException("La descripción debe tener entre 5 y 80 carácteres");
        }
        this.descripcion = descripcion;
    }
    public void setExp(int exp) {
        if(exp <= 0){
            throw new IllegalArgumentException("La exp de la tarea no puede ser menor a 1.");
        }
        this.exp = exp;
    }
    public void setFecha_expiracion(Date fecha_expiracion) {
        if(fecha_expiracion == null){
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        }
        Date fecha_actual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//Simplificar el formato
        String fechaTarea = sdf.format(fecha_expiracion);
        String fechaHoy = sdf.format(fecha_actual);
        if (fechaTarea.compareTo(fechaHoy) < 0) {
            throw new IllegalArgumentException("La fecha debe ser igual o posterior a hoy.");
        }
        this.fecha_expiracion = fecha_expiracion;
    }
    public boolean tareaExistePorNombre(List<Tarea> tareas, Tarea tarea) {
        for (Tarea t : tareas) {
            if (t.getNombre().equalsIgnoreCase(tarea.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public boolean tareaExistePorDescripcion(List<Tarea> tareas, Tarea tarea) {
        for (Tarea t : tareas) {
            if (t.getDescripcion().equalsIgnoreCase(tarea.getDescripcion())) {
                return true;
            }
        }
        return false;
    }
   
    @Override
    public String toString() {
        return "La tarea '"+nombre+"' ("+descripcion+") que otorga "+exp+" puntos de experiencia, vence el "+ fecha_expiracion;
    }
}
