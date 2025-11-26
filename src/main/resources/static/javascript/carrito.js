document.addEventListener('DOMContentLoaded', function() {
  // Referencias
  const resumenCarrito = document.querySelector('.modal-body .col-md-5 ul.list-group');
  const badgeCarrito = document.querySelectorAll('.badge.bg-primary.ms-2, .badge.bg-primary.rounded-pill, #cart-count, #cart-count-modal');
  const botonesAgregar = document.querySelectorAll('.add-to-cart');
  
  // La autenticación es manejada por Spring Security, no por localStorage
  // Si la página cargó, el usuario está autenticado

  // Inicializa el carrito desde localStorage o vacío
  let carrito = JSON.parse(localStorage.getItem('carrito')) || [];

  // Renderiza el carrito en el modal
  function renderCarrito() {
    resumenCarrito.innerHTML = '';
    let total = 0;

    if (carrito.length === 0) {
      resumenCarrito.innerHTML = `<li class="list-group-item text-center text-muted">Tu carrito está vacío.</li>`;
    } else {
      carrito.forEach((item, idx) => {
        resumenCarrito.innerHTML += `
          <li class="list-group-item d-flex justify-content-between align-items-center lh-sm">
            <div>
              <h6 class="my-0 fw-bold">${item.nombre}</h6>
            </div>
            <div class="d-flex align-items-center">
              <span class="text-muted me-2">$${item.precio}</span>
              <button class="btn btn-sm btn-link text-danger p-0 btn-eliminar" data-idx="${idx}" title="Eliminar">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </li>
        `;
        total += Number(item.precio);
      });
      resumenCarrito.innerHTML += `
        <li class="list-group-item d-flex justify-content-between">
          <span class="fw-bold">Total (USD)</span>
          <span class="fw-bold">$${total}</span>
        </li>
      `;
    }

    // Actualiza el badge del carrito
    badgeCarrito.forEach(badge => {
      if (badge) badge.textContent = carrito.length;
    });

    // Asigna eventos a los botones de eliminar
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
      btn.addEventListener('click', function() {
        const idx = parseInt(btn.getAttribute('data-idx'));
        carrito.splice(idx, 1);
        localStorage.setItem('carrito', JSON.stringify(carrito));
        renderCarrito();
      });
    });
  }

  // Evento para añadir productos
  botonesAgregar.forEach(btn => {
    btn.addEventListener('click', function() {
      const nombre = btn.getAttribute('data-nombre');
      const precio = btn.getAttribute('data-precio');
      carrito.push({ nombre, precio });
      localStorage.setItem('carrito', JSON.stringify(carrito));
      renderCarrito();
    });
  });

  // Renderiza el carrito al cargar la página
  renderCarrito();
});