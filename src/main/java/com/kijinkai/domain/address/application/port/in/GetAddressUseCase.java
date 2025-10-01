package com.kijinkai.domain.address.application.port.in;

import com.kijinkai.domain.address.application.dto.AddressResponseDto;

import java.util.UUID;

public interface GetAddressUseCase {
    AddressResponseDto getAddressInfo(UUID userUuid);

}
