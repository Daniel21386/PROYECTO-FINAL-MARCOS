document.addEventListener('DOMContentLoaded', () => {
  // Ya no usamos localStorage para autenticación; Spring Security maneja eso.
  // Este script solo maneja la UI del logout y datos locales opcionales.

  // Manejar logout en botones posibles
  const logoutIds = ['logout-btn', 'btn-logout'];
  logoutIds.forEach(id => {
    const btn = document.getElementById(id);
    if (btn) {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        // Enviar POST a /logout (maneja Spring Security)
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/logout';
        document.body.appendChild(form);
        form.submit();
      });
    }
  });
});
