package com.foodly.foodlyapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceDto {
    
    /**
     * Process instance ID
     */
    private String id;
    
    /**
     * Process definition ID
     */
    private String processDefinitionId;
    
    /**
     * Process definition key (foodOrderProcess)
     */
    private String processDefinitionKey;
    
    /**
     * Süreç başlangıç zamanı
     */
    private Date startTime;
    
    /**
     * Süreç bitiş zamanı (sadece tamamlanmış süreçlerde)
     */
    private Date endTime;
    
    /**
     * Süreç durumu (active/completed)
     */
    private String status;
    
    /**
     * Süreç değişkenleri (orderId, userId, approved vb.)
     */
    private Map<String, Object> variables;
    
    /**
     * Süreç sonlanma nedeni (rejected, cancelled vb.)
     */
    private String deleteReason;
    
    /**
     * Process definition name (human readable)
     */
    private String processDefinitionName;
}
