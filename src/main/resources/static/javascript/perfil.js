// Script para funcionalidades de la página de perfil

document.addEventListener('DOMContentLoaded', function() {
    // Configurar sistema de pestañas
    setupTabs();
    
    // Toggle de visibilidad de contraseña
    setupPasswordToggle();

    // Validación de fortaleza de contraseña
    setupPasswordStrengthValidator();

    // Vista previa de imagen
    setupImagePreview();

    // Confirmación antes de eliminar cuenta
    setupDeleteConfirmation();

    // Cargar nombre de usuario en navbar
    loadUserInfo();
});

/**
 * Configurar sistema de pestañas
 */
function setupTabs() {
    const tabButtons = document.querySelectorAll('[data-tab]');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            // Obtener el ID de la pestaña a mostrar
            const tabId = this.getAttribute('data-tab');

            // Ocultar todas las pestañas
            tabContents.forEach(content => {
                content.style.display = 'none';
            });

            // Remover clase active de todos los botones
            tabButtons.forEach(btn => {
                btn.classList.remove('active');
            });

            // Mostrar la pestaña seleccionada
            const selectedTab = document.getElementById(tabId);
            if (selectedTab) {
                selectedTab.style.display = 'block';
                selectedTab.classList.add('active');
            }

            // Añadir clase active al botón clickeado
            this.classList.add('active');
        });
    });
}

/**
 * Toggle de visibilidad de contraseña
 */
function setupPasswordToggle() {
    const toggleButtons = document.querySelectorAll('.toggle-password');

    toggleButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('data-target');
            const input = document.getElementById(targetId);
            const icon = this.querySelector('i');

            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });
    });
}

/**
 * Validador de fortaleza de contraseña
 */
function setupPasswordStrengthValidator() {
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const passwordStrength = document.getElementById('passwordStrength');
    const passwordBar = document.getElementById('passwordBar');
    const strengthText = document.getElementById('strengthText');

    if (!newPasswordInput) return;

    newPasswordInput.addEventListener('input', function() {
        const password = this.value;
        const strength = calculatePasswordStrength(password);

        if (password.length > 0) {
            passwordStrength.style.display = 'block';
            passwordBar.textContent = strength.text;

            // Remover clases anteriores
            passwordBar.classList.remove('weak', 'fair', 'strong');

            // Añadir clase según fortaleza
            if (strength.level === 'weak') {
                passwordBar.classList.add('weak');
                passwordBar.style.backgroundColor = '#dc3545';
            } else if (strength.level === 'fair') {
                passwordBar.classList.add('fair');
                passwordBar.style.backgroundColor = '#ffc107';
            } else if (strength.level === 'strong') {
                passwordBar.classList.add('strong');
                passwordBar.style.backgroundColor = '#28a745';
            }
        } else {
            passwordStrength.style.display = 'none';
        }
    });
}

/**
 * Calcula la fortaleza de una contraseña
 */
function calculatePasswordStrength(password) {
    let strength = 0;
    const feedback = [];

    if (password.length >= 8) strength += 20;
    if (password.length >= 12) strength += 10;
    if (/[a-z]/.test(password)) strength += 20;
    if (/[A-Z]/.test(password)) strength += 20;
    if (/[0-9]/.test(password)) strength += 20;
    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength += 10;

    let level = 'weak';
    let text = 'Débil';

    if (strength >= 60 && strength < 80) {
        level = 'fair';
        text = 'Moderada';
    } else if (strength >= 80) {
        level = 'strong';
        text = 'Fuerte';
    }

    return { level, text, strength: Math.min(strength, 100) };
}

/**
 * Vista previa de imagen
 */
