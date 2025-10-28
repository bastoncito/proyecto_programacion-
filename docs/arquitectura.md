## Estructura del proyecto
### Dentro de la carpeta src/main/java/Michaelsoft_Binbows...

```text
├─ controller/ --> Llamadas a la capa de servicios (validaciones de entradas/solicitudes), endpoints
│  ├─ TareaController.java 
│  ├─ AutorizacionController.java
│  ├─ HomeController.java
│  ├─ AdminController.java
│  ├─ HomeController.java
│  ├─ PerfilController.java
│  ├─ DevController.java
├─ dto/ --> objetos utilizados para transferir datos (especialmente desde solicitudes POST/PUT)
│  ├─ UsuarioDTO.java 
│  ├─ TareaDTO.java 
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
│  ├─ PersistenciaJSON.java
│  ├─ JsonMigrator.java
│  ├─ LogroRepository.java
│  ├─ TareaRepository.java
│  ├─ UsuarioRepository.java
│  ├─ JpaRepository.java
├─ services/ --> La lógica de la solución en sí, Services de Spring
│  ├─ GestorLogro.java
│  ├─ Rol.java
│  ├─ Id.java
│  ├─ Email.java
│  ├─ Dificultad.java
│  ├─ NotBlank.java
│  ├─ UsuarioService.java
│  ├─ TareaService.java
│  ├─ LogroService.java
│  ├─ SeguridadService.java
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

