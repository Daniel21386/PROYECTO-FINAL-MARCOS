package com.tuempresa.fitboost.config;

import com.tuempresa.fitboost.model.Product;
import com.tuempresa.fitboost.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setName("Proteína Whey");
            p1.setCategory("Proteínas");
            p1.setPrice(new BigDecimal("45.99"));
            p1.setStock(120);

            Product p2 = new Product();
            p2.setName("Aminoácidos BCAA");
            p2.setCategory("Aminoácidos");
            p2.setPrice(new BigDecimal("29.50"));
            p2.setStock(85);

            Product p3 = new Product();
            p3.setName("Multivitamínico");
            p3.setCategory("Vitaminas");
            p3.setPrice(new BigDecimal("19.99"));
            p3.setStock(200);

            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);
        }
    }
}
