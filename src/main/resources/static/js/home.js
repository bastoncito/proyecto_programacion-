/**
 * home.js
 * Lógica integral del Home: Tareas, Modales y Gestor de Amigos.
 */

document.addEventListener('DOMContentLoaded', function () {
  
  // =========================================================
  // 1. LÓGICA MODAL PARA VER/ELIMINAR TAREAS
  // =========================================================
  const taskModal = document.getElementById('taskModal');
  const openTaskButtons = document.querySelectorAll('.open-modal');
  const modalTitle = document.getElementById('modalTitle');
  const modalDescription = document.getElementById('modalDescription');
  const modalExp = document.getElementById('modalExp');
  const modalExpire = document.getElementById('modalExpire');
  const modalMitadVida = document.getElementById('modalMitadVida');
  const completionStatusInfo = document.getElementById('completionStatusInfo');
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
        
        // Display lifetime information
        const mitadVida = btn.dataset.mitadVida;
        modalMitadVida.textContent = mitadVida;
        
        // Calculate if task can be completed now
        const now = new Date();
        const mitadVidaDate = new Date(btn.dataset.mitadVida.replace(' ', 'T'));
        
        if (now >= mitadVidaDate) {
          completionStatusInfo.innerHTML = '<span style="color: #4CAF50;">✅ Puedes completar esta tarea ahora</span>';
          completeButton.disabled = false;
          completeButton.style.opacity = '1';
          completeButton.style.cursor = 'pointer';
        } else {
          const timeRemaining = Math.ceil((mitadVidaDate - now) / (1000 * 60));
          completionStatusInfo.innerHTML = '<span style="color: #FF9800;">⏳ Falta ' + timeRemaining + ' minuto(s) para poder completar</span>';
          completeButton.disabled = true;
          completeButton.style.opacity = '0.5';
          completeButton.style.cursor = 'not-allowed';
        }
        
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

  // =========================================================
  // 2. LÓGICA PARA EL MODAL DE CREAR TAREA
  // =========================================================
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

  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get('showCreateTaskModal') === 'true' && createTaskModal) {
    createTaskModal.style.display = 'flex';
  }

  const globalToasts = document.querySelectorAll('.global-toast');
  const handleToast = (toastElement) => {
    if (toastElement) {
      setTimeout(() => {
        toastElement.style.transition = 'opacity 0.5s ease';
        toastElement.style.opacity = '0';
        setTimeout(() => {
          toastElement.style.display = 'none';
        }, 500);
      }, 3000);
    }
  };
  globalToasts.forEach(handleToast);


  // =========================================================
  // 3. LÓGICA DEL GESTOR DE AMIGOS (MODAL Y DATOS)
  // =========================================================
  
  const friendsModal = document.getElementById('friendsModal');
  const openFriendsModalBtn = document.getElementById('openFriendsModal');
  const closeFriendsModalBtn = document.getElementById('closeFriendsModal');
  const friendTabs = document.querySelectorAll('.friends-tabs .tab-btn');
  const friendContents = document.querySelectorAll('.friends-tab-content');
  const searchInput = document.querySelector('#buscar .search-input');
  
  const friendsTab = document.getElementById('mis-amigos'); 
  const requestsTab = document.getElementById('solicitudes');
  const searchTab = document.getElementById('buscar');

  // --- FUNCIONES GLOBALES DE CARGA ---
  
  window.cargarMisAmigos = function() {
    fetch('/social/amigos')
      .then(response => {
        if(!response.ok) throw new Error("Error: " + response.status);
        return response.json();
      })
      .then(data => {
        renderizarLista(friendsTab, data, 'AMIGOS');
        const counter = document.getElementById('amigos-counter');
        if(counter) counter.textContent = `(${data.length})`;
      })
      .catch(err => console.error('Error cargando amigos:', err));
  }

  window.cargarSolicitudes = function() {
    fetch('/social/solicitudes')
      .then(response => response.json())
      .then(data => {
        renderizarLista(requestsTab, data, 'SOLICITUDES');
        const badges = document.querySelectorAll('.badge-count');
        badges.forEach(b => {
            b.textContent = data.length;
            b.style.display = data.length > 0 ? 'inline-block' : 'none';
        });
      })
      .catch(err => console.error('Error cargando solicitudes:', err));
  }

  window.buscarUsuarios = function(query) {
    fetch(`/social/buscar?query=${encodeURIComponent(query)}`)
      .then(response => response.json())
      .then(data => {
        renderizarLista(searchTab, data, 'BUSQUEDA');
      })
      .catch(err => console.error('Error buscando usuarios:', err));
  }

  // Carga inicial de badges
  cargarSolicitudes();

  if (openFriendsModalBtn) {
    openFriendsModalBtn.addEventListener('click', (e) => {
      e.preventDefault();
      if (friendsModal) {
        friendsModal.style.display = 'flex';
        cargarMisAmigos();
        cargarSolicitudes();
      }
    });
  }

  if (closeFriendsModalBtn) {
    closeFriendsModalBtn.addEventListener('click', () => {
      if (friendsModal) friendsModal.style.display = 'none';
    });
  }

  if (friendsModal) {
    friendsModal.addEventListener('click', (e) => {
      if (e.target === friendsModal) friendsModal.style.display = 'none';
    });
  }

  if (urlParams.get('openFriends') === 'true') {
      if (friendsModal) {
          friendsModal.style.display = 'flex';
          cargarMisAmigos();
          cargarSolicitudes();
          if (urlParams.get('tab') === 'solicitudes') {
              const solTab = document.querySelector('.tab-btn[data-tab="solicitudes"]');
              if(solTab) solTab.click();
          }
      }
  }

  friendTabs.forEach(tab => {
    tab.addEventListener('click', () => {
      friendTabs.forEach(t => t.classList.remove('active'));
      friendContents.forEach(c => c.classList.remove('active-content'));
      
      tab.classList.add('active');
      const targetId = tab.dataset.tab;
      document.getElementById(targetId).classList.add('active-content');

      if (targetId === 'mis-amigos') {
        cargarMisAmigos();
      } else if (targetId === 'solicitudes') {
        cargarSolicitudes();
      } else if (targetId === 'buscar') {
        if (searchInput && searchInput.value.trim() !== '') {
            buscarUsuarios(searchInput.value);
        } else {
            limpiarResultadosBusqueda();
        }
      }
    });
  });

  let timeoutId;
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      clearTimeout(timeoutId);
      const query = e.target.value.trim();
      
      timeoutId = setTimeout(() => {
        if (query.length > 0) {
          buscarUsuarios(query);
        } else {
            limpiarResultadosBusqueda();
        }
      }, 500);
    });
  }

  function renderizarLista(contenedor, listaUsuarios, contexto) {
    const searchBar = contenedor.querySelector('.search-bar-container');
    contenedor.innerHTML = '';
    if (searchBar) contenedor.appendChild(searchBar);

    if (listaUsuarios.length === 0) {
      const msg = document.createElement('p');
      msg.style.textAlign = 'center';
      msg.style.color = '#888';
      msg.style.padding = '20px';
      
      if (contexto === 'SOLICITUDES') msg.textContent = 'No tienes solicitudes pendientes.';
      else if (contexto === 'AMIGOS') msg.textContent = 'Aún no has agregado amigos.';
      else msg.textContent = 'No se encontraron usuarios.';
      
      contenedor.appendChild(msg);
      return;
    }

    listaUsuarios.forEach(usuario => {
      const card = document.createElement('div');
      card.className = 'friend-card';
      
      const iniciales = usuario.nombreUsuario ? usuario.nombreUsuario.substring(0, 2).toUpperCase() : "??";
      const colores = ['bg-blue', 'bg-green', 'bg-pink', 'bg-orange', 'bg-purple', 'bg-dark'];
      const colorClase = colores[usuario.nombreUsuario.length % colores.length];

      let avatarHtml = `<div class="avatar-circle ${colorClase}">${iniciales}</div>`;
      if (usuario.avatarUrl) {
          avatarHtml = `<img src="${usuario.avatarUrl}" class="avatar-circle" style="object-fit:cover;">`;
      }

      let botonesHtml = `<a href="/social/perfil/${usuario.id}" class="btn-small btn-outline">Ver Perfil</a>`;

      if (contexto === 'SOLICITUDES') {
        botonesHtml += `
          <button class="btn-small btn-primary-small" onclick="responderSolicitud(${usuario.solicitudId}, true, this)">
            <i class='bx bx-check'></i> Aceptar
          </button>
          <button class="btn-small btn-danger-small" onclick="responderSolicitud(${usuario.solicitudId}, false, this)">
            <i class='bx bx-x'></i> Rechazar
          </button>
        `;
      } else if (contexto === 'AMIGOS') {
        // --- BOTÓN ELIMINAR ---
        botonesHtml += `
          <button class="btn-small btn-danger-small" onclick="eliminarAmigo(${usuario.id}, this)">
            <i class='bx bx-user-x'></i> Eliminar
          </button>
        `;
      } else if (contexto === 'BUSQUEDA') {
        if (usuario.estadoRelacion === 'NADA') {
            botonesHtml += `
              <button class="btn-small btn-primary-small" onclick="enviarSolicitud(${usuario.id}, this)">
                <i class='bx bx-user-plus'></i> Agregar
              </button>
            `;
        } else if (usuario.estadoRelacion === 'PENDIENTE_ENVIADA') {
            botonesHtml += `<span style="font-size:12px; color:#888; align-self:center;">Solicitud enviada</span>`;
        } else if (usuario.estadoRelacion === 'AMIGOS') {
            botonesHtml += `<span style="font-size:12px; color:green; align-self:center;">Amigos</span>`;
        }
      }

      card.innerHTML = `
        ${avatarHtml}
        <div class="user-info">
          <span class="user-name">${usuario.nombreUsuario}</span>
          <span class="user-meta">
            <span class="league-highlight ${usuario.liga}">${usuario.liga}</span> • ${usuario.puntosLiga} pts
          </span>
        </div>
        <div class="friend-actions">
          ${botonesHtml}
        </div>
      `;
      contenedor.appendChild(card);
    });
  }

  function limpiarResultadosBusqueda() {
      const searchBar = searchTab.querySelector('.search-bar-container');
      searchTab.innerHTML = '';
      if(searchBar) searchTab.appendChild(searchBar);
  }

});

