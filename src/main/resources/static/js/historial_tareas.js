// Contiene la función que muestra el resumen semanal/mensual
// La función se expone en window para que siga siendo invocable desde
// atributos onclick en las plantillas (por ejemplo: onclick="mostrarResumen('semanal')").

// Habilita o deshabilita logs de depuración para esta función.
const HISTORIAL_LOGS = false;

// Variables globales para los gráficos
let chartSemanal = null;
let chartMensual = null;

// Inicializa los gráficos cuando el DOM está listo
document.addEventListener("DOMContentLoaded", function () {
  inicializarGraficos();
});

/**
 * Inicializa ambos gráficos (semanal y mensual) obteniendo datos del servidor
 */
async function inicializarGraficos() {
  if (HISTORIAL_LOGS) console.log("[historial] Inicializando gráficos...");

  try {
    // Obtener datos semanales
    const responseWeekly = await fetch("/historial/api/semanal");
    const datosSemanales = await responseWeekly.json();

    // Obtener datos mensuales
    const responseMonthly = await fetch("/historial/api/mensual");
    const datosMensuales = await responseMonthly.json();

    // Crear los gráficos
    crearGraficoSemanal(datosSemanales);
    crearGraficoMensual(datosMensuales);

    if (HISTORIAL_LOGS)
      console.log("[historial] Gráficos inicializados correctamente");
  } catch (error) {
    console.error("[historial] Error al inicializar gráficos:", error);
  }
}

/**
 * Crea el gráfico semanal con los datos obtenidos del servidor
 */
function crearGraficoSemanal(datos) {
  const ctx = document.getElementById("chartSemanal");
  if (!ctx) {
    console.warn("[historial] Canvas #chartSemanal no encontrado");
    return;
  }

  // Destruir gráfico anterior si existe
  if (chartSemanal) {
    chartSemanal.destroy();
  }

  // Actualizar el total semanal
  document.getElementById("totalSemanal").textContent = datos.total;

  chartSemanal = new Chart(ctx, {
    type: "bar",
    data: {
      labels: datos.dias,
      datasets: [
        {
          label: "Tareas Completadas",
          data: datos.tareas,
          backgroundColor: "#ffc839",
          borderColor: "#e6b332",
          borderWidth: 2,
          borderRadius: 8,
          hoverBackgroundColor: "#e6b332",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            stepSize: 1,
            font: {
              family: "'Poppins', sans-serif",
              size: 11,
            },
          },
          grid: {
            color: "#eef2f5",
          },
        },
        x: {
          ticks: {
            font: {
              family: "'Poppins', sans-serif",
              size: 11,
            },
          },
          grid: {
            display: false,
          },
        },
      },
    },
  });
}

/**
 * Crea el gráfico mensual con los datos obtenidos del servidor
 */
function crearGraficoMensual(datos) {
  const ctx = document.getElementById("chartMensual");
  if (!ctx) {
    console.warn("[historial] Canvas #chartMensual no encontrado");
    return;
  }

  // Destruir gráfico anterior si existe
  if (chartMensual) {
    chartMensual.destroy();
  }

  // Actualizar el total mensual
  document.getElementById("totalMensual").textContent = datos.total;

  chartMensual = new Chart(ctx, {
    type: "line",
    data: {
      labels: datos.etiquetas,
      datasets: [
        {
          label: "Tareas Completadas",
          data: datos.tareas,
          borderColor: "#ffc839",
          backgroundColor: "rgba(255, 200, 57, 0.1)",
          borderWidth: 3,
          fill: true,
          tension: 0.3,
          pointBackgroundColor: "#ffc839",
          pointBorderColor: "#fff",
          pointBorderWidth: 2,
          pointRadius: 4,
          pointHoverRadius: 6,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            stepSize: 1,
            font: {
              family: "'Poppins', sans-serif",
              size: 11,
            },
          },
          grid: {
            color: "#eef2f5",
          },
        },
        x: {
          ticks: {
            font: {
              family: "'Poppins', sans-serif",
              size: 9,
            },
            maxRotation: 45,
            minRotation: 0,
          },
          grid: {
            display: false,
          },
        },
      },
    },
  });
}

window.mostrarResumen = function (vista) {
  if (HISTORIAL_LOGS) console.log("[historial] mostrarResumen llamado:", vista);

  const semanal = document.getElementById("resumen-semanal");
  const mensual = document.getElementById("resumen-mensual");
  const tabSemanal = document.getElementById("tab-semanal");
  const tabMensual = document.getElementById("tab-mensual");

  if (!semanal)
    console.warn("[historial] elemento #resumen-semanal no encontrado");
  if (!mensual)
    console.warn("[historial] elemento #resumen-mensual no encontrado");
  if (!tabSemanal)
    console.warn("[historial] elemento #tab-semanal no encontrado");
  if (!tabMensual)
    console.warn("[historial] elemento #tab-mensual no encontrado");

  if (semanal) semanal.style.display = "none";
  if (mensual) mensual.style.display = "none";
  if (tabSemanal) tabSemanal.classList.remove("active");
  if (tabMensual) tabMensual.classList.remove("active");

  if (vista === "semanal") {
    if (semanal) semanal.style.display = "block";
    if (tabSemanal) tabSemanal.classList.add("active");
    if (HISTORIAL_LOGS) console.log("[historial] mostrando vista: semanal");
    // Redibuja el gráfico semanal para asegurar que se vea correctamente
    if (chartSemanal) setTimeout(() => chartSemanal.resize(), 100);
  } else if (vista === "mensual") {
    if (mensual) mensual.style.display = "block";
    if (tabMensual) tabMensual.classList.add("active");
    if (HISTORIAL_LOGS) console.log("[historial] mostrando vista: mensual");
    // Redibuja el gráfico mensual para asegurar que se vea correctamente
    if (chartMensual) setTimeout(() => chartMensual.resize(), 100);
  } else {
    if (HISTORIAL_LOGS) console.warn("[historial] vista desconocida:", vista);
  }
};
