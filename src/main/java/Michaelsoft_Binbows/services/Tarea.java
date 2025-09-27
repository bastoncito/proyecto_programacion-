package Michaelsoft_Binbows.services;

import java.util.Date;
import java.util.List;
public class Tarea{
    private String nombre, descripcion;
    private int exp;
    private Date fechaExpiracion;
    private Date fechaCompletada = null;
    // constructor
    public Tarea (String nombre, String descripcion, int exp, Date fecha_expiracion){
        setNombre(nombre);
        setDescripcion(descripcion);
        setExp(exp);
        setFechaExpiracion(fecha_expiracion);
    }
    public String getNombre() {
        return nombre;
    }
    public Date getFechaExpiracion() {
        return fechaExpiracion;
    }
    public int getExp() {
        return exp;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public Date getFechaCompletada() {
        return fechaCompletada;
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
    public void setFechaExpiracion(Date fechaExpiracion) {
        if(fechaExpiracion == null){
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        }
        Date fechaActual = new Date();
        if(fechaExpiracion.before(fechaActual)){ 
            throw new IllegalArgumentException("La fecha debe ser igual o posterior a hoy.");
        }
        //revisa si 
        long diferenciaMilisegundos = fechaExpiracion.getTime() - fechaActual.getTime();
        if (diferenciaMilisegundos < 60 * 60 * 1000) {
            throw new IllegalArgumentException("La fecha debe ser al menos 1 hora posterior a la actual.");
        }
        this.fechaExpiracion = fechaExpiracion;
    }
    public void setFechaCompletada(Date fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
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
        return "La tarea '"+nombre+"' ("+descripcion+") que otorga "+exp+" puntos de experiencia, vence el "+ fechaExpiracion;
    }
}
