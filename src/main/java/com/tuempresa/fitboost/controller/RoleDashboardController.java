package com.tuempresa.fitboost.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoleDashboardController {

    @GetMapping("/dashboard-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboardAdmin(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "ADMIN");
        return "dashboard/admin";
    }

    @GetMapping("/dashboard-cliente")
    @PreAuthorize("hasRole('CLIENTE')")
    public String dashboardCliente(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "CLIENTE");
        return "dashboard/cliente";
    }
}
