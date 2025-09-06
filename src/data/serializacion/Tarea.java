import java.io.Serializable;
import java.util.Date;
public class Tarea implements Serializable{
    private String nombre, descripcion;
    private int exp;
    private Date fecha_expiracion;
    // constructor
    public Tarea (String nombre, String descripcion, int exp,Date fecha_expiracion){
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.exp=exp;
        this.fecha_expiracion=fecha_expiracion;
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
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setExp(int exp) {
        this.exp = exp;
    }
    public void setFecha_expiracion(Date fecha_expiracion) {
        this.fecha_expiracion = fecha_expiracion;
    }
    @Override
    public String toString() {
        return "La tarea '"+nombre+"' ("+descripcion+") que otorga "+exp+" puntos de experiencia, vence el "+ fecha_expiracion;
    }
}
