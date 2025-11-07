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
}
