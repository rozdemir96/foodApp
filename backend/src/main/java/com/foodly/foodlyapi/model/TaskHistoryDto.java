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
public class TaskHistoryDto {
    
    /**
     * Task ID
     */
    private String id;
    
    /**
     * Task adı (reviewOrder, prepareOrder, shipOrder)
     */
    private String name;
    
    /**
     * Atanan kişi (chef, kitchen, courier)
     */
    private String assignee;
    
    /**
     * Task başlangıç zamanı
     */
    private Date startTime;
    
    /**
     * Task bitiş zamanı
     */
    private Date endTime;
    
    /**
     * Task süresi (milisaniye)
     */
    private Long durationInMillis;
}
