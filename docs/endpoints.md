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
+ /api/usuarios/{idUsuario}/tareas/{idTarea}/completar: maraca una tarea como completada
+ /api/usuarios/{idUsuario}/tareas/{idTarea}/actualizar: actualiza una tarea
### DELETE
+ /api/usuarios/{idUsuario}: borra un usuario
+ /api/usuarios/{idUsuario}/tareas/{idTarea}: borra una tarea

```text
Para métodos POST y PUT se utilizan objetos DTO para Usuario y Tarea, los cuales se escriben en Postman de la siguiente manera
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