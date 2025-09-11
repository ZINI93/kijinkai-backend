package com.kijinkai.domain.orderitem.validator;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.exception.OrderItemValidateException;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderItemValidator {

    private final OrderItemRepository orderItemRepository;

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

    public void requiredPendingStatus(OrderItem orderItem){
        if (orderItem.getOrderItemStatus() != OrderItemStatus.PENDING) {
            throw new OrderItemValidateException(
                    "Order item cannot be pending; it must be in PENDING status"
            );
        }
    }

    /**
     * 주문 아이템 유효성 검사
     */
    public List<OrderItem> validateOrderItems(UUID customerUuid, List<UUID> orderItemUuids, OrderItemStatus orderItemStatus) {

        if (orderItemUuids == null || orderItemUuids.isEmpty()) {
            throw new IllegalArgumentException("결제할 주문 아이템을 선택해주세요.");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderItemUuidInAndCustomerUuid(orderItemUuids, customerUuid);

        // 요청한 수량과 조회된 수량 비교
        if (orderItems.size() != orderItemUuids.size()) {
            log.warn("요청된 주문 아이템과 조회된 주문 아이템 수가 다름 - 요청: {}, 조회: {}",
                    orderItemUuids.size(), orderItems.size());
            throw new IllegalArgumentException("일부 주문 아이템을 찾을 수 없습니다.");
        }

        // 결제 가능한 상태인지 확인 (PENDING_APPROVAL 상태만 결제 가능)
        List<OrderItem> invalidItems = orderItems.stream()
                .filter(item -> item.getOrderItemStatus() != orderItemStatus)
                .toList();

        if (!invalidItems.isEmpty()) {
            log.warn("결제 불가능한 상태의 주문 아이템 발견 - 개수: {}", invalidItems.size());
            throw new IllegalStateException("결제할 수 없는 상태의 주문이 포함되어 있습니다.");
        }

        return orderItems;
    }
}
