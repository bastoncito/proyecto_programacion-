document.addEventListener('DOMContentLoaded', function() {

  const LOADER_MINIMUM_DURATION = 500;
  const htmlElement = document.documentElement;

  // Función para INICIAR el estado de carga
  function startLoading() {
    // 1. Dejamos la señal para la próxima página
    sessionStorage.setItem('isLoading', 'true');
    // 2. Aplicamos el estado de carga a la página ACTUAL para feedback instantáneo
    htmlElement.classList.add('is-loading');
  }

  // Función para FINALIZAR el estado de carga
  function stopLoading() {
    htmlElement.classList.remove('is-loading');
  }

  // Listener para los clics de navegación principal
  function handleNavigationClick(e) {
    const link = e.target.closest('a');
    if (!link || link.dataset.noLoader !== undefined) {
      return;
    }
    const href = link.getAttribute('href');
    if (href && !href.startsWith('#') && !href.startsWith('javascript:')) {
      startLoading();
    }
  }

  // --- ASIGNACIÓN DE EVENTOS ---
  const bottomNav = document.querySelector('.bottom-nav');
  if (bottomNav) {
    bottomNav.addEventListener('click', handleNavigationClick);
  }

  const adminSideBar = document.querySelector('.container-task-bar');
  if (adminSideBar) {
    adminSideBar.addEventListener('click', handleNavigationClick);
  }
  
  const loginForm = document.querySelector('form[action="/login"]');
  if(loginForm) {
      loginForm.addEventListener('submit', startLoading);
  }


  // --- LÓGICA PARA DETENER LA CARGA ---
  // Esperamos a que la página se cargue por completo Y que pase un tiempo mínimo
  const loadPromise = new Promise(resolve => {
    window.addEventListener('load', resolve);
  });

  const minDelayPromise = new Promise(resolve => {
    setTimeout(resolve, LOADER_MINIMUM_DURATION);
  });

  // Cuando ambas promesas se cumplan, quitamos el estado de carga
  Promise.all([loadPromise, minDelayPromise]).then(() => {
    stopLoading();
  });

});