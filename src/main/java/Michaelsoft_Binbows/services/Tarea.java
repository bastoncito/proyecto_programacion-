package Michaelsoft_Binbows.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import Michaelsoft_Binbows.exceptions.TareaInvalidaException;
import jakarta.persistence.*;

@Entity
@Table(name = "tareas")
public class Tarea{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    private int exp;
    
    private LocalDateTime fechaExpiracion;
    private LocalDateTime fechaCompletada = null;

    // Relación con Usuario (tarea activa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    // Relación con Usuario (tarea completada)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_completado_id")
    private Usuario usuarioCompletado;

    // Constructor vacío protegido para JPA
    public Tarea() {
        // Constructor vacío requerido por JPA
    }

    public Tarea (String nombre, String descripcion, String dificultad) throws TareaInvalidaException{
        setNombre(nombre);
        setDescripcion(descripcion);
        //Se calcula la experiencia en base a una de 6 categorías de dificultad
        setExp(Dificultad.obtenerExpPorDificultad(dificultad));
        setFechaExpiracion(Dificultad.obtenerDíasPorDificultad(dificultad));
    }
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Usuario getUsuarioCompletado() {
        return usuarioCompletado;
    }
    
    public void setUsuarioCompletado(Usuario usuarioCompletado) {
        this.usuarioCompletado = usuarioCompletado;
    }
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    public void setNombre(String nombre) throws TareaInvalidaException {
        if(nombre == null || nombre.isEmpty()){
            throw new TareaInvalidaException("El nombre de la tarea no puede estar vacío.");
        }if( nombre.length()<5 || nombre.length() > 30){
            throw new TareaInvalidaException("El nombre de la tarea debe tener entre 5 y 30 carácteres");
        }
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) throws TareaInvalidaException {
        if(descripcion == null || descripcion.isEmpty()){
            throw new TareaInvalidaException("La descripción de la tarea no puede estar vacía.");
        }if(descripcion.length()<5 || descripcion.length() > 70){
            throw new TareaInvalidaException("La descripción debe tener entre 5 y 80 carácteres");
        }
        this.descripcion = descripcion;
    }
    public void setExp(int exp) throws TareaInvalidaException {
        if(exp <= 0){
            throw new TareaInvalidaException("La exp de la tarea no puede ser menor a 1.");
        }
        this.exp = exp;
    }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) throws TareaInvalidaException {
        if(fechaExpiracion == null){
            throw new TareaInvalidaException("La fecha no puede estar vacía.");
        }
        LocalDateTime fechaActual = LocalDateTime.now();
        if(fechaExpiracion.isBefore(fechaActual)){ 
            throw new TareaInvalidaException("La fecha debe ser igual o posterior a hoy.");
        }
        // revisa si la fecha está al menos 1 hora en el futuro
        long diferenciaMilisegundos = Duration.between(fechaActual, fechaExpiracion).toMillis();
        if (diferenciaMilisegundos < 60 * 60 * 1000) {
            throw new TareaInvalidaException("La fecha debe ser al menos 1 hora posterior a la actual.");
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
