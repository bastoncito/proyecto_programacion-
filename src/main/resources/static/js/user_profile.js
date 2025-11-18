document.addEventListener("DOMContentLoaded", function () {
  const tooltip = document.getElementById("achievement-tooltip");
  const tooltipTitle = document.getElementById("tooltip-title");
  const tooltipDesc = document.getElementById("tooltip-desc");
  const tooltipExp = document.getElementById("tooltip-exp");
  const tooltipDate = document.getElementById("tooltip-date");
  const achievementItems = document.querySelectorAll(".achievement-item");

  // Función para ocultar el tooltip
  const hideTooltip = () => {
    if (!tooltip) return;
    tooltip.classList.remove("visible");
    tooltip.removeAttribute("data-current-item");
  };

  // Manejo de scroll para mejorar experiencia en móvil
  window.addEventListener('scroll', hideTooltip, { passive: true });

  // Listener para cerrar el tooltip si se hace clic fuera de él
  document.addEventListener("click", function (e) {
    if (
      !e.target.closest(".achievement-item") &&
      tooltip &&
      tooltip.classList.contains("visible")
    ) {
      hideTooltip();
    }
  });

  achievementItems.forEach((item) => {
    // Mostrar tooltip al pasar el mouse
    item.addEventListener("mouseenter", (e) => {
      const target = e.currentTarget;
      fillTooltipData(target);
      positionTooltip(target);
      if (tooltip) tooltip.classList.add("visible");
    });

    // Comportamiento hover para escritorio
    item.addEventListener("mouseleave", () => {
      if (tooltip && !tooltip.hasAttribute("data-current-item")) {
        hideTooltip();
      }
    });

    // Comportamiento táctil para móvil y clic en escritorio  
    item.addEventListener("click", function (e) {
      e.stopPropagation();
      const target = e.currentTarget;
      const currentItemId = target.dataset.title;

      if (
        tooltip &&
        tooltip.classList.contains("visible") &&
        tooltip.getAttribute("data-current-item") === currentItemId
      ) {
        hideTooltip();
      } else {
        fillTooltipData(target);
        positionTooltip(target);
        if (tooltip) tooltip.classList.add("visible");
        if (tooltip) tooltip.setAttribute("data-current-item", currentItemId);
      }
    });
  });

  // --- LÓGICA PARA VALIDACIÓN DE CIUDAD ---

  const cityInput = document.getElementById('ciudad');
  const cityStatus = document.getElementById('city-validation-status');
  const cityIcon = cityStatus ? cityStatus.querySelector('i') : null; // Obtenemos el ícono <i>
  let debounceTimer;

  if (cityInput && cityStatus && cityIcon) {
    
    const validateCity = () => {
      const cityName = cityInput.value.trim();
      const validationUrl = cityInput.dataset.validationUrl;

      // Si el campo está vacío, simplemente ocultamos el ícono.
      if (cityName === '') {
        cityStatus.classList.remove('visible', 'loading', 'valid', 'invalid');
        return;
      }

      // Mostrar estado de carga
      cityStatus.classList.remove('valid', 'invalid');
      cityStatus.classList.add('loading', 'visible');
      cityIcon.className = 'bx bx-loader-alt'; // Usamos la clase para el spinner

      fetch(`${validationUrl}?city=${encodeURIComponent(cityName)}`)
        .then(response => {
          if (!response.ok) throw new Error('Error de red');
          return response.json();
        })
        .then(data => {
          // Quitar el estado de carga
          cityStatus.classList.remove('loading');

          // Mostrar el resultado correcto (éxito o error)
          if (data.valid) {
            cityStatus.classList.add('valid');
            cityIcon.className = 'bx bx-check'; // Clase para el ícono de check
          } else {
            cityStatus.classList.add('invalid');
            cityIcon.className = 'bx bx-x'; // Clase para el ícono de 'x'
          }
        })
        .catch(error => {
          console.error('Error en la validación:', error);
          cityStatus.classList.remove('loading');
          cityStatus.classList.add('invalid');
          cityIcon.className = 'bx bx-x'; // Mostramos 'x' también si hay un error de red
        });
    };

    // Listener que evita muchas peticiones
    cityInput.addEventListener('input', () => {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(validateCity, 500); // Espera 500ms después de la última tecla
    });
  }

  /**
   * Llena el tooltip con los datos del logro
   * @param {HTMLElement} target - Elemento del logro
   */
  function fillTooltipData(target) {
    if (!tooltip) return;
    const isUnlocked = target.dataset.unlocked === "true";
    if (tooltipTitle) tooltipTitle.textContent = target.dataset.title;
    if (tooltipDesc)
      tooltipDesc.textContent = isUnlocked
        ? target.dataset.desc
        : "Requisito: " + target.dataset.desc;
    const exp = parseInt(target.dataset.exp, 10);
    if (exp > 0) {
      if (tooltipExp) {
        tooltipExp.textContent = `+${exp} XP`;
        tooltipExp.style.display = "block";
      }
    } else if (tooltipExp) {
      tooltipExp.style.display = "none";
    }
    if (isUnlocked) {
      if (tooltipDate) {
        // Leemos la fecha real desde el atributo data-date
        const fecha = target.dataset.date;
        
        // Nos aseguramos de que la fecha exista antes de mostrarla
        if (fecha) {
            tooltipDate.textContent = `Desbloqueado el ${fecha}`;
            tooltipDate.style.display = "block";
        } else {
            // Oculta la fecha si está desbloqueado pero no hay fecha (por si acaso)
            tooltipDate.style.display = "none";
        }
      }
    } else if (tooltipDate) {
      tooltipDate.style.display = "none";
    }
  }

  /**
   * Posiciona el tooltip de forma inteligente usando coordenadas de la ventana (viewport).
   * Funciona tanto para elementos normales como para elementos 'sticky'.
   * @param {HTMLElement} target - El elemento del logro que activó el tooltip.
   */
  function positionTooltip(target) {
    if (!tooltip) return;

    // getBoundingClientRect() da la posición RELATIVA A LA VENTANA.
    const targetRect = target.getBoundingClientRect();
    const tooltipHeight = tooltip.offsetHeight;
    const tooltipWidth = tooltip.offsetWidth;

    // --- 1. CÁLCULO DE POSICIÓN VERTICAL (top) ---
    let top;
    // ¿Hay espacio arriba del icono? (respecto a la ventana)
    if (targetRect.top > tooltipHeight + 10) {
      top = targetRect.top - tooltipHeight - 10; // Posicionar arriba
      tooltip.classList.remove("arrow-up");
      tooltip.classList.add("arrow-down");
    } else {
      top = targetRect.bottom + 10; // Posicionar abajo
      tooltip.classList.remove("arrow-down");
      tooltip.classList.add("arrow-up");
    }

    // --- 2. CÁLCULO DE POSICIÓN HORIZONTAL (left) ---
    let left = targetRect.left + targetRect.width / 2 - tooltipWidth / 2;

    // Ajustar si se sale por los bordes de la ventana
    if (left < 10) {
      left = 10;
    } else if (left + tooltipWidth > window.innerWidth - 10) {
      left = window.innerWidth - tooltipWidth - 10;
    }

    // --- 3. CÁLCULO DE LA POSICIÓN DE LA FLECHA ---
    const iconCenterAbsoluteX = targetRect.left + targetRect.width / 2;
    const arrowLeft = iconCenterAbsoluteX - left;

    // --- 4. APLICAR TODOS LOS ESTILOS CALCULADOS ---
    tooltip.style.top = `${top}px`;
    tooltip.style.left = `${left}px`;
    tooltip.style.setProperty("--arrow-left", `${arrowLeft}px`);
  }
});