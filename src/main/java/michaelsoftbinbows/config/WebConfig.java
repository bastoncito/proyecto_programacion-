package michaelsoftbinbows.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Mapea la URL "/uploads/**" a la carpeta física "uploads" en la raíz del proyecto
    // "file:uploads/" indica que es una ruta en el sistema de archivos
    registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
  }
}
