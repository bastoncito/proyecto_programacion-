import java.util.Date;

public class Ejecutora {
    public static void main(String[] args) {
        
        // Creación de la base de datos
        BaseDatos b1 = new BaseDatos();
        
        // Creación usuarios, fechas y tareas
        Usuario u1 = new Usuario("Matias Gomez", "mgomez22@alumnos.utalca.cl", "Contra5eña$EGURA");
        Usuario u2 = new Usuario("Juan Carlos Bodoque", "elAlmadeLaFiesta@31minutos.cl", "Nota.Verde31");
        Usuario u3 = new Usuario("Carlos Mendoza", "cmendoza.profesional@gmail.com", "M3nd0z4*97");
        Usuario u4 = new Usuario("Javier Ruiz", "javier.ruiz.data@scientist.com", "J@v1erZiuR");

        Date fecha_actual = new Date();
        Date fechaTresHoras = new Date(System.currentTimeMillis() + 10800000); // +3 horas
        Date fechaDoceHoras = new Date(System.currentTimeMillis() + 43200000); // +12 horas
        Date fechaManana = new Date(System.currentTimeMillis() + 86400000); // +1 día
        Date fechaDiaSiguiente = new Date(System.currentTimeMillis() + 172800000); // +2 días

        Tarea t1 = new Tarea("Estudiar EDO", "Repasar la materia vista en clases", 150, fechaManana);
        Tarea t2 = new Tarea("Salir a trotar", "Salir a trotar por 20 minutos", 100, fecha_actual);
        Tarea t3 = new Tarea("Hacer ejercicio", "30 minutos de gym", 80, fechaDoceHoras);
        Tarea t4 = new Tarea("Leer libro", "Avanzar 2 capítulos de 'Luna de Pluton'", 90, fechaManana);
        Tarea t5 = new Tarea("Limpieza del hogar", "Limpiar toda la casa profundamente", 160, fechaDiaSiguiente);
        Tarea t6 = new Tarea("Aprender inglés", "Practicar 30 minutos en Duolingo", 60, fechaTresHoras);
        Tarea t7 = new Tarea("Meditación matutina", "10 minutos de meditación al despertar", 30, fechaTresHoras);
        Tarea t8 = new Tarea("Aprender nuevo framework", "Investigar por 2 horas Spring Boot", 190, fechaDiaSiguiente);

        // Asignación tareas a usuarios
        u1.agregarTarea(t1);
        u1.agregarTarea(t6);
        u1.agregarTarea(t8);

        u2.agregarTarea(t2);
        u2.agregarTarea(t7);

        u3.agregarTarea(t3);
        u3.agregarTarea(t5);
        u3.agregarTarea(t8);

        u4.agregarTarea(t4);
        u4.agregarTarea(t2);

        // Agregar usuarios a la base de datos
        b1.agregarUsuario(u1);
        b1.agregarUsuario(u2);
        b1.agregarUsuario(u3);
        b1.agregarUsuario(u4);

        // Agregar y mostrar base de datos
        System.out.println("=== Base de datos ===");
        b1.imprimirTodosUsuarios();
        b1.guardarBaseDatos();

        //Pruebas de validaciones (try/catch ya que usan excepciones)
        System.out.println("Pruebas validacion v2\n");
        System.out.println("PARA USUARIOS\n");
        try{
            System.out.println("Prueba: Usuario nombre invalido (menor a 3 caracteres)");
            Usuario u = new Usuario("Pi", "correo.valido@email.com", "Contrasena.Valida1");
        }catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()+"\n");
        }

        try {
            System.out.println("Prueba: Usuario nombre muy largo");
            Usuario u = new Usuario("EsteEsUnNombreRidiculamenteLargoParaProbarValidacion", "correo.valido@email.com", "Contrasena.Valida1");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()+"\n");
        }

        try {
            System.out.println("Prueba: Correo invalido sin arroba @");
            Usuario u = new Usuario("Nombre Valido", "correo-sin-arroba.com", "Contrasena.Valida1");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()+"\n");
        }

        try {
            System.out.println("Prueba: Correo inválido (sin dominio)");
            Usuario u = new Usuario("Nombre Valido", "correo@.com", "Contrasena.Valida1");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Prueba: Contraseña inválida (demasiado corta)");
            Usuario u = new Usuario("Nombre Valido", "correo.valido@email.com", "Pass1!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Prueba: Contraseña inválida (sin mayúscula)");
            Usuario u = new Usuario("Nombre Valido", "correo.valido@email.com", "password1!");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage()); 
        }

        try {
            System.out.println("Prueba: Contraseña inválida (sin número)");
            Usuario u = new Usuario("Nombre Valido", "correo.valido@email.com", "Password!");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Prueba: Contraseña inválida (sin carácter especial)");
            Usuario u = new Usuario("Nombre Valido", "correo.valido@email.com", "Password123");
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

        System.out.println("PARA TAREAS\n");
        try {
            System.out.println("Prueba: Nombre inválido (nulo/vacío)");
            Tarea t = new Tarea("", "Descripción buena", 30, new Date(System.currentTimeMillis() + 86400000));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Prueba: Descripción inválida (nula/vacía)");
            Tarea t = new Tarea("Tarea buena", "", 30, new Date(System.currentTimeMillis() + 86400000));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Prueba: Exp negativa/cero");
            Tarea t = new Tarea("Tarea buena", "Descripción buena", 0, new Date(System.currentTimeMillis() + 86400000));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Prueba: fecha inválida (anterior a la actual)");
            Tarea t = new Tarea("Tarea buena", "Buena descripción", 30, new Date(System.currentTimeMillis() - 86400000));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + "\n");
        }

        try {
            System.out.println("Inscripción de tarea válida");
            Tarea tValida = new Tarea("Repasar apuntes para prueba", "Revisar apuntes de programación para la prueba de la próxima semana", 250, new Date(System.currentTimeMillis() + 86400000));
            System.out.println("Tarea \"" + tValida.getNombre() + "\" creada exitosamente.\n");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + "\n");
        }

        System.out.println("USUARIOS/TAREAS REPETIDAS\n");
        try{
            b1.agregarUsuario(u1);
        }catch(Exception e){
            System.out.println(e.getMessage() + "\n");
        }
        try{
            u1.agregarTarea(t2);
        }catch(Exception e){
            System.out.println(e.getMessage() + "\n");
        }       
    }
}