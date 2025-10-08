
package Michaelsoft_Binbows.services;


import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;

/**
 * Script para poblar la base de datos con 5 usuarios de ejemplo.
 * Esta versión es compatible con el constructor de Tarea que acepta
 * un String de dificultad para calcular la EXP y la fecha de expiración.
 */
public class Ejecutora {

    /* 
    public static void main(String[] args) {
        System.out.println("--- Iniciando Script de Población ---");
        BaseDatos db = new BaseDatos();

        // Crear una Pool de tareas disponibles
        System.out.println("\nCreando pool de tareas disponibles...");
        List<Tarea> poolDeTareas = new ArrayList<>();
        try {
            poolDeTareas.add(new Tarea("Revisar Correo", "Limpiar bandeja de entrada y responder urgentes", "Fácil"));
            poolDeTareas.add(new Tarea("Hacer Ejercicio", "30 minutos de cardio o pesas", "Medio"));
            poolDeTareas.add(new Tarea("Estudiar Spring Boot", "Completar el capítulo sobre Controladores", "Difícil"));
            poolDeTareas.add(new Tarea("Llamar a la familia", "Ponerse al día con la familia", "Muy facil"));
            poolDeTareas.add(new Tarea("Limpiar la Cocina", "Lavar platos y limpiar encimeras", "Fácil"));
            poolDeTareas.add(new Tarea("Planificar la Semana", "Organizar el calendario para los próximos 7 días", "Medio"));
            System.out.println("-> Pool con " + poolDeTareas.size() + " tareas creado exitosamente.");
        } catch (Exception e) {
            System.err.println("!! ERROR FATAL al crear el pool de tareas. Abortando. Causa: " + e.getMessage());
            return;
        }


        // Usuario 1
        try {
            System.out.println("\nCreando Usuario 1");
            Usuario admin = new Usuario("Admin", "admin@michaelsoftBinbows.com", "SuperClaveSegura123!");
            admin.setRol(Rol.ADMIN);
            admin.agregarTarea(poolDeTareas.get(2)); 
            admin.agregarTarea(poolDeTareas.get(5)); 
            db.agregarUsuario(admin);
            System.out.println("-> Usuario agregado.");
        } catch (Exception e) {
            System.err.println("-> ERROR al crear Usuario: " + e.getMessage());
        }

        // Usuario 2
        try {
            System.out.println("\nCreando Usuario 2");
            Usuario moderador = new Usuario("Matias Gomez", "mgomez22@alumnos.utalca.cl", "Saquenme.De.ED0");
            moderador.setRol(Rol.MODERADOR);
            moderador.agregarTarea(poolDeTareas.get(0)); 
            moderador.agregarTarea(poolDeTareas.get(4)); 
            db.agregarUsuario(moderador);
            System.out.println("-> Usuario agregado.");
        } catch (Exception e) {
            System.err.println("-> ERROR al crear Usuario: " + e.getMessage());
        }

        // Usuario 3 
        try {
            System.out.println("\nCreando Usuario 3");
            Usuario usuarioActivo = new Usuario("Juan Carlos Bodoque", "elAlmadeLaFiesta@31minutos.cl", "Nota.Verde31");
            // Rol por defecto: USUARIO
            usuarioActivo.agregarTarea(poolDeTareas.get(1)); 
            usuarioActivo.agregarTarea(poolDeTareas.get(3)); 
            db.agregarUsuario(usuarioActivo);
            System.out.println("-> Usuario agregado.");
        } catch (Exception e) {
            System.err.println("-> ERROR al crear Usuario: " + e.getMessage());
        }

        // Usuario 4 
        try {
            System.out.println("\nCreando Usuario 4");
            Usuario usuarioNuevo = new Usuario("Valerie Tron", "valtron@gmail.com", "g00dP@55w0rd");
            // Rol por defecto: USUARIO
            usuarioNuevo.agregarTarea(poolDeTareas.get(0)); 
            db.agregarUsuario(usuarioNuevo);
            System.out.println("-> Usuario agregado.");
        } catch (Exception e) {
            System.err.println("-> ERROR al crear Usuario: " + e.getMessage());
        }

        // Usuario 5 
        try {
            System.out.println("\nCreando Usuario 5");
            Usuario otroUsuario = new Usuario("Julia Campos", "jcampos@hotmail.com", "tr!Rep3tae");
            // Sin tareas iniciales
            // Rol por defecto: USUARIO
            db.agregarUsuario(otroUsuario);
            System.out.println("-> Usuario agregado.");
        } catch (Exception e) {
            System.err.println("-> ERROR al crear Usuario: " + e.getMessage());
        }

        // Serializacion
        System.out.println("\n--- Proceso de Población Finalizado ---");
        db.guardarBaseDatos();
        System.out.println("\n--- Estado Final de la Base de Datos ---");
        db.imprimirTodosUsuarios();
    }
    */
}