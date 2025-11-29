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

  /**
   * Llena el tooltip con los datos del logro (sin cambios)
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
    
    // Manejo de fecha (opcional, si viene del backend)
    if (isUnlocked) {
      if (tooltipDate) {
        const fecha = target.dataset.date;
        if (fecha) {
            tooltipDate.textContent = `Desbloqueado el ${fecha}`;
            tooltipDate.style.display = "block";
        } else {
            tooltipDate.style.display = "none";
        }
      }
    } else if (tooltipDate) {
      tooltipDate.style.display = "none";
    }
  }

  /**
   * Posiciona el tooltip de forma inteligente usando coordenadas de la ventana (viewport).
   * @param {HTMLElement} target - El elemento del logro que activó el tooltip.
   */
  function positionTooltip(target) {
    if (!tooltip) return;

    const targetRect = target.getBoundingClientRect();
    const tooltipHeight = tooltip.offsetHeight;
    const tooltipWidth = tooltip.offsetWidth;

    // --- 1. CÁLCULO DE POSICIÓN VERTICAL (top) ---
    let top;
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