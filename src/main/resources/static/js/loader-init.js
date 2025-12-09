/**
 * Responsabilidad: Se ejecuta en el <head> para prevenir el "flash" de contenido.
 * Si detecta que una navegación está en curso, añade la clase 'is-loading' al
 * elemento <html> antes de que la página sea visible para el usuario.
 */
(function() {
  if (sessionStorage.getItem('isLoading') === 'true') {
    document.documentElement.classList.add('is-loading');
    
    // Es crucial limpiar la bandera aquí.
    // Esto asegura que si el usuario refresca la página (F5),
    // el loader no se quede pegado.
    sessionStorage.removeItem('isLoading');
  }
})();