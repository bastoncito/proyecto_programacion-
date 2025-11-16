document.addEventListener('DOMContentLoaded', function() {
  
  // Verificamos si la variable global con los datos existe
  if (window.logrosChartData) {
    
    const datosGrafico = window.logrosChartData;
    const canvas = document.getElementById('graficoLogrosCompletados');

    // Verificamos si hay datos y si el canvas existe
    if (canvas && datosGrafico && datosGrafico.length > 0) {
      
      const etiquetas = datosGrafico.map(logro => logro.nombre);
      const conteos = datosGrafico.map(logro => logro.conteo);

      // Creamos el gráfico (mismo código de Chart.js)
      const ctx = canvas.getContext('2d');
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: etiquetas,
          datasets: [{
            label: 'Nº de Jugadores',
            data: conteos,
            backgroundColor: 'rgba(255, 200, 57, 0.6)', 
            borderColor: 'rgba(255, 200, 57, 1)',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              ticks: { precision: 0 }
            }
          },
          plugins: {
            legend: { display: false },
            tooltip: {
              callbacks: {
                label: function(context) {
                  return ` ${context.raw} jugadores`;
                }
              }
            }
          }
        }
      });
    } else if (canvas) {
      // Mensaje si no hay datos
      const context = canvas.getContext('2d');
      context.font = '14px Poppins';
      context.fillStyle = '#868e96';
      context.textAlign = 'center';
      context.fillText('No hay datos de logros para mostrar.', canvas.width / 2, canvas.height / 2);
    }
  }
  
  // (Opcional) Limpiamos la variable global después de usarla
  if (window.logrosChartData) {
    delete window.logrosChartData;
  }
});