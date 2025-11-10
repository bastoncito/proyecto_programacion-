## Sistema de niveles y experiencia

El funcionamiento del sistema de niveles se basa en el de Minecraft, cuyo comportamiento se describe así:

- 0-16: XP necesaria = 2\*nivel + 7
- 17-31: XP necesaria = 5\*nivel - 38
- 32+: XP necesaria = 9\*nivel - 158

La idea es que los primeros niveles se completen rápidamente, pero que se vuelvan más y más costosos a medida que se avanza.<br>
Simplificamos un poco este sistema, quedándonos con el siguiente:

- 0-15: 40\*siguienteNivel + 50
- 16-30: 80\*siguienteNivel - 200
- 31+: 100\*siguienteNivel - 500

Notar que aquí utilizamos el nivel siguiente en vez del actual.<br>
El método que comprueba la experiencia faltante para el siguiente nivel se puede encontrar en el archivo "SistemaNiveles.java", bajo la carpeta "util".
