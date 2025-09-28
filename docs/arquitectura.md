## Estructura del proyecto

```text
proyecto_programacion-/
├─ readme.md
├─ base_datos.json
├─ lib/
│  ├─ gson-2.13.1.jar
├─ docs/
│  ├─ (documentación proyecto)
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ Michaelsoft_Binbows/
│  │  │  │  ├─ controller/ --> Llamadas a la capa de servicios (validaciones de entradas/solicitudes) + endpoints
│  │  │  │  │  ├─ TareaController.java 
│  │  │  │  │  ├─ AutorizacionController.java
│  │  │  │  │  ├─ HomeController.java
│  │  │  │  │  ├─ AdminController.java
│  │  │  │  ├─ data/ --> Almacenamiento de información y persistencia de datos
│  │  │  │  │  ├─ PersistenciaJSON.java
│  │  │  │  ├─ services/ --> La lógica de la solución en sí
│  │  │  │  │  ├─ BaseDatos.java
│  │  │  │  │  ├─ Logro.java
│  │  │  │  │  ├─ GestorLogro.java
│  │  │  │  │  ├─ Usuario.java
│  │  │  │  │  ├─ Tarea.java
│  │  │  │  │  ├─ SistemaNiveles.java
│  │  │  │  ├─ ProyectoApplication.java
│  │  ├─ resources/ --> Recursos para visualización de cada página
│  │  │  ├─ static/
│  │  │  │  ├─ (.css + imágenes)
│  │  │  ├─ templates/
│  │  │  │  ├─ (.html)

