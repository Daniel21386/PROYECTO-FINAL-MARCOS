document.addEventListener('DOMContentLoaded', () => {
  // =========================================================
  // 1. LÓGICA DE USUARIO Y NAVEGACIÓN
  // =========================================================

  // 🔹 Botón de cerrar sesión
  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
      localStorage.removeItem('usuarioActivo');
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
        buttons.forEach((b) => b.classList.remove('active'));
        btn.classList.add('active');
        sections.forEach((sec) => sec.classList.add('d-none'));

        const sectionId = btn.getAttribute('data-section');
        const targetSection = document.getElementById(sectionId);

        if (targetSection) {
          targetSection.classList.remove('d-none');
        }
      });
    });
  }

  // 🔹 Cargar promociones dinámicas
  function cargarPromocionesEnProductos() {
    const container = document.getElementById('promosContainer');
    if (!container) return;
    fetch('/api/promotions')
      .then(res => res.json())
      .then(promos => {
        container.innerHTML = '';
        if (!promos || promos.length === 0) {
          container.innerHTML = '<div class="col-12"><div class="alert alert-secondary">No hay promociones.</div></div>';
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
      .catch(err => console.error(err));
  }
  cargarPromocionesEnProductos();


  // =========================================================
  // 2. LÓGICA DEL CARRITO DE COMPRAS (DISEÑO MEJORADO)
  // =========================================================

  let carrito = JSON.parse(localStorage.getItem('carritoFitBoost')) || [];
  actualizarCarritoUI();

  // Escuchar clics
  document.body.addEventListener('click', (e) => {
    // Añadir al carrito
    if (e.target.classList.contains('add-to-cart')) {
      const btn = e.target;
      const producto = {
        nombre: btn.getAttribute('data-nombre'),
        precio: parseFloat(btn.getAttribute('data-precio')),
        id: new Date().getTime()
      };
      agregarAlCarrito(producto);
    }
    
    // Eliminar del carrito (Buscamos el botón o el ícono dentro del botón)
    const btnEliminar = e.target.closest('.btn-eliminar-item');
    if (btnEliminar) {
      const idEliminar = parseInt(btnEliminar.getAttribute('data-id'));
      eliminarDelCarrito(idEliminar);
    }
  });

  function agregarAlCarrito(producto) {
    carrito.push(producto);
    guardarCarrito();
    actualizarCarritoUI();
  }

  function eliminarDelCarrito(id) {
    carrito = carrito.filter(item => item.id !== id);
    guardarCarrito();
    actualizarCarritoUI();
  }

  function guardarCarrito() {
    localStorage.setItem('carritoFitBoost', JSON.stringify(carrito));
  }

  function actualizarCarritoUI() {
    // 1. Actualizar numerito rojo (Badge)
    const cartCount = document.getElementById('cart-count');
    if (cartCount) {
      cartCount.textContent = carrito.length;
    }

    // 2. Actualizar lista visual en el Modal
    const listaCarrito = document.querySelector('#modalCarrito .col-md-5 .list-group');
    
    if (listaCarrito) {
      listaCarrito.innerHTML = ''; 

      let total = 0;

      carrito.forEach(item => {
        total += item.precio;
        
        const li = document.createElement('li');
        // Usamos 'd-flex' para poner Izquierda (Texto) y Derecha (Precio+Botón)
        // 'align-items-center' para que todo quede centrado verticalmente
        li.className = 'list-group-item d-flex justify-content-between align-items-center py-3 px-3';
        
        li.innerHTML = `
          <div class="me-auto pe-2" style="max-width: 65%;">
            <h6 class="my-0 fw-bold text-dark" style="font-size: 0.95rem; line-height: 1.2;">${item.nombre}</h6>
            <small class="text-muted" style="font-size: 0.85rem;">Suplemento</small>
          </div>

          <div class="d-flex align-items-center">
            <span class="fw-bold text-primary text-nowrap me-3" style="font-size: 1rem;">
              S/ ${item.precio.toFixed(2)}
            </span>
            <button class="btn btn-sm btn-outline-danger btn-eliminar-item d-flex align-items-center justify-content-center" 
                    style="width: 32px; height: 32px; border-radius: 50%; padding: 0;"
                    data-id="${item.id}" 
                    title="Eliminar producto">
              <i class="bi bi-trash-fill" style="font-size: 1rem;"></i>
            </button>
          </div>
        `;
        listaCarrito.appendChild(li);
      });

      // 3. Fila del Total
      const liTotal = document.createElement('li');
      liTotal.className = 'list-group-item d-flex justify-content-between align-items-center bg-light py-3 px-3 mt-2 border-top';
      liTotal.innerHTML = `
        <span class="fw-bold fs-5 text-dark">Total</span>
        <span class="fw-bold fs-4 text-primary">S/ ${total.toFixed(2)}</span>
      `;
      listaCarrito.appendChild(liTotal);
    }
  }
});
