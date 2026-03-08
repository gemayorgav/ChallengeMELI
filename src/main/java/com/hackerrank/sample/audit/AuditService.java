package com.hackerrank.sample.audit;

import com.hackerrank.sample.model.AuditLog;
import com.hackerrank.sample.repository.AuditLogRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio de auditoría para registrar todas las operaciones
 * Incluye resiliencia con Circuit Breaker y Retry
 */
@Service
@Slf4j
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    /**
     * Registra una operación en el log de auditoría
     * Con resiliencia: reintentos y circuit breaker
     */
    @CircuitBreaker(name = "auditService", fallbackMethod = "fallbackAudit")
    @Retry(name = "auditService")
    public void logOperation(String username, String service, String method, 
                            String endpoint, String action, String result, 
                            String statusCode, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(username);
        auditLog.setService(service);
        auditLog.setMethod(method);
        auditLog.setEndpoint(endpoint);
        auditLog.setAction(action);
        auditLog.setResult(result);
        auditLog.setStatusCode(statusCode);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);
        
        auditLogRepository.save(auditLog);
        
        // Log en consola para monitoreo
        String logMessage = String.format(
            "[AUDIT] [%s] [USER:%s] [%s %s] [ACTION:%s] [RESULT:%s] [STATUS:%s] [TIME:%s]",
            service, username, method, endpoint, action, result, statusCode, LocalDateTime.now()
        );
        logger.info(logMessage);
    }
    
    /**
     * Método fallback en caso de que el circuit breaker se abra
     */
    public void fallbackAudit(String username, String service, String method, 
                             String endpoint, String action, String result, 
                             String statusCode, String details, Exception ex) {
        // Log en consola cuando el circuit breaker está abierto
        logger.warn("[AUDIT-FALLBACK] Auditoría no registrada - CircuitBreaker abierto: " + ex.getMessage());
        logger.warn(String.format("[FALLBACK] [%s] [USER:%s] [%s %s] [ACTION:%s] [RESULT:%s]", 
            service, username, method, endpoint, action, result));
    }
}
