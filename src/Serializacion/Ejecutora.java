import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Ejecutora {
    public static void main(String[] args) {
        // creacion usuarios, fechas y tareas
        Usuario u1=new Usuario("Matias", "matias123@alumnos.utalca.cl", "contraseñaSEGURA");
        Usuario u2=new Usuario("Carlos", "carloselmaspro@alumnos.utalca.cl", "TodoAlRojo");

        Date fecha_actual=new Date();
        Date fechaManana = new Date(System.currentTimeMillis() + 86400000); // +1 día

        Tarea t1=new Tarea("Estudiar EDO", "Repasar la materia vista en clases", 150, fechaManana);
        Tarea t2=new Tarea("Salir a trotar", "Salir a trotar por 20 minutos", 100, fecha_actual);

        //asignacion tareas a usuarios
        u1.agregarTarea(t1);
        u1.agregarTarea(t2);

        /** 
        System.out.println(u1.toString());
        System.out.println(u1.getTareas().toString());
        System.out.println(u2.toString());
        **/

        //serializacion
        BaseDatos b1= new BaseDatos();
        //b1.agregarUsuario(u1);
        //b1.agregarUsuario(u2);
        b1.imprimirTodosUsuarios();
        b1.guardarBaseDatos();
        
    }
}
