// Script para el dashboard admin

document.addEventListener('DOMContentLoaded', function() {
    // Cargar estadísticas del dashboard
    loadDashboardStats();
});

/**
 * Cargar estadísticas del dashboard desde el backend
 */
function loadDashboardStats() {
    // Obtener datos de usuarios
    fetch('/api/admin/stats/users')
        .then(response => {
            if (response.ok) return response.json();
            throw new Error('Error al cargar usuarios');
        })
        .then(data => {
            if (data && data.total !== undefined) {
                document.getElementById('totalUsers').textContent = formatNumber(data.total);
            }
        })
        .catch(error => {
            console.log('No se pudieron cargar estadísticas de usuarios:', error);
            // Mantener los valores por defecto
        });

    // Obtener datos de órdenes
    fetch('/api/admin/stats/orders-today')
        .then(response => {
            if (response.ok) return response.json();
            throw new Error('Error al cargar órdenes');
        })
        .then(data => {
            if (data && data.count !== undefined) {
                document.getElementById('ordersToday').textContent = formatNumber(data.count);
            }
        })
        .catch(error => {
            console.log('No se pudieron cargar órdenes de hoy:', error);
        });

    // Obtener datos de ventas del mes
    fetch('/api/admin/stats/monthly-sales')
        .then(response => {
            if (response.ok) return response.json();
            throw new Error('Error al cargar ventas');
        })
        .then(data => {
            if (data && data.total !== undefined) {
                document.getElementById('monthlySales').textContent = '$' + formatNumber(data.total);
            }
        })
        .catch(error => {
            console.log('No se pudieron cargar ventas del mes:', error);
        });

    // Obtener datos de stock bajo
    fetch('/api/admin/stats/low-stock')
        .then(response => {
            if (response.ok) return response.json();
            throw new Error('Error al cargar stock');
        })
        .then(data => {
            if (data && data.count !== undefined) {
                document.getElementById('lowStock').textContent = formatNumber(data.count);
            }
        })
        .catch(error => {
            console.log('No se pudieron cargar productos con stock bajo:', error);
        });
}

/**
 * Formatea un número con separadores de miles
 */
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

/**
 * Manejar navegación de tarjetas con efectos
 */
document.querySelectorAll('.stat-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-5px)';
    });
    
    card.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
    });
});
