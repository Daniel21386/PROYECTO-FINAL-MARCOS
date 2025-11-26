package com.tuempresa.fitboost.controller;

import com.tuempresa.fitboost.model.User;
import com.tuempresa.fitboost.model.UserRole;
import com.tuempresa.fitboost.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", UserRole.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, 
                                 @RequestParam String role,
                                 RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            try {
                user.setRoleEnum(UserRole.valueOf(role.toUpperCase()));
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("success", 
                    "Rol de " + user.getUsername() + " actualizado a " + role);
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Rol inválido");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        }
        
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String username = user.getUsername();
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario " + username + " eliminado");
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        }
        
        return "redirect:/admin/users";
    }

    // ========== API Endpoints para Dashboard ==========

    @GetMapping("/api/stats/users")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", userRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/api/stats/orders-today")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrdersToday() {
        Map<String, Object> stats = new HashMap<>();
        // Por ahora retorna un valor fijo, se puede conectar con OrderRepository
        stats.put("count", 47);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/api/stats/monthly-sales")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonthlySales() {
        Map<String, Object> stats = new HashMap<>();
        // Por ahora retorna un valor fijo, se puede conectar con OrderRepository
        stats.put("total", 12540);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/api/stats/low-stock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLowStock() {
        Map<String, Object> stats = new HashMap<>();
        // Por ahora retorna un valor fijo, se puede conectar con ProductRepository
        stats.put("count", 12);
        return ResponseEntity.ok(stats);
    }

}
