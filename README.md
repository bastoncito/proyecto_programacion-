# Proyecto de Programación 2025<br>Michaelsoft Binbows<br>"Good Time" - gestor de tareas gamificado
## Proyecto en sí
Esencialmente, esta aplicación permitirá a los usuarios agendar tareas y recibir/aceptar sugerencias de tareas basadas en el clima.<br>  
El punto es que cada tarea tiene un valor de experiencia, el cual varía según el tiempo estimado para completarla, y sirve como recompensa por cumplir con los objetivos que uno se ponga.<br>  
Se busca gamificar el proceso de realizar las tareas que se planteen los usuarios, evitando el farmeo de puntos (mediante límites diarios, tiempo mínimo para completar una tarea, límites por dificultad, etc.), y teniendo los niveles y experiencia como registro de la productividad del usuario.<br>   
Además, los usuarios podrán ganar logros por realizar acciones específicas, y podrán comparar sus estadísticas con otros mediante rankings.<br>
## Herramientas utilizadas (hasta el momento)
Considerar que este proyecto utiliza Gradle para su correcta compilación.
### Lenguaje 
El backend del proyecto está codificado principalmente en Java 21, mientras que el frontend utiliza html+css (con la implementación de javascript cuando se necesario).
### Framework web
Para "levantar" la página en sí se utiliza el framework de Spring, manejando llamadas a rutas mediante controladores de este.
### Base de datos
Para el manejo de los datos de la aplicación, se opta por implementar PostgreSQL. Recordar que, al ser un servicio externo a Gradle, requiere ser configurado aparte del proyecto.
Para llamadas a la base en Postgre, se utilizan las dependencias de Hibernate y JPA.
### API Externa
Se utilizará la API de OpenWeather para recibir la información del clima.
