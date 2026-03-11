package com.kijinkai.domain.shipment.entity;


import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "shipment_box_items")
@Entity
public class ShipmentBoxItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_id", nullable = false)
    private Long boxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_entity", nullable = false)
    private ShipmentEntity shipmentEntity;

    @Column(name = "order_item_uuid", nullable = false, updatable = false)
    private UUID oderItemUuid;


    @Column(name = "order_item_code", nullable = false, updatable = false)
    private String orderItemCode;


    @Column(name = "quantity", nullable = false)
    private int quantity;
}
