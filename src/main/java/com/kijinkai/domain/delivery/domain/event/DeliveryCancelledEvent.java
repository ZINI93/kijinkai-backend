package com.kijinkai.domain.delivery.domain.event;

import java.util.UUID;

public record DeliveryCancelledEvent(UUID deliveryUuid) {
}
