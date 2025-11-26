package com.tuempresa.fitboost.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tuempresa.fitboost.repository.ProductRepository;
import com.tuempresa.fitboost.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductosController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public AdminProductosController(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("titulo", "Gestión de Productos");
        model.addAttribute("products", productRepository.findAll());
        return "admin/productos";
    }

    @GetMapping("/ordenes")
    public String ordenes(Model model) {
        model.addAttribute("titulo", "Gestión de Órdenes");
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/ordenes";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("titulo", "Reportes");
        return "admin/reportes";
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model) {
        model.addAttribute("titulo", "Configuración");
        return "admin/configuracion";
    }
}
