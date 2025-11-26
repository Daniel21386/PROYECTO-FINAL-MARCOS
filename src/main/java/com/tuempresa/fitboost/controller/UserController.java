package com.tuempresa.fitboost.controller;

import com.tuempresa.fitboost.dto.RegisterForm;
import com.tuempresa.fitboost.model.User;
import com.tuempresa.fitboost.model.UserRole;
import com.tuempresa.fitboost.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/register")
    public String register(@Valid RegisterForm form, BindingResult bindingResult) {
        // Validación básica de campos (anotaciones en DTO)
        if (bindingResult.hasErrors()) {
            return "redirect:/register?error=invalid_input";
        }

        // Validar que las contraseñas coincidan
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            return "redirect:/register?error=password_mismatch";
        }

        // Validar que el usuario no exista
        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            return "redirect:/register?error=user_exists";
        }

        // Validar que el email no exista
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            return "redirect:/register?error=email_exists";
        }

        // Crear nuevo usuario - SIEMPRE como CLIENTE
        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRoleEnum(UserRole.CLIENTE); // Solo admins pueden cambiar esto
        
        userRepository.save(user);
        
        return "redirect:/login?success=registered";
    }

    @PostMapping("/register/json")
    public ResponseEntity<?> registerJson(@Valid @RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleEnum(UserRole.CLIENTE); // Solo admins pueden cambiar esto
        userRepository.save(user);
        return ResponseEntity.ok("Usuario registrado");
    }
}