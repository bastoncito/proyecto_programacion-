import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
public class Usuario implements Serializable{
    String nombre_usuario,correo_electronico,contraseña;
    private List<Tarea> tareas;
    public Usuario(String nombre_usuario, String correo_electronico, String contraseña){
        this.nombre_usuario=nombre_usuario;
        this.correo_electronico=correo_electronico;
        this.contraseña=contraseña;
        this.tareas=new ArrayList<>();
    }
    public String getNombre_usuario() {
        return nombre_usuario;
    }
    public String getCorreo_electronico() {
        return correo_electronico;
    }
    public String getContraseña() {
        return contraseña;
    }
    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }
    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
    /**
     * Recibe una Tarea como parametro
     * la agrega a la base de datos si su nombre, descricpcion y exp son validos
     */
    public boolean agregarTarea(Tarea tarea){//TRABAJAR AQUI---->>>Validar si la tarea se puede agregar
        if (tarea == null) {
            System.out.println("Error: La tarea no puede ser nula.");
            return false;
        }
        // Validar que el nombre no esté vacío
        if (tarea.getNombre() == null) {
            System.out.println("Error: El nombre de la tarea no puede estar vacía.");
            return false;
        }
        // Validar que la descripcion no esté vacía
        if (tarea.getDescripcion() == null) {
            System.out.println("Error: La descripción de la tarea no puede estar vacía.");
            return false;
        }
        // Validar que exp no esté vacía y sea positiva
        if (tarea.getExp() == 0) {
            System.out.println("Error: La exp de la tarea no puede estar vacía.");
            return false;
        }
        if (tarea.getExp() < 0) {
            System.out.println("Error: La exp de la tarea no puede ser negativa.");
            return false;
        }
        // Validar que la fecha no este vacia y no sea menor a la fecha actual
        if (tarea.getFecha_expiracion() == null) {
            System.out.println("Error: La fecha no puede estar vacía.");
            return false;
        }
        Date fecha_actual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//Simplificar el formato
        String fechaTarea = sdf.format(tarea.getFecha_expiracion());
        String fechaHoy = sdf.format(fecha_actual);

        if (fechaTarea.compareTo(fechaHoy) < 0) {
            System.out.println("Error: La fecha debe ser igual o posterior a hoy.");
            return false;
        }
        // Validar nombre
        if (tareaExistePorNombre(tarea.getNombre())) {
            System.out.println("Ya existe una tarea con el nombre: " + tarea.getNombre());
            return false;
        }

        // Validar descripción
        if (tareaExistePorDescripcion(tarea.getDescripcion())) {
            System.out.println("Ya existe una tarea con la descripción: " + tarea.getDescripcion());
            return false;
        }

        // Si pasa las validaciones, la agregamos
        tareas.add(tarea);
        System.out.println("Tarea '" + tarea.getNombre() + "' agregada exitosamente.");
        return true;
    }

    public boolean tareaExistePorNombre(String nombre){
        for (Tarea tareaExistente : tareas) {
            if (tareaExistente.getNombre().equalsIgnoreCase(nombre)) {
                System.out.println("Ya existe una tarea con el nombre: " + nombre);
                return true;
            }
        }
        return false;
    }
    public boolean tareaExistePorDescripcion(String descripcion){
        for (Tarea tareaExistente : tareas) {
            if (tareaExistente.getDescripcion().equalsIgnoreCase(descripcion)) {
                System.out.println("Ya existe una tarea con la descripcion: " + descripcion);
                return true;
            }
        }
        return false;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario: ").append(nombre_usuario).append("\n")
          .append("Correo: ").append(correo_electronico).append("\n")
          .append("Número de tareas: ").append(tareas.size()).append("\n");
          return sb.toString();
    }
}
