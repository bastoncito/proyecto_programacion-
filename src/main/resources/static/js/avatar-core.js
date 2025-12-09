/**
 * avatar-core.js
 * Se encarga de renderizar avatares (imagen o iniciales) en cualquier
 * elemento que tenga la clase .auto-avatar
 */

document.addEventListener("DOMContentLoaded", function() {
    renderizarTodosLosAvatares();
});

function renderizarTodosLosAvatares() {
    // Buscamos todos los elementos con la clase "auto-avatar"
    const contenedores = document.querySelectorAll('.auto-avatar');

    contenedores.forEach(div => {
        // Leemos los datos que pusimos en el HTML (data-nombre y data-url)
        const nombre = div.getAttribute('data-nombre') || "??";
        const url = div.getAttribute('data-url');
        
        // Generamos el HTML (usando la misma lógica que ya conoces)
        div.innerHTML = generarHtmlAvatar(nombre, url);
        
        // Limpiamos estilos para que se integre bien
        div.style.backgroundColor = 'transparent'; 
        div.style.display = 'flex';
        div.style.justifyContent = 'center';
        div.style.alignItems = 'center';
    });
}

// La función lógica pura (la misma que hicimos antes)
function generarHtmlAvatar(nombreUsuario, avatarUrl) {
    // 1. Si hay URL válida (y no es string "null"), mostramos imagen
    if (avatarUrl && avatarUrl !== 'null' && avatarUrl.trim() !== "") {
        return `<img src="${avatarUrl}" class="avatar-circle" style="object-fit:cover; width:100%; height:100%;">`;
    }

    // 2. Si no, generamos iniciales y color
    const iniciales = nombreUsuario ? nombreUsuario.substring(0, 2).toUpperCase() : "??";
    const colores = ['bg-blue', 'bg-green', 'bg-pink', 'bg-orange', 'bg-purple', 'bg-dark'];
    const colorClase = colores[nombreUsuario.length % colores.length];

    return `<div class="avatar-circle ${colorClase}" style="width:100%; height:100%; display:flex; justify-content:center; align-items:center;">${iniciales}</div>`;
}