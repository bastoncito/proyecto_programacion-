package Michaelsoft_Binbows.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Tarea{
    private String nombre, descripcion;
    private int exp;
    private LocalDateTime fechaExpiracion;
    private LocalDateTime fechaCompletada = null;
    // constructor
        public Tarea (String nombre, String descripcion, String dificultad){
            setNombre(nombre);
            setDescripcion(descripcion);
            //Se calcula la experiencia en base a una de 6 categorías de dificultad
            setExp(Dificultad.obtenerExpPorDificultad(dificultad));
            setFechaExpiracion(Dificultad.obtenerDíasPorDificultad(dificultad));
        }
    public String getNombre() {
        return nombre;
    }
    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
    public int getExp() {
        return exp;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public LocalDateTime getFechaCompletada() {
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
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        if(fechaExpiracion == null){
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        }
        LocalDateTime fechaActual = LocalDateTime.now();
        if(fechaExpiracion.isBefore(fechaActual)){ 
            throw new IllegalArgumentException("La fecha debe ser igual o posterior a hoy.");
        }
        // revisa si la fecha está al menos 1 hora en el futuro
        long diferenciaMilisegundos = Duration.between(fechaActual, fechaExpiracion).toMillis();
        if (diferenciaMilisegundos < 60 * 60 * 1000) {
            throw new IllegalArgumentException("La fecha debe ser al menos 1 hora posterior a la actual.");
        }
        this.fechaExpiracion = fechaExpiracion;
    }
    public void setFechaCompletada(LocalDateTime fechaCompletada) {
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
