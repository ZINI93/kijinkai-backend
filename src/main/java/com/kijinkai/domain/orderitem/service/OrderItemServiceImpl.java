package com.kijinkai.domain.orderitem.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.exchange.calculator.CurrencyExchangeCalculator;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.exception.OrderNotFoundException;
import com.kijinkai.domain.order.validator.OrderValidator;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.factory.OrderItemFactory;
import com.kijinkai.domain.orderitem.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.orderitem.validator.OrderItemValidator;
import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.exception.PaymentProcessingException;
import com.kijinkai.domain.platform.entity.Platform;
import com.kijinkai.domain.platform.exception.PlatformNotFoundException;
import com.kijinkai.domain.platform.repository.PlatformRepository;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.exchange.repository.ExchangeRateRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final CustomerRepository customerRepository;
    private final PlatformRepository platformRepository;
    private final OrderItemRepository orderItemRepository;

    private final ExchangeRateService exchangeRateService;

    private final OrderItemValidator orderItemValidator;
    private final OrderValidator orderValidator;
    private final UserValidator userValidator;
    private final OrderItemFactory orderItemFactory;
    private final OrderItemMapper orderItemMapper;

    private final CurrencyExchangeCalculator exchangeCalculator;


    /**
     * 유저로 부터 상품 url, quantity 를 입력 받고, 확인 후 관리자가 가격 수정
     * @param customer
     * @param order
     * @param requestDto
     * @return orderItem
     */
    @Override
    @Transactional
    public OrderItem createOrderItem(Customer customer, Order order, OrderItemRequestDto requestDto) {

        Platform platform = findPlatformByPlatformUuid(requestDto.getPlatformUuid());
        BigDecimal priceOriginal = requestDto.getPriceOriginal();
        BigDecimal exchangeRate = getLatestExchangeRate(Currency.JPY, requestDto.getCurrencyConverted());
        BigDecimal convertedAmount = exchangeCalculator.calculateConvertedAmount(priceOriginal, exchangeRate);
        exchangeCalculator.validateConverterAmount(priceOriginal, exchangeRate);

        return orderItemFactory.createOrderItem(customer, platform, order, convertedAmount, exchangeRate, requestDto);
    }


    /**
     * 유저가 견적서 작성으로 넘어가기 전에 url을 수정 가능하게 하는 프로세스
     * @param orderUuid
     * @param updateDto
     * @return
     */
    @Override
    @Transactional
    public OrderItem updateOrderItemWithValidate(String userUuid, String orderItemUuid, OrderItemUpdateDto updateDto) {

        Customer customer = findCustomerByUserUuid(userUuid);
        OrderItem orderItem = findOrderItemByCustomerAndOrderItemUuid(customer, orderItemUuid);
        orderValidator.requireDraftOrderStatus(orderItem.getOrder());

        Platform platform = findPlatformByPlatformUuid(updateDto.getPlatformUuid());
        orderItem.updateOrderItem(updateDto, platform);

        return orderItem;
    }

    /**
     * 관리자가 주문 수정 - 유저 전체의 상품 update 가능
     *
     * @param userUuid
     * @param orderUuid
     * @param updateDto
     * @return
     */
    @Override
    @Transactional
    public OrderItem updateOrderItemByAdmin(String userUuid, String orderItemUuid, OrderItemUpdateDto updateDto) {
        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());

        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);

        Platform platform = findPlatformByPlatformUuid(updateDto.getPlatformUuid());

        orderItem.updateOrderItem(updateDto, platform);

        return orderItem;
    }

    private OrderItem findOrderItemByOrderItemUuid(String orderItemUuid) {
        return orderItemRepository.findByOrderItemUuid(UUID.fromString(orderItemUuid))
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("Order item not found for order item uuid: %s", orderItemUuid)));
    }


    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItemInfo(String userUuid, String orderItemUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        OrderItem orderItem = findOrderItemByCustomerAndOrderItemUuid(customer, orderItemUuid);

        return orderItemMapper.toResponseDto(orderItem);
    }

    private Platform findPlatformByPlatformUuid(String platformUuid) {
        return platformRepository.findByPlatformUuid(UUID.fromString(platformUuid))
                .orElseThrow(() -> new PlatformNotFoundException(String.format("Platform not found for platform uuid: %s", platformUuid)));
    }

    private OrderItem findOrderItemByCustomerAndOrderItemUuid(Customer customer, String orderItemUuid) {
        return orderItemRepository.findByCustomerUuidAndOrderItemUuid(UUID.fromString(customer.getUser().getUserUuid()), UUID.fromString(orderItemUuid))
                .orElseThrow(() -> new OrderNotFoundException(String.format("OrderItem not found for customer uuid: %s and order uuid: %s", customer.getCustomerUuid(), orderItemUuid)));
    }

    private Customer findCustomerByUserUuid(String userUuid) {
        return customerRepository.findByUserUserUuid(UUID.fromString(userUuid))
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

    /**
     * ExchangeRateService를 통해 최신 환율을 조회합니다.
     * 환율이 없는 경우 (예: 아직 API 호출 전 또는 실패)에 대한 견고한 처리 필요.
     *
     * @param fromCurrency 기준 통화 (예: USD)
     * @param toCurrency   대상 통화 (예: KRW)
     * @return 최신 환율 (BigDecimal)
     * @throws RuntimeException 환율 정보를 찾을 수 없을 경우
     */
    private BigDecimal getLatestExchangeRate(Currency fromCurrency, Currency toCurrency) {

        return exchangeRateService.getLatestExchangeRate(fromCurrency, toCurrency)
                .map(ExchangeRate::getRate) // ExchangeRate 엔티티에서 rate 값 추출
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Latest exchange rate for %s to %s not found. Please ensure exchange rate update is working.",
                                fromCurrency, toCurrency)));
    }

}
