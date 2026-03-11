package com.kijinkai.domain.orderitem.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;


@Getter
@Builder
public class ArrivedItemRequestDto {

    List<UUID> orderItemUuids;
}
