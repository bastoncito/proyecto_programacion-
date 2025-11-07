package michaelsoftbinbows.dto;

import michaelsoftbinbows.model.Rol;

/**
 * Objeto de transferencia de datos para Usuario.
 *
 * <p>Usado principalmente para recibir datos desde Postman para la API REST.
 */
public class UsuarioDto {
  public String nombreUsuario;
  public String correoElectronico;
  public String contrasena;
  public Rol rol;
}
