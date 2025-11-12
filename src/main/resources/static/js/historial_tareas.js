// Contiene la función que muestra el resumen semanal/mensual
// La función se expone en window para que siga siendo invocable desde
// atributos onclick en las plantillas (por ejemplo: onclick="mostrarResumen('semanal')").

// Habilita o deshabilita logs de depuración para esta función.
const HISTORIAL_LOGS = false;

window.mostrarResumen = function (vista) {
  if (HISTORIAL_LOGS) console.log('[historial] mostrarResumen llamado:', vista);

  const semanal = document.getElementById('resumen-semanal');
  const mensual = document.getElementById('resumen-mensual');
  const tabSemanal = document.getElementById('tab-semanal');
  const tabMensual = document.getElementById('tab-mensual');

  if (!semanal) console.warn('[historial] elemento #resumen-semanal no encontrado');
  if (!mensual) console.warn('[historial] elemento #resumen-mensual no encontrado');
  if (!tabSemanal) console.warn('[historial] elemento #tab-semanal no encontrado');
  if (!tabMensual) console.warn('[historial] elemento #tab-mensual no encontrado');

  if (semanal) semanal.style.display = 'none';
  if (mensual) mensual.style.display = 'none';
  if (tabSemanal) tabSemanal.classList.remove('active');
  if (tabMensual) tabMensual.classList.remove('active');

  if (vista === 'semanal') {
    if (semanal) semanal.style.display = 'block';
    if (tabSemanal) tabSemanal.classList.add('active');
    if (HISTORIAL_LOGS) console.log('[historial] mostrando vista: semanal');
  } else if (vista === 'mensual') {
    if (mensual) mensual.style.display = 'block';
    if (tabMensual) tabMensual.classList.add('active');
    if (HISTORIAL_LOGS) console.log('[historial] mostrando vista: mensual');
  } else {
    if (HISTORIAL_LOGS) console.warn('[historial] vista desconocida:', vista);
  }
};