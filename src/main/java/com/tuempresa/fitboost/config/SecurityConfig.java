package com.tuempresa.fitboost.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Rutas públicas
                .requestMatchers("/login", "/register", "/welcome", "/nosotros", "/productos", 
                                "/static/**", "/css/**", "/js/**", "/imagenes/**", "/javascript/**", 
                                "/api/users/register", "/orders/create").permitAll()
                // Rutas solo para ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Rutas para CLIENTE (cualquier usuario autenticado)
                .requestMatchers("/cliente/**", "/orders/**").authenticated()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // En producción usaremos usuarios persistidos. Mantengo el PasswordEncoder bean.
    // El UserDetailsService basado en JPA se registra como un servicio (`CustomUserDetailsService`).

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
