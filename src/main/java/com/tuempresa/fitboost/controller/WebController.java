package com.tuempresa.fitboost.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("timestamp", System.currentTimeMillis());
        return "login";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        return "nosotros";
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        return "productos";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }
}
