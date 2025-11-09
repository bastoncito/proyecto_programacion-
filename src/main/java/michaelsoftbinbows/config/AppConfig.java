package michaelsoftbinbows.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Clase de configuración principal de Spring. Define los Beans que estarán disponibles para la
 * inyección de dependencias.
 */
@Configuration
public class AppConfig {

  /**
   * Esto crea una instancia ÚNICA de RestTemplate (un "Bean") que Spring usará para inyectar en
   * todos los servicios que lo pidan.
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
