package com.hackerrank.sample.config;

import com.hackerrank.sample.model.User;
import com.hackerrank.sample.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración e inicialización de datos de la aplicación
 */
@Configuration
@Slf4j
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Crea usuario de prueba al iniciar la aplicación
     */
    @Bean
    public CommandLineRunner initializeUsers() {
        return args -> {
            try {
                // Verificar si el usuario ya existe
                if (userRepository.findByUsername("testuser").isEmpty()) {
                    User testUser = new User();
                    testUser.setUsername("testuser");
                    testUser.setPassword("password123");
                    testUser.setRole("USER");
                    testUser.setEnabled(true);
                    
                    userRepository.save(testUser);
                    logger.info("[INIT] Usuario de prueba creado: testuser");
                } else {
                    logger.info("[INIT] Usuario de prueba ya existe");
                }
                
                // Crear usuario admin de prueba
                if (userRepository.findByUsername("admin").isEmpty()) {
                    User adminUser = new User();
                    adminUser.setUsername("admin");
                    adminUser.setPassword("admin123");
                    adminUser.setRole("ADMIN");
                    adminUser.setEnabled(true);
                    
                    userRepository.save(adminUser);
                    logger.info("[INIT] Usuario admin creado: admin");
                } else {
                    logger.info("[INIT] Usuario admin ya existe");
                }
                
                logger.info("[INIT] Base de datos inicializada correctamente");
                
            } catch (Exception e) {
                logger.error("[INIT] Error inicializando usuarios: {}", e.getMessage());
            }
        };
    }
}
