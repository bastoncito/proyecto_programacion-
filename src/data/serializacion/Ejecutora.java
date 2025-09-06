import java.util.Date;

public class Ejecutora {
    public static void main(String[] args) {
        /* 
        // Creación de la base de datos
        BaseDatos b1 = new BaseDatos();
        
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
        //b1.agregarUsuario(u1);
        //b1.agregarUsuario(u2);

        // Comentar hasta aca para mostrar serializacion
        //System.out.println("=== Base de datos ===");
        //b1.imprimirTodosUsuarios();

        //b1.guardarBaseDatos();
        */
        System.out.println("Pruebas validacion v2\n");
        try{
            System.out.println("Prueba: Usuario nombre invalido (menor a 3 caracteres)");
            Usuario u1 = new Usuario("Pi", "correo.valido@email.com", "Contrasena.Valida1");
        }catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()+"\n");
        }

        try {
            System.out.println("Prueba: Usuario nombre muy largo");
            Usuario u2 = new Usuario("EsteEsUnNombreRidiculamenteLargoParaProbarValidacion", "correo.valido@email.com", "Contrasena.Valida1");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()+"\n");
        }

        try {
            System.out.println("Prueba: Correo invalido sin arroba @");
            Usuario u3 = new Usuario("Nombre Valido", "correo-sin-arroba.com", "Contrasena.Valida1");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()+"\n");
        }

        try {
            System.out.println("Prueba: Correo inválido (sin dominio)");
            Usuario u4 = new Usuario("Nombre Valido", "correo@.com", "Contrasena.Valida1");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Prueba: Contraseña inválida (demasiado corta)");
            Usuario u5 = new Usuario("Nombre Valido", "correo.valido@email.com", "Pass1!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Prueba: Contraseña inválida (sin mayúscula)");
            Usuario u6 = new Usuario("Nombre Valido", "correo.valido@email.com", "password1!");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage()); 
        }

        try {
            System.out.println("Prueba: Contraseña inválida (sin número)");
            Usuario u7 = new Usuario("Nombre Valido", "correo.valido@email.com", "Password!");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Prueba: Contraseña inválida (sin carácter especial)");
            Usuario u8 = new Usuario("Nombre Valido", "correo.valido@email.com", "Password123");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Prueba: Creación de un usuario completamente válido");
            Usuario uValido = new Usuario("Matias Gomez", "matias.gomez@email.com", "Contrasena.Valida1");
            System.out.println("Usuario '" + uValido.getNombre_usuario() + "' creado exitosamente.\n");
        } catch (IllegalArgumentException e) {
            // Este bloque no debería ejecutarse
            System.err.println("ERROR INESPERADO: " + e.getMessage());
        }
    }
}