document.addEventListener('DOMContentLoaded', () => {
  // 🔹 Botón de cerrar sesión
  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
      // Eliminar usuario activo del localStorage
      localStorage.removeItem('usuarioActivo');

      // Redirigir al login
      window.location.href = '/login';
    });
  }

  // 🔹 Botones de la barra lateral
  const buttons = document.querySelectorAll('.sidebar-btn');
  const sections = document.querySelectorAll('.contenido-section');

  if (buttons.length > 0 && sections.length > 0) {
    buttons.forEach((btn) => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();

        // Quitar la clase "active" de todos los botones
        buttons.forEach((b) => b.classList.remove('active'));
        btn.classList.add('active');

        // Ocultar todas las secciones
        sections.forEach((sec) => sec.classList.add('d-none'));

        // Mostrar solo la sección seleccionada
        const sectionId = btn.getAttribute('data-section');
        const targetSection = document.getElementById(sectionId);

        if (targetSection) {
          targetSection.classList.remove('d-none');
        } else {
          console.warn(`⚠️ No se encontró la sección con id "${sectionId}"`);
        }
      });
    });
  } else {
    console.warn('⚠️ No se encontraron botones o secciones para manejar el cambio de vista.');
  }

  // Cargar promociones dinámicas desde la API y mostrarlas en la sección Promociones
  function cargarPromocionesEnProductos() {
    const container = document.getElementById('promosContainer');
    if (!container) return;
    fetch('/api/promotions')
      .then(res => res.json())
      .then(promos => {
        container.innerHTML = '';
        if (!promos || promos.length === 0) {
          container.innerHTML = '<div class="col-12"><div class="alert alert-secondary">No hay promociones en este momento.</div></div>';
          return;
        }
        promos.forEach(p => {
          const col = document.createElement('div');
          col.className = 'col-md-4';
          col.innerHTML = `
            <div class="card h-100 shadow-sm ${p.discount ? 'border-primary' : ''}">
              ${p.imageUrl ? `<img src="${p.imageUrl}" class="card-img-top" style="height:160px; object-fit:cover;"/>` : ''}
              <div class="card-body">
                <h5 class="card-title">${p.title}</h5>
                <p class="card-text">${p.description || ''}</p>
                ${p.discount ? `<div class="fw-bold text-danger">-${p.discount}%</div>` : ''}
              </div>
            </div>
          `;
          container.appendChild(col);
        });
      })
      .catch(err => {
        console.error('Error cargando promociones en productos:', err);
      });
  }

  // Ejecutar carga de promociones al inicio
  cargarPromocionesEnProductos();
});
