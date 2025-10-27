## Estructura del proyecto
### Dentro de la carpeta src/main/java/Michaelsoft_Binbows...

```text
├─ controller/ --> Llamadas a la capa de servicios (validaciones de entradas/solicitudes) + endpoints
│  ├─ TareaController.java 
│  ├─ AutorizacionController.java
│  ├─ HomeController.java
│  ├─ AdminController.java
│  ├─ HomeController.java
│  ├─ PerfilController.java
│  ├─ DevController.java
├─ data/ --> Persistencia de datos y repositorios
│  ├─ PersistenciaJSON.java
│  ├─ JsonMigrator.java
│  ├─ LogroRepository.java
│  ├─ TareaRepository.java
│  ├─ UsuarioRepository.java
│  ├─ JpaRepository.java
├─ services/ --> La lógica de la solución en sí, Services de Spring
│  ├─ BaseDatos.java --> pronto a eliminar
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
├─ entities/ --> las clases que se guardan en la base de datos
│  ├─ Usuario.java
│  ├─ Tarea.java
│  ├─ Logro.java

