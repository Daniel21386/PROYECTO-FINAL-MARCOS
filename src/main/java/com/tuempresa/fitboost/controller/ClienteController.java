package com.tuempresa.fitboost.controller;

import com.tuempresa.fitboost.model.User;
import com.tuempresa.fitboost.repository.OrderRepository;
import com.tuempresa.fitboost.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/compras")
    public String compras(Authentication authentication, Model model) {
        model.addAttribute("titulo", "Mis Compras");
        
        // Obtener el usuario actual y sus órdenes
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            model.addAttribute("orders", orderRepository.findByUserOrderByOrderDateDesc(user));
        }
        return "cliente/compras";
    }

    @GetMapping("/carrito")
    public String carrito(Model model) {
        model.addAttribute("titulo", "Mi Carrito");
        return "cliente/carrito";
    }

    @GetMapping("/favoritos")
    public String favoritos(Model model) {
        model.addAttribute("titulo", "Mis Favoritos");
        return "cliente/favoritos";
    }

    @GetMapping("/perfil")
    public String perfil(Authentication authentication, Model model) {
        model.addAttribute("titulo", "Mi Perfil");
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            model.addAttribute("user", user);
        }
        return "cliente/perfil";
    }

    @PostMapping("/perfil")
    public String updatePerfil(@RequestParam(required = false) String firstName,
                               @RequestParam(required = false) String lastName,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String newPassword,
                               @RequestParam(required = false) String confirmPassword,
                               @RequestParam(required = false) MultipartFile profilePhoto,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos básicos
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);

        // Cambio de contraseña si se proporcionó y coincide
        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/cliente/perfil";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        // Subir foto si existe
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/imagenes/usuarios/";
                Files.createDirectories(Paths.get(uploadDir));
                String original = profilePhoto.getOriginalFilename();
                String ext = "";
                if (original != null && original.contains(".")) {
                    ext = original.substring(original.lastIndexOf('.'));
                }
                String filename = UUID.randomUUID().toString() + ext;
                Path filePath = Paths.get(uploadDir).resolve(filename);
                profilePhoto.transferTo(filePath.toFile());
                // Guardar ruta pública para servir desde /imagenes/usuarios/
                user.setProfilePhoto("/imagenes/usuarios/" + filename);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Error subiendo la imagen");
                return "redirect:/cliente/perfil";
            }
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        return "redirect:/cliente/perfil";
    }

    @PostMapping("/perfil/delete")
    public String deleteAccount(Authentication authentication,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Borrar imagen de perfil si existe
        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isBlank()) {
            try {
                String publicPath = user.getProfilePhoto(); // e.g. /imagenes/usuarios/uuid.jpg
                String relative = publicPath.startsWith("/") ? publicPath.substring(1) : publicPath;
                Path filePath = Paths.get("src/main/resources/static").resolve(relative);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // No bloqueamos la eliminación si falla borrar el archivo
            }
        }

        // Eliminar usuario
        userRepository.delete(user);

        // Invalidar sesión y limpiar contexto de seguridad
        try {
            request.getSession().invalidate();
        } catch (Exception ignored) {}
        SecurityContextHolder.clearContext();

        redirectAttributes.addFlashAttribute("success", "Cuenta eliminada correctamente");
        return "redirect:/";
    }
}

