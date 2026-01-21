package com.kijinkai.domain.shipment.entity;


import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "shipment_box_item")  // 나중에 복수형으로 변경
@Entity
public class ShipmentBoxItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_id", nullable = false)
    private Long boxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_entity", nullable = false)
    private ShipmentEntity shipmentEntity;

    @Column(name = "order_item_code", nullable = false, updatable = false)
    private String orderItemCode;


    @Column(name = "quantity", nullable = false)
    private int quantity;
}
