package com.kijinkai.domain.orderitem.validator;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.exception.OrderItemValidateException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Component
public class OrderItemValidator {

    public void validateCreateRequest(String userUuid, OrderItemRequestDto dto) {
        if (dto.getQuantity() <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        if (dto.getPriceOriginal().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        if (dto.getProductLink() == null || dto.getProductLink().isBlank())
            throw new IllegalArgumentException("상품 링크는 필수입니다.");
        // 추가 유효성 검사 가능
    }

    public void validateCustomerOwnershipOfOrderItem(Customer customer, OrderItem orderItem){
        UUID orderCustomerUuid = orderItem.getOrder().getCustomer().getCustomerUuid();
        if (!customer.getCustomerUuid().equals(orderCustomerUuid)) {
            throw new OrderItemValidateException(
                    String.format("Customer UUID mismatch: expected %s, but got %s",
                            customer.getCustomerUuid(), orderCustomerUuid)
            );
        }
    }
}
