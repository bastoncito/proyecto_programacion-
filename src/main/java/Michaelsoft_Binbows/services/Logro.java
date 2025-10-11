package Michaelsoft_Binbows.services;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "logros")
public class Logro {
    
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 500)
    private String descripcion;
    
    @Column(nullable = false)
    private int experienciaRecompensa;
    
    // Relación muchos a muchos con Usuario
    @ManyToMany(mappedBy = "logros")
    private List<Usuario> usuarios = new ArrayList<>();

    /**
     * Constructor vacío requerido por JPA.
     * No usar directamente - usar el constructor con parámetros.
     */
    protected Logro() {
        // Constructor protegido para JPA
    }

    /**
     * Constructor de la clase Logro.
     * Valida todos los parámetros antes de crear el objeto.
     * Si alguna validación falla, lanza una excepción para prevenir la creación
     * de un objeto en estado inválido.
     *
     * @param id El identificador único del logro 
     * @param nombre El nombre visible para el usuario (ej. "¡Primeros Pasos!").
     * @param descripcion La explicación de cómo se obtiene el logro.
     * @param experienciaRecompensa La cantidad de EXP que otorga el logro (puede ser 0).
     */
    public Logro(String id, String nombre, String descripcion, int experienciaRecompensa) {

        // Validacion de ID: sin espacios y en mayúsculas (por convención).
        if (id == null || id.trim().isEmpty() || id.contains(" ")) {
            throw new IllegalArgumentException("El ID del logro no puede ser nulo, vacío o contener espacios.");
        }

        // Validacion del Nombre: no puede estar vacío y debe tener una longitud razonable.
        if (nombre == null || nombre.trim().isEmpty() || nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre del logro no puede estar vacío y debe tener 100 caracteres o menos.");
        }

        // Validacion de la Descripción: no puede estar vacía.
        if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción del logro no puede estar vacía y debe tener 500 caracteres o menos.");
        }
        
        // Validacion de la experienciaRecompensa: no puede ser negativa.
        if (experienciaRecompensa < 0) {
            throw new IllegalArgumentException("La recompensa en puntos no puede ser negativa.");
        }

        // Si el objeto no lanzo excepciones se crea
        this.id = id.trim().toUpperCase();
        this.nombre = nombre.trim();
        this.descripcion = descripcion.trim();
        this.experienciaRecompensa = experienciaRecompensa;
    }

    // GETTERS
    public String getId() { 
        return id; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }
    
    public int getPuntosRecompensa() { 
        return experienciaRecompensa; 
    }
    
    // Getter adicional para compatibilidad con código que use getExperienciaRecompensa
    public int getExperienciaRecompensa() { 
        return experienciaRecompensa; 
    }
    
    public List<Usuario> getUsuarios() {
        return usuarios;
    }
    
    // SETTERS necesarios para JPA (aunque idealmente no se usen directamente)
    protected void setId(String id) {
        this.id = id;
    }
    
    protected void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    protected void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    protected void setExperienciaRecompensa(int experienciaRecompensa) {
        this.experienciaRecompensa = experienciaRecompensa;
    }
    
    protected void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Logro)) return false;
        Logro logro = (Logro) o;
        return id != null && id.equals(logro.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}