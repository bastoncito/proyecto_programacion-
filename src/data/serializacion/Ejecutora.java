import java.util.Date;

public class Ejecutora {
    private static final int hora = 3600000; //1 hora en milisegundos
    private static final int dia = 86400000; //1 dia en milisegundos
    public static void main(String[] args) {
        
        // Creación de la base de datos
        BaseDatos b1 = new BaseDatos();
        
        // Creación usuarios, fechas y tareas
        Usuario u1 = new Usuario("Matias Gomez", "mgomez22@alumnos.utalca.cl", "Contra5eña$EGURA");
        Usuario u2 = new Usuario("Juan Carlos Bodoque", "elAlmadeLaFiesta@31minutos.cl", "Nota.Verde31");
        Usuario u3 = new Usuario("Carlos Mendoza", "cmendoza.profesional@gmail.com", "M3nd0z4*97");
        Usuario u4 = new Usuario("Javier Ruiz", "javier.ruiz.data@scientist.com", "J@v1erZiuR");
        Usuario ubr1 = new Usuario("Federico Palacios", "fpalacios@alumnos.utalca.cl", "CH0col@teMA77er");
        Usuario ubr2 = new Usuario("Mario", "mariog@yahoo.es", "7\\ph3xTwi|\\|");
        Usuario ubr3 = new Usuario("Julia", "jcampos@hotmail.com", "tr!Rep3tae");
        Usuario ubr4 = new Usuario("Miguelito", "mhernandez@alumnos.utalca.cl", "P0Wersl@vE");
        Usuario ubr5 = new Usuario("Valerie", "valtron@gmail.com", "g00dP@55w0rd");
        Usuario uh1 = new Usuario("Alejandro Parra","aleparra@gmail.com","P4rr4!76");
        Usuario uh2 = new Usuario("Cristobal Figueroa","criisdxd@gmail.com","b4lU.7FC");
        Usuario uh3 = new Usuario("Luis Retamal","lretamal@alumnos.utalca.cl","hArdy@04");
        Usuario uh4 = new Usuario("Mauro Friz","kikoman767@gmail.com","gh0St!44");
        Usuario ud1 = new Usuario("Cristóbal Herrera", "cherrera21@alumnos.utalca.cl","Anashe123." );
        Usuario ud2 = new Usuario("Diego Pezoa", "dpezoa21@alumnos.utalca.cl","Chancho!21" );
        Usuario ud3 = new Usuario("Matías Pezoa", "mati.pezoa11@gmail.com","Roblox.lol1" );
        Usuario ud4 = new Usuario("Cecilia Oses", "edith04@gmail.com","Ceciceci!chl2" );
        

        Date fecha_actual = new Date();
        Date fechaTresHoras = new Date(System.currentTimeMillis() + hora*3); // +3 horas
        Date fechaDoceHoras = new Date(System.currentTimeMillis() + hora*12); // +12 horas
        Date fechaManana = new Date(System.currentTimeMillis() + dia); // +1 día
        Date fechaDiaSiguiente = new Date(System.currentTimeMillis() + dia*2); // +2 días

        Tarea t1 = new Tarea("Estudiar EDO", "Repasar la materia vista en clases", 150, fechaManana);
        Tarea t2 = new Tarea("Salir a trotar", "Salir a trotar por 20 minutos", 100, fecha_actual);
        Tarea t3 = new Tarea("Hacer ejercicio", "30 minutos de gym", 80, fechaDoceHoras);
        Tarea t4 = new Tarea("Leer libro", "Avanzar 2 capítulos de 'Luna de Pluton'", 90, fechaManana);
        Tarea t5 = new Tarea("Limpieza del hogar", "Limpiar toda la casa profundamente", 160, fechaDiaSiguiente);
        Tarea t6 = new Tarea("Aprender inglés", "Practicar 30 minutos en Duolingo", 60, fechaTresHoras);
        Tarea t7 = new Tarea("Meditación matutina", "10 minutos de meditación al despertar", 30, fechaTresHoras);
        Tarea t8 = new Tarea("Aprender nuevo framework", "Investigar por 2 horas Spring Boot", 190, fechaDiaSiguiente);
        Tarea tbr1 = new Tarea("Trabajar en proyecto", "Seh.", 150, fechaDoceHoras);
        Tarea tbr2 = new Tarea("Sacar hora para dentista", "Hace falta una hora para control", 25, fechaManana);
        Tarea tbr3 = new Tarea("Barrer", "Pasar la escoba por (al menos) todo el primer piso", 350, new Date(System.currentTimeMillis() + hora*2));
        Tarea tbr4 = new Tarea("Organizar refrigerador", "Ordenar alimentos y desechar lo vacío/vencido", 200, new Date(System.currentTimeMillis() + hora));
        Tarea td1 = new Tarea("Pasear a mascota.", "Sacar a pasear a tu mascota durante al menos 25 minutos.", 150, fechaManana);
        Tarea td2 = new Tarea("Aprender diagramas de Venn", "Investigar tecnicas para el desarrollo de diagramas de Venn", 100, fecha_actual);
        Tarea td3 = new Tarea("Dibujar algo sencillo.", "Mira a tu alrededor y dibuja algo que te llame la atención de forma sencilla", 100, fechaManana);
        Tarea td4 = new Tarea("Lavar la ropa.", "Junta toda la ropa sucio de tu pieza y lavala.", 150, fechaDoceHoras);

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

        ubr1.agregarTarea(tbr1);
        ubr1.agregarTarea(t4);

        ubr2.agregarTarea(tbr3);
        ubr2.agregarTarea(tbr4);

        ubr3.agregarTarea(tbr2);

        ubr4.agregarTarea(t8);

        ubr5.agregarTarea(tbr4);
        ubr5.agregarTarea(tbr1);

        uh1.agregarTarea(t4);
        uh1.agregarTarea(t7);

        uh2.agregarTarea(t2);
        uh2.agregarTarea(t6);
        uh2.agregarTarea(tbr3);

        uh3.agregarTarea(tbr1);
        uh3.agregarTarea(t1);
        uh3.agregarTarea(t8);

        uh4.agregarTarea(t3);
        uh4.agregarTarea(t5);

        ud1.agregarTarea(td1);
        ud1.agregarTarea(td2);

        ud2.agregarTarea(td3);
        ud2.agregarTarea(td4);

        ud3.agregarTarea(td1);
        ud3.agregarTarea(td4);

        ud4.agregarTarea(td2);
        ud4.agregarTarea(td3);

        // Agregar usuarios a la base de datos
        b1.agregarUsuario(u1);
        b1.agregarUsuario(u2);
        b1.agregarUsuario(u3);
        b1.agregarUsuario(u4);
        b1.agregarUsuario(ubr1);
        b1.agregarUsuario(ubr2);
        b1.agregarUsuario(ubr3);
        b1.agregarUsuario(ubr4);
        b1.agregarUsuario(ubr5);
        b1.agregarUsuario(uh1);
        b1.agregarUsuario(uh2);
        b1.agregarUsuario(uh3);
        b1.agregarUsuario(uh4);
        b1.agregarUsuario(ud1);
        b1.agregarUsuario(ud2);
        b1.agregarUsuario(ud3);
        b1.agregarUsuario(ud4);

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
            u1.agregarTarea(t1);
        }catch(Exception e){
            System.out.println(e.getMessage() + "\n");
        }       
    }
}