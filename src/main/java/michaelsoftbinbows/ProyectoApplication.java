package michaelsoftbinbows;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada para la aplicación en sí
 *
 * <p>Se cargan los parámetros para el funcionamiento de la base de datos desde un archivo .env.
 */
@SpringBootApplication
public class ProyectoApplication {
  /**
   * Este método pone en marcha la aplicación.
   *
   * @param args argumentos recibidos desde compilador
   */
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().filename(".env.application").load();
    System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
    System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
    System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
    System.setProperty("DB_USER", dotenv.get("DB_USER"));
    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    System.setProperty("OWM_API_KEY", dotenv.get("OWM_API_KEY"));

    SpringApplication.run(ProyectoApplication.class, args);
  }
}
