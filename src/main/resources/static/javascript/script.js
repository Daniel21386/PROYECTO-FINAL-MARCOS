// Script global para la aplicación

document.addEventListener('DOMContentLoaded', function() {
    // Manejar logout
    setupLogout();
    
    // Cargar nombre de usuario
    loadUserInfo();
});

/**
 * Configurar el botón de logout
 */
function setupLogout() {
    const logoutBtn = document.getElementById('logout-btn');
    
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Limpiar localStorage
            localStorage.removeItem('carrito');
            localStorage.removeItem('username');
            localStorage.removeItem('userEmail');
            
            // Redirigir a logout de Spring Security
            window.location.href = '/logout';
        });
    }
}

/**
 * Cargar información del usuario en el navbar
 */
function loadUserInfo() {
    // Obtener nombre de usuario del HTML renderizado
    const userNameElement = document.querySelector('[th\\:text="${user.username}"]');
    
    if (userNameElement && userNameElement.textContent) {
        const username = userNameElement.textContent.trim();
        localStorage.setItem('username', username);
        
        // Actualizar todos los elementos que muestren el nombre
        const nombreUsuarioElements = document.querySelectorAll('#nombreUsuario');
        nombreUsuarioElements.forEach(el => {
            el.textContent = username;
        });
    }
}

/**
 * Función auxiliar para hacer peticiones autenticadas
 */
function fetchAutenticado(url, options = {}) {
    const defaultOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'same-origin' // Incluir cookies de sesión
    };
    
    return fetch(url, { ...defaultOptions, ...options });
}
