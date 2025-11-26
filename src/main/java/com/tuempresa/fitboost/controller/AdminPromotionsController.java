package com.tuempresa.fitboost.controller;

import com.tuempresa.fitboost.model.Promotion;
import com.tuempresa.fitboost.repository.PromotionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminPromotionsController {

    private final PromotionRepository promotionRepository;

    public AdminPromotionsController(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @GetMapping("/api/promotions")
    public List<Promotion> getPromotions() {
        return promotionRepository.findByActiveTrue();
    }

    @PostMapping("/api/admin/promotions")
    @PreAuthorize("hasRole('ADMIN')")
    public Promotion createPromotion(@RequestParam String title,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) String imageUrl,
                                     @RequestParam(required = false) Double discount,
                                     @RequestParam(required = false, defaultValue = "true") boolean active) {

        Promotion p = new Promotion();
        p.setTitle(title);
        p.setDescription(description);
        p.setImageUrl(imageUrl);
        p.setDiscount(discount);
        p.setActive(active);
        return promotionRepository.save(p);
    }

    @DeleteMapping("/api/admin/promotions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePromotion(@PathVariable Long id) {
        return promotionRepository.findById(id)
                .map(p -> {
                    promotionRepository.delete(p);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
