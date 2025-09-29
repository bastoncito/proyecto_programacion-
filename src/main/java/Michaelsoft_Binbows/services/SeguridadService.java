package Michaelsoft_Binbows.services;


import org.springframework.stereotype.Service;

/**
 * Esta clase contiene la lógica de negocio relacionada con la seguridad y los permisos.
 * Al marcarla como @Service, le decimos a Spring que gestione su ciclo de vida.
 */
@Service
public class SeguridadService {

    /**
     * Comprueba si un usuario (actor) tiene permiso para editar a otro usuario (objetivo)
     * basándose en la jerarquía de roles.
     *
     * @param actor El usuario que intenta realizar la acción (quien está logueado).
     * @param objetivo El usuario que va a ser editado.
     * @return true si el actor tiene permiso, false en caso contrario.
     */
    public boolean puedeEditar(Usuario actor, Usuario objetivo) {
        // Regla básica: nadie puede realizar acciones si falta información.
        if (actor == null || objetivo == null) {
            return false;
        }

        // Un ADMIN puede editar a cualquiera (MODERADOR o USUARIO),
        // pero por seguridad, no le permitiremos editarse a sí mismo desde esta interfaz.
        if (actor.getRol() == Rol.ADMIN) {
            // Un admin no puede editar a otro admin (o a sí mismo) para evitar conflictos.
            return objetivo.getRol() != Rol.ADMIN;
        }

        // Un MODERADOR solo puede editar a usuarios con el rol USUARIO.
        if (actor.getRol() == Rol.MODERADOR) {
            return objetivo.getRol() == Rol.USUARIO;
        }

        // Un USUARIO no tiene permisos para editar a nadie.
        // Si el actor no es ni ADMIN ni MODERADOR, se llega aquí.
        return false;
    }

    /*
    * Comprueba si un usuario (actor) tiene permiso para eliminar a otro (objetivo).
    *
    * @param actor El usuario que intenta realizar la acción.
    * @param objetivo El usuario que va a ser eliminado.
    * @return true si el actor tiene permiso, false en caso contrario.
    */
    public boolean puedeEliminar(Usuario actor, Usuario objetivo) {
        // Reglas de seguridad básicas que no cambian
        if (actor == null || objetivo == null) {
            return false;
        }
        // Regla fundamental: Nadie puede eliminarse a sí mismo.
        // (Usamos el correo como identificador único para ser más seguros)
        if (actor.getCorreoElectronico().equalsIgnoreCase(objetivo.getCorreoElectronico())) {
            return false;
        }

        // --- LÓGICA DE ROLES ---

        // Regla 1: Un ADMIN puede eliminar a MODERADORES y USUARIOS.
        if (actor.getRol() == Rol.ADMIN) {
            // La única restricción para un Admin es que no puede eliminar a otro Admin.
            return objetivo.getRol() != Rol.ADMIN;
        }
        
        // Regla 2: Un MODERADOR solo puede eliminar a usuarios con el rol USUARIO.
        if (actor.getRol() == Rol.MODERADOR) {
            return objetivo.getRol() == Rol.USUARIO;
        }
        
        // Si el actor no es ni ADMIN ni MODERADOR (es decir, es USUARIO), no puede eliminar a nadie.
        return false;
    }
}