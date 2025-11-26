package com.tuempresa.fitboost.controller;

import com.tuempresa.fitboost.dto.OrderDTO;
import com.tuempresa.fitboost.dto.ProductDTO;
import com.tuempresa.fitboost.dto.UserDTO;
import com.tuempresa.fitboost.model.Order;
import com.tuempresa.fitboost.model.Product;
import com.tuempresa.fitboost.model.User;
import com.tuempresa.fitboost.repository.OrderRepository;
import com.tuempresa.fitboost.repository.ProductRepository;
import com.tuempresa.fitboost.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        
        return "dashboard/" + role.toLowerCase();
    }

    /**
     * Obtiene todas las órdenes para el dashboard admin (como DTOs)
     */
    @GetMapping("/api/admin/orders")
    @ResponseBody
    public List<OrderDTO> getAllOrders() {
        System.out.println("=== ENDPOINT /api/admin/orders llamado ===");
        List<Order> orders = orderRepository.findAll();
        System.out.println("Total de órdenes encontradas: " + orders.size());
        
        List<OrderDTO> dtos = orders.stream()
                .map(order -> {
                    System.out.println("Procesando orden ID: " + order.getId());
                    // Safely build client name: prefer firstName/lastName, fallback to username
                    String clientName = "";
                    if (order.getUser() != null) {
                        String first = order.getUser().getFirstName();
                        String last = order.getUser().getLastName();
                        String username = order.getUser().getUsername();
                        boolean hasFirst = first != null && !first.isBlank();
                        boolean hasLast = last != null && !last.isBlank();
                        if (hasFirst || hasLast) {
                            clientName = (hasFirst ? first : "") + (hasFirst && hasLast ? " " : "") + (hasLast ? last : "");
                        } else if (username != null && !username.isBlank()) {
                            clientName = username;
                        }
                    }
                    return new OrderDTO(
                            order.getId(),
                            clientName,
                            order.getOrderDate(),
                            order.getTotalAmount(),
                            order.getStatus()
                    );
                })
                .collect(Collectors.toList());
        
        System.out.println("DTOs creados: " + dtos.size());
        return dtos;
    }

    /**
     * Obtiene todos los usuarios clientes para el dashboard admin (como DTOs)
     */
    @GetMapping("/api/admin/users")
    @ResponseBody
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Order> allOrders = orderRepository.findAll();
        
        return users.stream()
                .map(user -> {
                    int orderCount = (int) allOrders.stream()
                        .filter(order -> order.getUser().getId().equals(user.getId()))
                        .count();
                    // Evitar enviar "null" en el frontend: usar username como fallback si no hay nombres
                    String first = user.getFirstName() != null && !user.getFirstName().isBlank()
                        ? user.getFirstName()
                        : (user.getUsername() != null ? user.getUsername() : "");
                    String last = user.getLastName() != null && !user.getLastName().isBlank()
                        ? user.getLastName()
                        : "";
                        return new UserDTO(
                            user.getId(),
                            first,
                            last,
                            user.getEmail(),
                            user.getPhone(),
                            orderCount,
                            user.getRole()
                        );
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los productos para el dashboard admin (como DTOs)
     */
    @GetMapping("/api/admin/products")
    @ResponseBody
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getStock()
                ))
                .collect(Collectors.toList());
    }

    // Endpoint para cambiar rol de usuario (usado por el dashboard admin via AJAX)
    @PutMapping("/api/admin/users/{id}/role")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @RequestParam String role) {
        return userRepository.findById(id)
                .map(user -> {
                    try {
                        System.out.println("changeUserRole called for id=" + id + " with raw role='" + role + "'");
                        String incoming = role == null ? "" : role.trim();
                        System.out.println("Parsed incoming='" + incoming + "'");
                        // Try direct enum name first
                        try {
                            com.tuempresa.fitboost.model.UserRole ur = com.tuempresa.fitboost.model.UserRole.valueOf(incoming.toUpperCase());
                            user.setRoleEnum(ur);
                        } catch (IllegalArgumentException ex) {
                            // Accept some friendly aliases (Spanish labels) or display names
                            String rUpper = incoming.toUpperCase();
                            if (rUpper.equals("GESTOR") || rUpper.equals("GESTOR_DE_PEDIDOS") || rUpper.equals("ORDER_MANAGER")) {
                                user.setRoleEnum(com.tuempresa.fitboost.model.UserRole.ORDER_MANAGER);
                            } else if (rUpper.equals("ADMIN")) {
                                user.setRoleEnum(com.tuempresa.fitboost.model.UserRole.ADMIN);
                            } else if (rUpper.equals("CLIENTE")) {
                                user.setRoleEnum(com.tuempresa.fitboost.model.UserRole.CLIENTE);
                            } else {
                                // As a last resort try to match by displayName
                                boolean found = false;
                                for (com.tuempresa.fitboost.model.UserRole candidate : com.tuempresa.fitboost.model.UserRole.values()) {
                                    if (candidate.getDisplayName().toUpperCase().equals(incoming.toUpperCase())) {
                                        user.setRoleEnum(candidate);
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) return ResponseEntity.badRequest().body("Invalid role");
                            }
                        }

                        userRepository.save(user);
                        return ResponseEntity.ok().build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(500).body("Server error");
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/admin/users/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserApi(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
