package michaelsoftbinbows.util;

import java.util.regex.Pattern;

/**
 * Servicio responsable únicamente de validaciones relacionadas con Usuario. Extrae las reglas de
 * validación para nombre, correo y contraseña.
 */
public class UsuarioValidator {

  /**
   * Valida el formato de un correo electrónico.
   *
   * @param correo El correo a validar.
   * @return true si el correo es válido, false en caso contrario.
   */
  public boolean correoValido(String correo) {
    if (correo == null || correo.trim().isEmpty()) {
      return false;
    }
    String regex =
        "[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5}";
    return correo.matches(regex);
  }

  /**
   * Valida el nombre de usuario.
   *
   * @param nombreUsuario El nombre de usuario a validar.
   * @return true si el nombre de usuario es válido, false en caso contrario.
   */
  public boolean nombreUsuarioValido(String nombreUsuario) {
    if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
      return false;
    }
    if (nombreUsuario == null
        || nombreUsuario.trim().isEmpty()
        || nombreUsuario.length() < 3
        || nombreUsuario.length() > 30) {
      return false;
    }
    return true;
  }

  /**
   * Valida la fortaleza de una contraseña.
   *
   * @return null si la contraseña es válida, o un String con el mensaje de error si no lo es.
   */
  public String validarContrasena(String contrasena) {
    if (contrasena == null || contrasena.trim().isEmpty()) {
      return "La contrasena no puede estar vacía";
    }

    if (contrasena.length() < 8) {
      return "La contrasena debe tener al menos 8 caracteres";
    }

    if (contrasena.contains(" ")) {
      return "La contrasena no puede contener espacios";
    }

    boolean tieneMayuscula = Pattern.compile("[A-Z]").matcher(contrasena).find();
    boolean tieneMinuscula = Pattern.compile("[a-z]").matcher(contrasena).find();
    boolean tieneDigito = Pattern.compile("\\d").matcher(contrasena).find();
    boolean tieneCaracterEspecial =
        Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;:'\",.<>/?]").matcher(contrasena).find();

    if (!tieneMayuscula || !tieneMinuscula || !tieneDigito || !tieneCaracterEspecial) {
      StringBuilder errores = new StringBuilder();
      if (!tieneMayuscula) {
        errores.append("- Debe contener al menos una mayúscula\n");
      }
      if (!tieneMinuscula) {
        errores.append("- Debe contener al menos una minúscula\n");
      }
      if (!tieneDigito) {
        errores.append("- Debe contener al menos un dígito\n");
      }
      if (!tieneCaracterEspecial) {
        errores.append("- Debe contener al menos un carácter especial (!@#$%^&* etc.)\n");
      }
      return "La contrasena es demasiado débil. Requisitos:\n" + errores.toString();
    }
    return null; // Contraseña válida
  }
}
