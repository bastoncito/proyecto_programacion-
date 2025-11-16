// Lógica del tooltip inteligente movida desde user_profile.html
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
      const target = e.currentTarget; // Llenar datos...
      fillTooltipData(target); // Posicionar...
      positionTooltip(target); // Mostrar.
      if (tooltip) tooltip.classList.add("visible"); // Mostrar tooltip
    });

    // Para quitar el hover en escritorio
    item.addEventListener("mouseleave", () => {
      if (tooltip && !tooltip.hasAttribute("data-current-item")) {
        tooltip.classList.remove("visible");
      }
    });

    // Para el clic en móvil (y escritorio)
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
   * Llena el tooltip con los datos del logro
   * @param {HTMLElement} target - Elemento del logro
   */
  function fillTooltipData(target) {
    if (!tooltip) return;
    const isUnlocked = target.dataset.unlocked === "true";
    // Título del logro
    if (tooltipTitle) tooltipTitle.textContent = target.dataset.title;
    // Descripción - cambia según si está desbloqueado o no
    if (tooltipDesc)
      tooltipDesc.textContent = isUnlocked
        ? target.dataset.desc
        : "Requisito: " + target.dataset.desc;

    const exp = parseInt(target.dataset.exp, 10);
    // Experiencia - solo muestra si es mayor a 0
    if (exp > 0) {
      if (tooltipExp) {
        tooltipExp.textContent = `+${exp} XP`;
        tooltipExp.style.display = "block";
      }
    } else if (tooltipExp) {
      tooltipExp.style.display = "none";
    }
    // Fecha de desbloqueo - solo muestra si está desbloqueado
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
   * Posiciona el tooltip de forma inteligente, evitando que se salga de la pantalla.
   * @param {HTMLElement} target - El elemento del logro que activó el tooltip.
   */
  function positionTooltip(target) {
    if (!tooltip) return;
    const tooltipWidth = tooltip.offsetWidth;
    const tooltipHeight = tooltip.offsetHeight;

    // Obtenemos el contenedor con position:relative
    const container = target.offsetParent;
    if (!container) return; // Salida segura si no hay contenedor

    // Coordenadas del ícono y del contenedor relativas a la ventana (viewport)
    const targetRect = target.getBoundingClientRect();
    const containerRect = container.getBoundingClientRect();

    // --- 1. CÁLCULO DE POSICIÓN VERTICAL (top) ---
    let top;
    // Decide si hay espacio para mostrar el tooltip arriba del ícono
    if (targetRect.top - tooltipHeight - 10 > 0) {
      // Posición 'top' relativa al contenedor
      top = target.offsetTop - tooltipHeight - 10;
      tooltip.classList.remove("arrow-up");
      tooltip.classList.add("arrow-down");
    } else {
      // O lo muestra abajo
      top = target.offsetTop + target.offsetHeight + 10;
      tooltip.classList.remove("arrow-down");
      tooltip.classList.add("arrow-up");
    }

    // --- 2. CÁLCULO DE POSICIÓN HORIZONTAL (left) CON AJUSTE DE BORDES ---
    // a) Posición 'left' ideal (centrado sobre el ícono), relativa al contenedor
    let left = target.offsetLeft + target.offsetWidth / 2 - tooltipWidth / 2;

    // b) Calculamos la posición absoluta (en la pantalla) que tendría el tooltip
    const absoluteLeft = containerRect.left + left;

    // c) Corregimos si se sale por la izquierda
    if (absoluteLeft < 10) {
      // 10px de margen
      // Recalculamos 'left' para que el borde izquierdo absoluto sea 10
      left = 10 - containerRect.left;
    }
    // d) Corregimos si se sale por la derecha
    else if (absoluteLeft + tooltipWidth > window.innerWidth - 10) {
      // Recalculamos 'left' para que el borde derecho absoluto sea el borde de la ventana - 10
      left = window.innerWidth - tooltipWidth - 10 - containerRect.left;
    }

    // --- 3. CÁLCULO DE LA POSICIÓN DE LA FLECHA ---
    // La flecha debe apuntar al centro del ícono, sin importar dónde se movió el tooltip.
    // Calculamos el centro absoluto del ícono
    const iconCenterAbsoluteX = targetRect.left + targetRect.width / 2;
    // Calculamos la posición absoluta final del tooltip
    const tooltipFinalAbsoluteX = containerRect.left + left;
    // La posición de la flecha es la diferencia
    const arrowLeft = iconCenterAbsoluteX - tooltipFinalAbsoluteX;

    // --- 4. APLICAR TODOS LOS ESTILOS CALCULADOS ---
    tooltip.style.top = `${top}px`;
    tooltip.style.left = `${left}px`;
    tooltip.style.setProperty("--arrow-left", `${arrowLeft}px`);
  }
});
