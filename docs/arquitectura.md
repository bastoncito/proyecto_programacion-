## Estructura del proyecto
### Dentro de la carpeta src/main/java/michaelsoftbinbows...

```text
├─ controller/ --> Llamadas a la capa de servicios (validaciones de entradas/solicitudes), endpoints
│  ├─ TareaController.java 
│  ├─ AutorizacionController.java
│  ├─ HomeController.java
│  ├─ AdminController.java
│  ├─ PerfilController.java
│  ├─ DevController.java
│  ├─ WeatherController.java
│  ├─ GeocodingController.java
│  ├─ APIController.java
│  ├─ LogroController.java
├─ dto/ --> objetos utilizados para transferir datos (especialmente desde solicitudes POST/PUT)
│  ├─ UsuarioDto.java 
│  ├─ TareaDto.java 
├─ model/ --> enums, interfaces
│  ├─ Email.java 
│  ├─ Id.java 
│  ├─ NotBlank.java 
│  ├─ Rol.java 
├─ util/ --> clases/métodos de utilidad
│  ├─ Dificultad.java 
│  ├─ GestorLogros.java 
│  ├─ SistemaNiveles.java 
├─ data/ --> Persistencia de datos, repositorios, interacción con base de datos
│  ├─ JsonMigrator.java
│  ├─ LogroRepository.java
│  ├─ TareaRepository.java
│  ├─ UsuarioRepository.java
│  ├─ JpaRepository.java
│  ├─ ConfiguracionRepository.java
├─ services/ --> La lógica de la solución en sí, Services de Spring
│  ├─ UsuarioService.java
│  ├─ ConfiguracionService.java
│  ├─ TareaService.java
│  ├─ LogroService.java
│  ├─ TemporadaService.java
│  ├─ SeguridadService.java
│  ├─ UsuarioTareaService.java
│  ├─ WeatherService.java
│  ├─ GeocodingService.java
│  ├─ CustomUserDetailsService.java
├─ exceptions/ --> excepciones/errores personalizados
│  ├─ (...)
├─ security/ --> flujo de datos + manejo de excepciones
│  ├─ CustomUserDetails.java
│  ├─ SecurityConfig.java
│  ├─ GlobalExceptionHandler.java
├─ entities/ --> lo que se guarda en la base de datos
│  ├─ Usuario.java
│  ├─ Tarea.java
│  ├─ Logro.java

