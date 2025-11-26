package com.tuempresa.fitboost.controller;

import com.tuempresa.fitboost.model.Order;
import com.tuempresa.fitboost.model.User;
import com.tuempresa.fitboost.repository.OrderRepository;
import com.tuempresa.fitboost.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Procesa el formulario de pago y crea una nueva orden (API)
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String usuario,
            @RequestParam(required = false) String email,
            @RequestParam String direccion,
            @RequestParam(required = false) String direccion2,
            @RequestParam String pais,
            @RequestParam String estado,
            @RequestParam String codigoPostal,
            @RequestParam String productDetails,
            @RequestParam Double totalAmount,
            Authentication authentication) {

        try {
            // Obtener el usuario actual
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear nueva orden
            Order order = new Order();
            order.setUser(user);
            order.setTotalAmount(totalAmount);
            order.setShippingAddress(direccion);
            order.setCity(pais);
            order.setState(estado);
            order.setPostalCode(codigoPostal);
            order.setCountry(pais);
            order.setOrderDate(LocalDateTime.now());
            // Estimar entrega en 7 días
            order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7));
            order.setStatus("En Tránsito");
            order.setProductDetails(productDetails);

            // Guardar la orden
            Order savedOrder = orderRepository.save(order);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Compra realizada con éxito! Tu pedido llegará en 7 días aproximadamente.");
            response.put("orderId", savedOrder.getId());
            response.put("totalAmount", savedOrder.getTotalAmount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al procesar el pedido: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Obtiene todas las órdenes del usuario actual
     */
    @GetMapping("/my-orders")
    public String getMyOrders(Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        model.addAttribute("orders", orders);

        return "cliente/compras";
    }
}
