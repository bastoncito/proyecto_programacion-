package Michaelsoft_Binbows.services;

import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.model.Rol;
import org.springframework.stereotype.Service;

/**
 * Esta clase contiene la lógica de negocio relacionada con la seguridad y los permisos. Al marcarla
 * como @Service, le decimos a Spring que gestione su ciclo de vida.
 */
@Service
public class SeguridadService {

  /*
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

    // --- LÓGICA DE ROLES MEJORADA ---

    // Regla 1: Un ADMIN puede editar a cualquiera (MODERADOR o USUARIO),
    // pero no puede editar a otro ADMIN ni a sí mismo.
    if (actor.getRol() == Rol.ADMIN) {
      return objetivo.getRol() != Rol.ADMIN;
    }

    // Regla 2: Un MODERADOR solo puede editar a usuarios con el rol USUARIO.
    // Esto previene que un moderador pueda siquiera intentar abrir el modal
    // para editar a otro moderador o a un admin.
    if (actor.getRol() == Rol.MODERADOR) {
      return objetivo.getRol() == Rol.USUARIO;
    }

    // Un USUARIO no tiene permisos para editar a nadie.
    // Si el actor no es ni ADMIN ni MODERADOR, se llega aquí y devuelve false.
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

  /*
   * Comprueba si un usuario (actor) tiene permiso para ASIGNAR un rol específico.
   * Esta es la regla de negocio principal para la promoción y degradación de usuarios.
   *
   * @param actor El usuario que realiza la acción (ej. el admin logueado).
   * @param rolAAsignar El rol que se intenta asignar al usuario objetivo.
   * @return true si el actor tiene permiso para asignar ese rol, false en caso contrario.
   */
  public boolean puedeAsignarRol(Usuario actor, Rol rolAAsignar) {
    // Verificación básica de seguridad.
    if (actor == null || rolAAsignar == null) {
      return false;
    }

    // Un ADMIN puede asignar roles de MODERADOR y USUARIO.
    // Por seguridad, no permitimos que se asigne el rol de ADMIN desde el panel.
    if (actor.getRol() == Rol.ADMIN) {
      return rolAAsignar == Rol.MODERADOR || rolAAsignar == Rol.USUARIO;
    }

    // Un MODERADOR solo puede asignar (o mantener) el rol de USUARIO.
    // Por seguridad, no permitimos que se asigne el rol de MODERADOR desde el panel.
    if (actor.getRol() == Rol.MODERADOR) {
      return rolAAsignar == Rol.USUARIO;
    }

    // Si no es admin ni moderador, no puede asignar ningún rol.
    return false;
  }

  /*
   * Comprueba si un usuario (actor) tiene permiso para gestionar las tareas de otro (objetivo).
   */
  public boolean puedeGestionarTareasDe(Usuario actor, Usuario objetivo) {
    if (actor == null || objetivo == null) {
      return false;
    }

    // Regla 1: Un ADMIN puede gestionar las tareas de cualquiera, excepto las de otro ADMIN.
    if (actor.getRol() == Rol.ADMIN) {
      return objetivo.getRol() != Rol.ADMIN;
    }

    // Regla 2: Un MODERADOR puede gestionar las tareas de un USUARIO o las suyas propias.
    if (actor.getRol() == Rol.MODERADOR) {
      boolean esElMismoUsuario =
          actor.getCorreoElectronico().equals(objetivo.getCorreoElectronico());
      return objetivo.getRol() == Rol.USUARIO || esElMismoUsuario;
    }

    // Un USUARIO no puede gestionar tareas de nadie más desde el panel de admin.
    return false;
  }
}
