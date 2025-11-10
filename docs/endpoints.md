# ENDPOINTS IMPLEMENTADOS EN API REST

### GET (obtener datos existentes)

- /api/usuarios: todos los usuarios
- /api/usuarios/{idUsuario}: usuario en específico
- /api/usuarios/{idUsuario}/tareas: tareas de un usuario
- /api/usuarios/{idUsuario}/tareas/{idTarea}: tarea específica de un usuario
- /api/usuarios/{idUsuario}/tareas/completadas: tareas completadas de un usuario
- /api/tareas: todas las tareas
- /api/logros: todos los logros disponibles
- /api/logros/{id}: un logro en específico

### POST (inserción de datos)

- /api/usuarios: añade a un usuario nuevo
- /api/usuarios/{idUsuario}/tareas: añade una nueva tarea a un usuario
- /api/logros: añade un logro nuevo

### PUT (actualización de datos)

- /api/usuarios/{idUsuario}: actualiza los datos de un usuario
- /api/usuarios/{idUsuario}/tareas/{idTarea}/completar: marca una tarea de un usuario como completada
- /api/usuarios/{idUsuario}/tareas/{idTarea}/actualizar: actualiza los datos de una tarea de un usuario

### DELETE (eliminación de datos)

- /api/usuarios/{idUsuario}: borra un usuario
- /api/usuarios/{idUsuario}/tareas/{idTarea}: borra una tarea específica de un usuario
- /api/logros/{id}: borra un logro en específico 

Para métodos POST y PUT se utilizan objetos DTO para Usuario y Tarea, los cuales se ingresan se escriben en JSON de la siguiente manera:

```text
{
    "nombreUsuario": "",
    "contrasena": "",
    "correoElectronico": ""
}
{
    "nombre": "",
    "descripcion": "",
    "dificultad": ""
}
```
En el caso de Logro, el método POST pide lo siguiente como entrada:
```text
{
    "nombre": "",
    "descripcion": "",
    "experienciaRecompensa": 
}
```