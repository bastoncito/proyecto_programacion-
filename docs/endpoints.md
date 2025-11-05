# ENDPOINTS IMPLEMENTADOS EN API REST
### GET
+ /api/usuarios: todos los usuarios
+ /api/usuarios/{idUsuario}: usuario en específico
+ /api/usuarios/{idUsuario}/tareas: tareas de un usuario
+ /api/usuarios/{idUsuario}/tareas/{idTarea}: tarea específica de un usuario
+ /api/usuarios/{idUsuario}/tareas/completadas: tareas completadas de un usuario
+ /api/tareas: todas las tareas
### POST
+ /api/usuarios: añade usuario nuevo
+ /api/usuarios/{idUsuario}/tareas: añade nueva tarea a un usuario
### PUT
+ /api/usuarios/{idUsuario}: actualiza a un usuario
+ /api/usuarios/{idUsuario}/tareas/{idTarea}/completar: marca una tarea como completada
+ /api/usuarios/{idUsuario}/tareas/{idTarea}/actualizar: actualiza una tarea
### DELETE
+ /api/usuarios/{idUsuario}: borra un usuario
+ /api/usuarios/{idUsuario}/tareas/{idTarea}: borra una tarea

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