// =========================================================
// 4. FUNCIONES GLOBALES (ACCIONES)
// =========================================================

function enviarSolicitud(receptorId, btnElement) {
    const csrfToken = document.querySelector('input[name="_csrf"]')?.value;
    
    fetch('/social/solicitud/enviar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `receptorId=${receptorId}&_csrf=${csrfToken || ''}`
    })
    .then(response => {
        if (response.ok) {
            // Actualizar búsqueda si estamos en el modal
            const searchInput = document.querySelector('#buscar .search-input');
            if (searchInput && searchInput.value && window.buscarUsuarios) {
                window.buscarUsuarios(searchInput.value);
            } else {
                // Si estamos en la página de perfil, recargar
                window.location.reload();
            }
        } else {
            alert("Error al enviar solicitud");
        }
    })
    .catch(err => console.error(err));
}

function responderSolicitud(solicitudId, aceptar, btnElement) {
    const csrfToken = document.querySelector('input[name="_csrf"]')?.value;

    fetch('/social/solicitud/responder', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `solicitudId=${solicitudId}&aceptar=${aceptar}&_csrf=${csrfToken || ''}`
    })
    .then(response => {
        if (response.ok) {
            const card = btnElement.closest('.friend-card');
            card.style.opacity = '0';
            setTimeout(() => {
                card.remove();
                // Actualizar contadores si se vacía la lista
                if (window.cargarSolicitudes) window.cargarSolicitudes();
                if (window.cargarMisAmigos) window.cargarMisAmigos();
            }, 300);
        } else {
            alert("Error al procesar solicitud");
        }
    })
    .catch(err => console.error(err));
}

// --- FUNCIÓN GLOBAL DE ELIMINAR ---
function eliminarAmigo(amigoId, btnElement) {
    if(!confirm("¿Estás seguro de que quieres eliminar a este amigo?")) return;

    const csrfToken = document.querySelector('input[name="_csrf"]')?.value;

    fetch('/social/amigo/eliminar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `amigoId=${amigoId}&_csrf=${csrfToken || ''}`
    })
    .then(response => {
        if (response.ok) {
            // Si venimos del modal (tiene btnElement)
            if (btnElement) {
                const card = btnElement.closest('.friend-card');
                card.style.opacity = '0';
                setTimeout(() => {
                    card.remove();
                    if (window.cargarMisAmigos) window.cargarMisAmigos();
                }, 300);
            } else {
                // Si venimos de la página de perfil
                window.location.reload();
            }
        } else {
            alert("Error al eliminar amigo");
        }
    })
    .catch(err => console.error(err));
}