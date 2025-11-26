document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('login-form');
  const usernameInput = document.getElementById('username');
  const passwordInput = document.getElementById('password');
  const usernameError = document.getElementById('username-error');
  const passwordError = document.getElementById('password-error');

  // Validar usuario
  function validateUsername(value) {
    value = value.trim();
    
    if (!value) {
      usernameError.textContent = 'Por favor, indique un usuario válido.';
      usernameInput.classList.add('error');
      return false;
    }
    
    if (value.length < 3) {
      usernameError.textContent = 'El usuario debe tener al menos 3 caracteres.';
      usernameInput.classList.add('error');
      return false;
    }
    
    usernameError.textContent = '';
    usernameInput.classList.remove('error');
    return true;
  }

  // Validar contraseña
  function validatePassword(value) {
    if (!value) {
      passwordError.textContent = 'Por favor, indique una contraseña válida.';
      passwordInput.classList.add('error');
      return false;
    }
    
    if (value.length < 6) {
      passwordError.textContent = 'La contraseña debe tener al menos 6 caracteres.';
      passwordInput.classList.add('error');
      return false;
    }
    
    passwordError.textContent = '';
    passwordInput.classList.remove('error');
    return true;
  }

  // Validar en tiempo real
  if (usernameInput) {
    usernameInput.addEventListener('blur', () => {
      validateUsername(usernameInput.value);
    });

    usernameInput.addEventListener('input', () => {
      if (usernameInput.classList.contains('error')) {
        usernameError.textContent = '';
        usernameInput.classList.remove('error');
      }
    });
  }

  if (passwordInput) {
    passwordInput.addEventListener('blur', () => {
      validatePassword(passwordInput.value);
    });

    passwordInput.addEventListener('input', () => {
      if (passwordInput.classList.contains('error')) {
        passwordError.textContent = '';
        passwordInput.classList.remove('error');
      }
    });
  }

  // Validación de login
  if (loginForm) {
    loginForm.addEventListener('submit', (e) => {
      const isUsernameValid = validateUsername(usernameInput.value);
      const isPasswordValid = validatePassword(passwordInput.value);

      if (!isUsernameValid || !isPasswordValid) {
        e.preventDefault();
      }
    });
  }

  // Auto-ocultar alertas del servidor (ej. ?error) y limpiar el parámetro de la URL
  const serverAlert = document.querySelector('.alert-box.error');
  if (serverAlert) {
    // Después de 5s ocultamos visualmente la alerta
    setTimeout(() => {
      serverAlert.classList.remove('show');
    }, 5000);

    // Limpiamos el parámetro `error` de la URL sin recargar la página
    try {
      const currentUrl = new URL(window.location.href);
      if (currentUrl.searchParams.has('error')) {
        currentUrl.searchParams.delete('error');
        const newUrl = currentUrl.pathname + (currentUrl.search ? currentUrl.search : '');
        history.replaceState(null, '', newUrl);
      }
    } catch (e) {
      // Si URL no está disponible por alguna razón (IE antiguo), no hacemos nada
    }
  }
});