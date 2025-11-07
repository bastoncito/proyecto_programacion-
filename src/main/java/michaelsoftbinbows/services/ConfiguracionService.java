package michaelsoftbinbows.services;

import michaelsoftbinbows.data.ConfiguracionRepository;
import michaelsoftbinbows.entities.Configuracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar la configuración global de la aplicación. Maneja parámetros como el
 * límite de la liga.
 */
@Service
public class ConfiguracionService {

  @Autowired private ConfiguracionRepository configuracionRepository;

  private final String limiteTopKey = "top_limite";

  /** Obtiene el límite guardado en la BD. Si no existe, devuelve 10 por defecto. */
  public int getLimiteTop() {
    Configuracion config =
        configuracionRepository
            .findById(limiteTopKey)
            .orElse(new Configuracion(limiteTopKey, "10"));
    return Integer.parseInt(config.getValor());
  }

  /** Guarda el nuevo límite en la BD. */
  public void setLimiteTop(int limite) {
    Configuracion config = new Configuracion(limiteTopKey, String.valueOf(limite));
    configuracionRepository.save(config);
  }

  /**
   * Obtiene un límite de liga (ej. "LIGA_PLATA") de la BD. Si no existe, devuelve el valor por
   * defecto que le pasemos.
   */
  public int getLimiteLiga(String clave, int defaultValue) {
    Configuracion config =
        configuracionRepository
            .findById(clave)
            .orElse(new Configuracion(clave, String.valueOf(defaultValue)));

    try {
      // Intenta convertir el valor guardado a número
      return Integer.parseInt(config.getValor());
    } catch (NumberFormatException e) {
      // Si alguien guarda "abc" en la BD, devolvemos el valor por defecto
      return defaultValue;
    }
  }

  /** Guarda un nuevo límite de liga (ej. "LIGA_PLATA" = 500) en la BD. */
  public void setLimiteLiga(String clave, int limite) {
    Configuracion config = new Configuracion(clave, String.valueOf(limite));
    configuracionRepository.save(config);
  }
}
