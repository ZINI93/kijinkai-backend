package com.kijinkai.domain.delivery.adpater.out.persistence.repository;


import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DeliverySearchCondition {

    private String name;
    private String phoneNumber;
    private DeliveryStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
