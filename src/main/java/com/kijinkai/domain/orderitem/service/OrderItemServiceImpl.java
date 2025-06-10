package com.kijinkai.domain.orderitem.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.order.dto.OrderUpdateDto;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.exception.OrderNotFoundException;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.factory.OrderItemFactory;
import com.kijinkai.domain.orderitem.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.orderitem.validator.OrderItemValidator;
import com.kijinkai.domain.platform.entity.Platform;
import com.kijinkai.domain.platform.exception.PlatformNotFoundException;
import com.kijinkai.domain.platform.repository.PlatformRepository;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.exchange.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PlatformRepository platformRepository;
    private final OrderItemRepository orderItemRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final OrderItemValidator validator;
    private final OrderItemFactory orderItemFactory;
    private final OrderItemMapper orderItemMapper;



    @Override
    public OrderItem createOrderItemProcess(Customer customer, Order order, OrderItemRequestDto requestDto) {

        Platform platform = platformRepository.findByPlatformUuid(requestDto.getPlatformUuid())
                .orElseThrow(() -> new PlatformNotFoundException("PlatformUuid: Platform not found"));

        OrderItem orderItem = orderItemFactory.createOrderItem(customer, platform, order, requestDto);

        return orderItem;
    }

    private void updateOrderItem(OrderItem orderItem, OrderItemUpdateDto updateDto){

        Platform platform = platformRepository.findByPlatformUuid(updateDto.getPlatformUuid())
                .orElseThrow(() -> new PlatformNotFoundException("Platform Uuid: platform not found"));

        orderItem.updateOrderItem(
                platform,
                updateDto.getProductLink(),
                updateDto.getQuantity(),
                updateDto.getMemo(),
                updateDto.getPriceOriginal(),
                updateDto.getCurrencyConverted()
        );

    }

    @Override
    public OrderItem updateOrderItemWithValidate(String orderUuid,  OrderItemUpdateDto updateDto) {
        OrderItem orderItem = orderItemRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new OrderNotFoundException(""));

        updateOrderItem(orderItem,updateDto);
        return orderItem;
    }

    @Override @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItemInfo(String userUuid, String orderItemUuid) {

        Customer customer = customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("UserUuid: Customer not found"));
        OrderItem orderItem = orderItemRepository.findByCustomerUuidAndOrderItemUuid(customer.getCustomerUuid(), orderItemUuid)
                .orElseThrow(() -> new OrderItemNotFoundException("Customer Uuid and OrderItem Uuid : Order not found"));
        return orderItemMapper.toResponseDto(orderItem);
    }
}
