package com.foodly.foodlyapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    
    /**
     * Task ID (task'ı tamamlamak için gerekli)
     */
    private String id;
    
    /**
     * Task adı (reviewOrder, prepareOrder, shipOrder)
     */
    private String name;
    
    /**
     * Task açıklaması
     */
    private String description;
    
    /**
     * Atanan kişi (chef, kitchen, courier)
     */
    private String assignee;
    
    /**
     * Task oluşturulma zamanı
     */
    private Date createTime;
    
    /**
     * Process Instance ID
     */
    private String processInstanceId;
}
