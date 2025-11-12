// Lógica compartida para calcular XP y plazo según la dificultad
document.addEventListener("DOMContentLoaded", function () {
  const dificultadSelect = document.getElementById("categoria");
  const xpSpan = document.getElementById("xp");
  const plazoSpan = document.getElementById("plazo");

  // Si alguno de los elementos no existe en esta página, salimos sin errores.
  if (!dificultadSelect || !xpSpan || !plazoSpan) return;

  const datosDificultad = {
    "Muy fácil": { xp: 10, texto: "1 día" },
    "Fácil": { xp: 25, texto: "2 días" },
    "Medio": { xp: 50, texto: "3 días" },
    "Difícil": { xp: 100, texto: "4 días" },
    "Muy difícil": { xp: 150, texto: "5 días" },
  };

  function actualizarResultados() {
    const seleccion = dificultadSelect.value;
    const datos = datosDificultad[seleccion];

    if (datos) {
      xpSpan.textContent = datos.xp + " XP";
      plazoSpan.textContent = datos.texto;
    } else {
      xpSpan.textContent = "—";
      plazoSpan.textContent = "—";
    }
  }

  dificultadSelect.addEventListener("change", actualizarResultados);

  // Ejecutar una vez al cargar para inicializar la UI
  actualizarResultados();
});
