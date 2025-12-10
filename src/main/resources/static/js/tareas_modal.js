// Manejo del modal de tareas: apertura, cierre y acciones relacionadas
document.addEventListener('DOMContentLoaded', function () {
  const modal = document.getElementById('taskModal');
  const modalTitle = document.getElementById('modalTitle');
  const modalDescription = document.getElementById('modalDescription');
  const modalExp = document.getElementById('modalExp');
  const modalExpire = document.getElementById('modalExpire');
  const modalMitadVida = document.getElementById('modalMitadVida');
  const completionStatusInfo = document.getElementById('completionStatusInfo');

  if (!modal) return; // nada que hacer si no existe el modal en la página

  const openButtons = document.querySelectorAll('.view-btn');
  openButtons.forEach((btn) => {
    btn.addEventListener('click', () => {
      if (modalTitle) modalTitle.textContent = btn.dataset.nombre || '';
      if (modalDescription) modalDescription.textContent = btn.dataset.descripcion || '';
      if (modalExp) modalExp.textContent = (btn.dataset.experiencia || '') + (btn.dataset.experiencia ? ' XP' : '');
      if (modalExpire) modalExpire.textContent = btn.dataset.expira || '';
      
      // Display lifetime information
      if (modalMitadVida) {
        modalMitadVida.textContent = btn.dataset.mitadVida || '';
        
        // Calculate if task can be completed now
        const now = new Date();
        const mitadVidaDate = new Date(btn.dataset.mitadVida.replace(' ', 'T'));
        
        if (completionStatusInfo) {
          if (now >= mitadVidaDate) {
            completionStatusInfo.innerHTML = '<span style="color: #4CAF50;">✅ Puedes completar esta tarea ahora</span>';
          } else {
            const timeRemaining = Math.ceil((mitadVidaDate - now) / (1000 * 60));
            completionStatusInfo.innerHTML = '<span style="color: #FF9800;">⏳ Falta ' + timeRemaining + ' minuto(s) para poder completar</span>';
          }
        }
      }
      
      modal.style.display = 'flex';
    });
  });

  const closeModal = document.getElementById('closeModal');
  if (closeModal) {
    closeModal.addEventListener('click', () => {
      modal.style.display = 'none';
    });
  }

  // Cerrar modal al hacer clic fuera del contenido
  modal.addEventListener('click', function (e) {
    if (e.target === modal) modal.style.display = 'none';
  });

  // Soporte opcional para botones de eliminar/completar si existen en la plantilla
  const deleteButton = document.getElementById('deleteButton');
  const deleteForm = document.getElementById('deleteForm');
  const deleteTaskId = document.getElementById('deleteTaskId');
  if (deleteButton && deleteForm && deleteTaskId) {
    deleteButton.addEventListener('click', () => {
      if (modalTitle) deleteTaskId.value = modalTitle.textContent;
      deleteForm.submit();
    });
  }

  const completeButton = document.getElementById('completeButton');
  const completeForm = document.getElementById('completeForm');
  const completeTaskId = document.getElementById('completeTaskId');
  if (completeButton && completeForm && completeTaskId) {
    completeButton.addEventListener('click', () => {
      if (modalTitle) completeTaskId.value = modalTitle.textContent;
      completeForm.submit();
    });
  }
});
