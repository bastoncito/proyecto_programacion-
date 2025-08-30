import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    public void agregarTarea(Tarea tarea){
        tareas.add(tarea);
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
