package com.hackerrank.sample.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de OpenAPI 3.0 / Swagger para documentación automática de API.
 * 
 * Proporciona:
 * - Información completa de la API
 * - Esquema de seguridad JWT
 * - Servidores disponibles
 * - Documentación profesional
 * 
 * Acceso a Swagger UI:
 * http://localhost:8080/swagger-ui.html
 * 
 * Acceso a OpenAPI JSON:
 * http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Información de la API
                .info(new Info()
                        .title("MercadoLibre ItemDetail API")
                        .description("""
                                API REST profesional para gestión completa de productos/modelos.
                                
                                **Características:**
                                - Autenticación JWT (HS512)
                                - Auditoría completa de operaciones
                                - Resiliencia con Circuit Breaker + Retry
                                - Validación exhaustiva de datos
                                - Health Checks y Métricas
                                
                                **Ambiente:** Development/Production
                                **Versión API:** v1
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("dev-team@meli.com")
                                .url("https://www.mercadolibre.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                
                // Servidores
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local"),
                        new Server()
                                .url("http://api.meli.dev:8080")
                                .description("Ambiente Desarrollo"),
                        new Server()
                                .url("https://api.meli.prod:8080")
                                .description("Ambiente Producción")))
                
                // Seguridad - JWT Bearer Token
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .name("Authorization")
                                        .description("JWT Bearer token obtenido del endpoint /api/v1/auth/login")
                                        .in(SecurityScheme.In.HEADER)))
                
                // Aplicar seguridad globalmente
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
