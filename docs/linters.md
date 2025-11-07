# Linters implementados en el proyecto
## Checkstyle
Utilizado para revisar la sintáxis y formato del proyecto, detectando errores que se salgan de la norma. Estos pueden incluir nombres que no se adhieran al estándar (clases que no empiecen con mayúsucla, paquetes que tengan más que minúsculas y letras...), comentarios javadoc, etc.<br>
Se implementa la convención de código de Google Java Style, cuyas reglas se pueden encontrar en el siguiente link: https://google.github.io/styleguide/javaguide.html<br>
Los registros generados por este se pueden encontrar bajo la carpeta build/reports/checkstyle, generada al construir el proyecto de Gradle.
## ErrorProne
Detecta posibles errores a la hora de compilar, además de dar varios avisos para atributos/funciones no usadas, excepciones ignoradas, inconsistencias en lectura de datos, etc.<br>
Los registros generados por este se pueden encontrar bajo la carpeta build/reports/problems, generada al construir el proyecto de Gradle.
## (Adicional) Spotless
No es un linter en sí ya que no hace un análisis del código, sino que formatea el código. Esto incluye cambios como eliminar espacios/saltos de línea adicionales, indentar correctamente llaves y bloques de código, y establecer un orden para los imports de cada clase.