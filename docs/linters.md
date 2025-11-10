## Linters implementados en el proyecto

### Checkstyle

Se implementa como dependencia de Gradle, utilizando la convención de código de Google Java Style (ligeramente modificada), cuyas reglas pueden ser encontradas en el siguiente link: https://google.github.io/styleguide/javaguide.html<br>
Utilizado para revisar la sintáxis y formato del proyecto, detectando errores que se salgan de la norma. Estos pueden incluir nombres que no se adhieran al estándar (clases que no empiecen con mayúsucla, paquetes que tengan más que minúsculas y letras...), comentarios javadoc faltantes/mal escritos, indentación, etc.<br>
Los registros generados por este se pueden encontrar bajo la carpeta build/reports/checkstyle, generada al construir el proyecto de Gradle.

### ErrorProne

Se implementa como dependencia de Gradle<br>
Detecta posibles errores a la hora de compilar, además de dar varios avisos para atributos/funciones no usadas, excepciones ignoradas, inconsistencias en lectura de datos, etc.<br>
Los registros generados por este se pueden encontrar bajo la carpeta build/reports/problems, generada al construir el proyecto de Gradle.

## Herramientas de formato

## Spotless

Se implementa como dependencia de Gradle, utilizando Google Java Format 1.17.0<br>
No hace un análisis del código, solo lo formatea. Esto incluye cambios como eliminar espacios/saltos de línea adicionales, indentar correctamente llaves y bloques de código, y establecer un orden para los imports de cada clase.<br>
Debido a que Prettier no es una dependencia de Spring ni de Gradle, no podemos confgurar Spotless para que lo use y formatee los HTML/CSS, pero sí lo podemos utilizar para archivos Java.

## Prettier

Implementado para solucionar el problema antes descrito.<br>
A diferencia de los anteriores, este es instalado como extensión de Visual Studio Code, y lo utilizamos principalmente para el formato de los templates HTML y hojas de estilo CSS (lo que no podemos cubrir con Spotless, básicamente).
