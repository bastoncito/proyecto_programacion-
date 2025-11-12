// --- LÓGICA COMPLETA DEL LOADER ---

/**
 * Muestra el loader cambiando su estilo a 'flex'.
 * Se activa al hacer clic en un enlace de navegación en la PÁGINA ANTIGUA.
 */
function showLoader() {
  const loader = document.getElementById('loader-wrapper');
  if (loader) {
    loader.style.display = 'flex';
  }
}

/**
 * Oculta el loader con una animación de fade-out.
 * Se activa cuando la PÁGINA NUEVA ha terminado de cargar.
 */
function hideLoader() {
  const loader = document.getElementById('loader-wrapper');
  if (loader) {
    // 1. Añade la clase que inicia la transición de opacidad
    loader.classList.add('fade-out');

    // 2. Después de que la animación termine (500ms), oculta el elemento por completo.
    // Esto es importante para que no bloquee los clics en la página.
    setTimeout(() => {
      loader.style.display = 'none';
    }, 500); // Debe coincidir con la duración de la transición en el CSS
  }
}

// --- EVENT LISTENERS ---

// Listener para MOSTRAR el loader al navegar
document.addEventListener('click', function (e) {
  const a = e.target.closest('a');

  // Si no hay enlace o el enlace tiene 'data-no-loader', no hacemos nada.
  if (!a || a.dataset.noLoader !== undefined) {
    return;
  }

  // Muestra el loader si el enlace tiene 'data-show-loader'
  if (a.dataset.showLoader !== undefined) {
    const href = a.getAttribute('href');
    if (href && !href.startsWith('#') && !href.startsWith('javascript:')) {
      showLoader();
    }
  }
});

// Listener para formularios
document.addEventListener('submit', function (e) {
  const form = e.target;
  if (form && form.dataset.showLoader !== undefined) {
    showLoader();
  }
});

// Listener para OCULTAR el loader cuando la nueva página esté lista.
// Usamos 'load' en lugar de 'DOMContentLoaded' para esperar a que todo
// (incluyendo imágenes) esté cargado, dando una experiencia más fluida.
window.addEventListener('load', hideLoader);