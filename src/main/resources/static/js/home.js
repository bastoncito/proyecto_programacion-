// Contiene la lógica de loader, modales, formularios y toasts del home

document.addEventListener('DOMContentLoaded', function () {
  // --- LÓGICA MODAL PARA VER/ELIMINAR TAREAS ---
  const taskModal = document.getElementById('taskModal');
  const openTaskButtons = document.querySelectorAll('.open-modal');
  const modalTitle = document.getElementById('modalTitle');
  const modalDescription = document.getElementById('modalDescription');
  const modalExp = document.getElementById('modalExp');
  const modalExpire = document.getElementById('modalExpire');
  const closeModalBtn = document.getElementById('closeModal');

  const deleteButton = document.getElementById('deleteButton');
  const deleteTaskId = document.getElementById('deleteTaskId');
  const deleteForm = document.getElementById('deleteForm');

  const completeButton = document.getElementById('completeButton');
  const completeTaskId = document.getElementById('completeTaskId');
  const completeForm = document.getElementById('completeForm');

  if (deleteButton) {
    deleteButton.addEventListener('click', () => {
      if (modalTitle && deleteTaskId && deleteForm) {
        deleteTaskId.value = modalTitle.textContent;
        deleteForm.submit();
      }
    });
  }

  if (completeButton) {
    completeButton.addEventListener('click', () => {
      if (modalTitle && completeTaskId && completeForm) {
        completeTaskId.value = modalTitle.textContent;
        completeForm.submit();
      }
    });
  }

  openTaskButtons.forEach((btn) => {
    btn.addEventListener('click', () => {
      if (taskModal) {
        modalTitle.textContent = btn.dataset.nombre;
        modalDescription.textContent = btn.dataset.descripcion;
        modalExp.textContent = btn.dataset.experiencia + ' XP';
        modalExpire.textContent = btn.dataset.expira;
        taskModal.style.display = 'flex';
      }
    });
  });

  if (closeModalBtn) closeModalBtn.addEventListener('click', () => { if (taskModal) taskModal.style.display = 'none'; });

  if (taskModal) {
    taskModal.addEventListener('click', (event) => {
      if (event.target === taskModal) {
        taskModal.style.display = 'none';
      }
    });
  }

  // --- LÓGICA PARA EL MODAL DE CREAR TAREA ---
  const createTaskModal = document.getElementById('createTaskModal');
  const openCreateTaskModalBtn = document.getElementById('openCreateTaskModal');
  const closeCreateTaskModalBtn = document.getElementById('closeCreateTaskModal');
  const cancelCreateTaskBtn = document.getElementById('cancelCreateTask');

  if (openCreateTaskModalBtn) {
    openCreateTaskModalBtn.addEventListener('click', (e) => {
      e.preventDefault();
      if (createTaskModal) {
        createTaskModal.style.display = 'flex';
      }
    });
  }

  const closeCreateModal = () => { if (createTaskModal) createTaskModal.style.display = 'none'; };

  if (closeCreateTaskModalBtn) closeCreateTaskModalBtn.addEventListener('click', closeCreateModal);
  if (cancelCreateTaskBtn) cancelCreateTaskBtn.addEventListener('click', (e) => { e.preventDefault(); closeCreateModal(); });

  if (createTaskModal) {
    createTaskModal.addEventListener('click', (event) => {
      if (event.target === createTaskModal) {
        closeCreateModal();
      }
    });
  }

  // Lógica para el formulario de creación
  const dificultadSelect = document.getElementById('categoria');
  const xpSpan = document.getElementById('xp');
  const plazoSpan = document.getElementById('plazo');
  const datosDificultad = {
    'Muy fácil': { xp: 10, texto: '1 día' }, 'Fácil': { xp: 25, texto: '2 días' },
    'Medio': { xp: 50, texto: '3 días' }, 'Difícil': { xp: 100, texto: '4 días' },
    'Muy difícil': { xp: 150, texto: '5 días' },
  };

  function actualizarResultados() {
    if (dificultadSelect) {
      const seleccion = dificultadSelect.value;
      const datos = datosDificultad[seleccion];
      if (datos) {
        xpSpan.textContent = datos.xp + ' XP';
        plazoSpan.textContent = datos.texto;
      } else {
        xpSpan.textContent = '—';
        plazoSpan.textContent = '—';
      }
    }
  }

  if (dificultadSelect) {
    dificultadSelect.addEventListener('change', actualizarResultados);
    actualizarResultados();
  }

  // --- LÓGICA PARA CONTADORES DE CARACTERES CON LÍMITE Y ESTILO ---
  function setupCharCounter(inputId, spanId, maxLength) {
    const input = document.getElementById(inputId);
    const span = document.getElementById(spanId);

    if (input && span) {
      const parentCounter = span.parentElement;

      const updateCounter = () => {
        const currentLength = input.value.length;
        span.textContent = currentLength;
        if (currentLength > maxLength) {
          parentCounter.classList.add('error-limit');
        } else {
          parentCounter.classList.remove('error-limit');
        }
      };
      input.addEventListener('input', updateCounter);
      updateCounter();
    }
  }
  setupCharCounter('nombre', 'nombre-chars', 50);
  setupCharCounter('descripcion', 'descripcion-chars', 100);

// --- LÓGICA PARA ABRIR MODAL CON ERROR Y MOSTRAR TOAST ---
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get('showCreateTaskModal') === 'true' && createTaskModal) {
    createTaskModal.style.display = 'flex';
  }

  // Selecciona todos los toasts que tengan la clase 'global-toast'
  const globalToasts = document.querySelectorAll('.global-toast');

  // Función reutilizable para manejar la desaparición de un toast
  const handleToast = (toastElement) => {
    if (toastElement) {
      // La lógica para desaparecer después de 3 segundos
      setTimeout(() => {
        toastElement.style.transition = 'opacity 0.5s ease';
        toastElement.style.opacity = '0';

        // Esperamos a que la transición termine para poner display: none
        setTimeout(() => {
          toastElement.style.display = 'none';
        }, 500); // Este tiempo debe coincidir con la duración de la transición CSS
      }, 3000);
    }
  };

  // Aplica la lógica a cada toast encontrado en la página
  globalToasts.forEach(handleToast);

});
