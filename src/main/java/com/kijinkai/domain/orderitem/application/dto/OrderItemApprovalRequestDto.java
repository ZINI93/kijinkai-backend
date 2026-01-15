package com.kijinkai.domain.orderitem.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class OrderItemApprovalRequestDto {

    List<String> orderItemCodes;
}
