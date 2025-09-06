import java.util.Date;

public class Ejecutora {
    public static void main(String[] args) {
        // Creación de la base de datos
        BaseDatos b1 = new BaseDatos();
        
        // Comentar desde aca para mostrar serializacion
        // Creación usuarios, fechas y tareas
        Usuario u1 = new Usuario("Matias", "matias123@alumnos.utalca.cl", "cotraseñaSEGURA");
        Usuario u2 = new Usuario("Carlos", "carloselmaspro@alumnos.utalca.cl", "TodoAlRojo");

        Date fecha_actual = new Date();
        Date fechaManana = new Date(System.currentTimeMillis() + 86400000); // +1 día

        Tarea t1 = new Tarea("Estudiar EDO", "Repasar la materia vista en clases", 150, fechaManana);
        Tarea t2 = new Tarea("Salir a trotar", "Salir a trotar por 20 minutos", 100, fecha_actual);
        Tarea t3 = new Tarea("Hacer ejercicio", "30 minutos de gym", 80, fecha_actual);

        // Asignación tareas a usuarios
        u1.agregarTarea(t1);
        u1.agregarTarea(t2);
        u2.agregarTarea(t3);

        // Agregar usuarios a la base de datos
        b1.agregarUsuario(u1);
        b1.agregarUsuario(u2);

        // Comentar hasta aca para mostrar serializacion
        System.out.println("=== Base de datos ===");
        b1.imprimirTodosUsuarios();

        b1.guardarBaseDatos();
    }
}