function setupImagePreview() {
    const fileInput = document.getElementById('profilePhoto');
    const preview = document.getElementById('preview');
    const progressDiv = document.getElementById('uploadProgress');
    const progressBar = document.getElementById('progressBar');

    if (!fileInput) return;

    fileInput.addEventListener('change', function(e) {
        const file = this.files[0];

        if (file) {
            // Validar tipo de archivo
            if (!file.type.startsWith('image/')) {
                showAlert('El archivo debe ser una imagen', 'danger');
                this.value = '';
                return;
            }

            // Validar tamaño (5MB)
            if (file.size > 5 * 1024 * 1024) {
                showAlert('La imagen no debe exceder 5MB', 'danger');
                this.value = '';
                return;
            }

            // Mostrar vista previa
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.src = e.target.result;
                preview.style.animation = 'fadeIn 0.3s ease-in-out';
            };
            reader.readAsDataURL(file);
        }
    });

    // Simular progreso de carga
    const formFoto = document.getElementById('formFoto');
    if (formFoto) {
        formFoto.addEventListener('submit', function(e) {
            if (fileInput.files.length > 0) {
                progressDiv.style.display = 'block';
                let progress = 0;
                const interval = setInterval(() => {
                    progress += Math.random() * 30;
                    if (progress > 90) progress = 90;
                    progressBar.style.width = progress + '%';

                    if (progress > 90) {
                        clearInterval(interval);
                    }
                }, 200);
            }
        });
    }
}

/**
 * Confirmación antes de eliminar cuenta
 */
function setupDeleteConfirmation() {
    const formEliminar = document.getElementById('formEliminar');
    const confirmDelete = document.getElementById('confirmDelete');

    if (!formEliminar) return;

    formEliminar.addEventListener('submit', function(e) {
        if (!confirmDelete.checked) {
            e.preventDefault();
            showAlert('Debes confirmar que deseas eliminar tu cuenta', 'warning');
            return;
        }

        // Mostrar confirmación adicional
        if (!confirm('¿Estás seguro? Esta acción no se puede deshacer. Se perderán todos tus datos y tu historial de compras.')) {
            e.preventDefault();
        }
    });
}

/**
 * Cargar información del usuario en navbar
 */
function loadUserInfo() {
    // El usuario ya está disponible del servidor, pero podemos cargar desde localStorage si existe
    const username = localStorage.getItem('username');
    if (username) {
        const nombreUsuario = document.getElementById('nombreUsuario');
        if (nombreUsuario) {
            nombreUsuario.textContent = username;
        }
    }

    // Mostrar nombre del usuario en el navbar si está disponible
    try {
        const userElement = document.querySelector('[th\\:text="${user.username}"]');
        if (userElement && userElement.textContent) {
            localStorage.setItem('username', userElement.textContent);
        }
    } catch (e) {
        // Ignorar error
    }
}

/**
 * Mostrar alerta
 */
function showAlert(message, type = 'info') {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="bi bi-${getAlertIcon(type)}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    `;

    const container = document.querySelector('main .container');
    if (container) {
        const existingAlert = container.querySelector('.alert');
        if (existingAlert) {
            existingAlert.remove();
        }
        container.insertAdjacentHTML('afterbegin', alertHtml);
    }
}

/**
 * Obtener icono para alerta según tipo
 */
function getAlertIcon(type) {
    const icons = {
        'success': 'check-circle',
        'danger': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

/**
 * Manejar cambio de pestaña en el sidebar (eliminado - ahora manejado en setupTabs)
 */

/**
 * Validación de formulario de datos personales en tiempo real
 */
document.addEventListener('DOMContentLoaded', function() {
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            const email = this.value;
            if (email && !isValidEmail(email)) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
            }
        });
    }
});

/**
 * Validar formato de email
 */
function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

/**
 * Cargar información de usuario desde API o localStorage
 */
function loadUserProfile() {
    fetch('/api/users/profile', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
    })
    .then(data => {
        if (data) {
            // Guardar en localStorage para referencia rápida
            localStorage.setItem('username', data.username);
            localStorage.setItem('userEmail', data.email);
        }
    })
    .catch(error => {
        console.log('No se pudo cargar el perfil desde API');
    });
}

// Cargar perfil al iniciar
loadUserProfile();
