package com.kijinkai.domain.orderitem.domain.scheduler;


import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.port.in.CreateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderItemScheduler {

    private final OrderItemPersistencePort orderItemPersistencePort;
    private final UpdateOrderItemUseCase updateOrderItemUseCase;
    private final CreateOrderItemUseCase createOrderItemUseCase;


    @Scheduled(cron = "0 0 3 * * *") // 1시간 간격
    public void autoStoragePeriodCancel(){

        // 무료 보관기간 30일
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);

        // 무료 보관기간 초과
        List<OrderItem> targets = orderItemPersistencePort
                .findAllByStatusAndLocalArriveAtBefore(OrderItemStatus.LOCAL_ORDER_COMPLETED, threshold);

        List<OrderItem> successItems = new ArrayList<>();

        // 상태변환
        for (OrderItem item : targets) {
            try{

                // 상태변화 및 이메일 전송
                updateOrderItemUseCase.processStoragePeriodExceeded(item);

                successItems.add(item);
            }catch (Exception e){
                log.error("아이템 {} 처리 중 오류 발생, 스킵합니다.", item.getOrderItemCode());
            }

        }

        if (!successItems.isEmpty()){
            createOrderItemUseCase.saveOrderItems(successItems);
            log.info("{}건의 주문이 24시간 경과로 인해 자동 통합진행으로 전환되었습니다.", successItems.size());
        }
    }
}

